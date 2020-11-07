package com.dkm.isaaccommunity.service.impl;


import com.dkm.isaaccommunity.bean.Item;
import com.dkm.isaaccommunity.dao.TestDao;
import com.dkm.isaaccommunity.service.TestService;
import net.semanticmetadata.lire.builders.DocumentBuilder;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.searchers.GenericFastImageSearcher;
import net.semanticmetadata.lire.searchers.ImageSearchHits;
import net.semanticmetadata.lire.searchers.ImageSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;

    private String preAddr = "/images/";
    private String png = ".png";



    @Override
    public void showScore(String filePath) throws IOException{
        BufferedImage img = null;
        boolean passed = false;
        //URL imgPath = c.getClassLoader().getResource("toSearcher/"+toSearcherFileName);
        File f = new File(filePath);
        if (f.exists()) {
            try {
                System.out.println("111");
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
        ImageSearcher searcher = new GenericFastImageSearcher(30, CEDD.class);
        ImageSearchHits hits = searcher.search(img, ir);

        for (int i = 0; i < hits.length(); i++) {
            String fileName = ir.document(hits.documentID(i)).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits.score(i) + ": \t" + fileName);
        }
    }

    @Override
    public List<Item> showAllItems() {
        List<Item> items = testDao.selectAllItem();
        for(Item item : items){
            item.setImgAddr(preAddr + item.getImgName() + png);
        }
        return items;
    }
}
