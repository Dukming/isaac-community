package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Item;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.service.ItemService;
import com.dkm.isaaccommunity.util.FileUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/item")
@RequiresRoles(value = {"2"})
public class ItemController {
    @Autowired
    private ItemService itemService;


    @RequestMapping(value = "/showAll", method = RequestMethod.GET)
    public String showAll(Model model){
        List<Item> items = itemService.showAllItems();
        model.addAttribute("items", items);
        return "item/showItem";
    }

    @RequestMapping(value = "/showItem", method = RequestMethod.GET)
    public String searcher(Model model,
                           @RequestParam("itemNameOrTagName") String itemNameOrTagName,
                           @RequestParam("typeName") String typeName){
        List<Item> items = itemService.showItemByNameOrTagAndType(itemNameOrTagName, typeName);
        model.addAttribute("items", items);
        return "item/showItem";
    }

    @RequestMapping(value = "/tagSelect", method = RequestMethod.GET)
    public String tagSelect(Model model, @PathParam("tagName") String tagName){
        List<Item> items = itemService.showItemByTag(tagName);
        model.addAttribute("items", items);
        return "item/showItem";
    }

    @ResponseBody
    @RequestMapping(value = "/imgSelect", method = RequestMethod.POST)
    public List<Item> imgSelect(HttpServletRequest request){
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multiRequest.getFile("file");

        // 拿到文件名
        String filename = file.getOriginalFilename();

        //判断上传文件格式
        String fileType = file.getContentType();

        // 存放上传图片的文件夹
        File fileDir = FileUtil.getImgDirFile(GlobalConstant.TO_SEARCHER_IMG_PATH_PREFIX);

        List<Item> items = new ArrayList<>();

        if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpeg")) {
            try {
                // 构建真实的文件路径
                String localPath = fileDir.getAbsolutePath() + File.separator + filename;
                File newFile = new File(localPath);
                //System.out.println(newFile.getAbsolutePath());

                // 上传图片到 -》 “绝对路径”
                file.transferTo(newFile);
                try {
                    items = itemService.showItemByImg(localPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(newFile.exists()&&newFile.isFile())
                    newFile.delete();
                return items;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return items;

        }else{
            return items;
        }
    }


}
