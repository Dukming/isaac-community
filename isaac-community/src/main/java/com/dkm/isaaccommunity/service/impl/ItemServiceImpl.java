package com.dkm.isaaccommunity.service.impl;

import com.dkm.isaaccommunity.bean.Item;
import com.dkm.isaaccommunity.bean.Tag;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.dao.ItemDao;
import com.dkm.isaaccommunity.dao.TagDao;
import com.dkm.isaaccommunity.service.ItemService;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private TagDao tagDao;

    @Override
    @Cacheable(value = "redisCacheManager")
    public List<Item> showAllItems() {
        List<Item> items = itemDao.selectAllItem();
        for(Item item : items){
            item.setImgAddr(GlobalConstant.ITEM_PRE_ADDR + item.getImgName() + GlobalConstant.PNG);
        }
        return items;
    }

    @Override
    public List<Item> showItemByNameOrTagAndType(String itemNameOrTagName, String typeName) {
        List<Item> items = itemDao.selectItemByNameAndType(itemNameOrTagName,itemNameOrTagName,typeName);
        for(Item item : items){
            item.setImgAddr(GlobalConstant.ITEM_PRE_ADDR + item.getImgName() + GlobalConstant.PNG);
        }
        return items;
    }

    @Override
    public List<Item> showItemByTag(String tagNme) {
        List<Item> items = itemDao.selectItemByTagName(tagNme);
        for(Item item : items){
            item.setImgAddr(GlobalConstant.ITEM_PRE_ADDR + item.getImgName() + GlobalConstant.PNG);
        }
        return items;
    }

    @Override
    public List<Item> showItemByImg(String filePath) throws IOException {
        BufferedImage img = null;
        boolean passed = false;
        File f = new File(filePath);
        if (f.exists()) {
            try {
                img = ImageIO.read(f);
                passed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!passed) {
            System.out.println("No image given as first argument.");
            System.out.println("Run \"Searcher <query image>\" to search for <query image>.");
            System.exit(1);
        }


        IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        ImageSearcher searcher = new GenericFastImageSearcher(10, CEDD.class);
        ImageSearchHits hits = searcher.search(img, ir);

        List<Item> items = new ArrayList<>();

        for (int i = 0; i < hits.length(); i++) {
            String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            String[] imgPath = fileName.split("\\\\");
            String imgName = imgPath[imgPath.length-1].split("\\.")[0];
            items.add(itemDao.selectItemByImgName(imgName));
        }
        for(Item item : items){
            item.setImgAddr(GlobalConstant.ITEM_PRE_ADDR + item.getImgName() + GlobalConstant.PNG);
        }
        return items;
    }

    @Override
    public Item showItemByID(Integer id) {
        return itemDao.getItem(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public boolean updateItem(Item itemParams) {
        Item item = new Item();
        item.setId(itemParams.getId());
        item.setName(itemParams.getName());
        item.setTypeName(itemParams.getTypeName());
        item.setDescription(itemParams.getDescription());
        item.setImgName(itemDao.getItem(itemParams.getId()).getImgName());
        if(itemDao.updateItem(item) <= 0){
            return false;
        }
        tagDao.deleteMapByii(item.getId());
        for(Tag tag : itemParams.getTags()){
            Integer tagID = tagDao.getTag(tag.getName()).getId();
            if(tagDao.insertMap(item.getId(), tagID) <= 0){
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public boolean registItem(Item itemParams) {
        Item item = new Item();
        item.setName(itemParams.getName());
        item.setImgName(itemParams.getImgName());
        item.setTypeName(itemParams.getTypeName());
        item.setDescription(itemParams.getDescription());
        if(itemDao.insertItem(item) <= 0){
            return false;
        }
        for(Tag tag : itemParams.getTags()){
            Integer tagID = tagDao.getTag(tag.getName()).getId();
            if(tagDao.insertMap(item.getId(), tagID) <= 0){
                return false;
            }
        }
        return true;
    }


}
