package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Item;
import com.dkm.isaaccommunity.bean.Tag;
import com.dkm.isaaccommunity.service.ItemService;
import com.dkm.isaaccommunity.service.TagService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/adminItem")
@RequiresRoles(value = {"1"})
public class ItemAdminController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TagService tagService;

    @RequestMapping(value = "/showAll", method = RequestMethod.GET)
    public String showAll(Model model){
        List<Item> items = itemService.showAllItems();
        model.addAttribute("items", items);
        return "adminItem/adminShowItem";
    }

    @RequestMapping(value = "/showItem", method = RequestMethod.GET)
    public String searcher(Model model,
                           @RequestParam("itemNameOrTagName") String itemNameOrTagName,
                           @RequestParam("typeName") String typeName){
        List<Item> items = itemService.showItemByNameOrTagAndType(itemNameOrTagName, typeName);
        model.addAttribute("items", items);
        return "adminItem/adminShowItem";
    }

    @RequestMapping(value = "/tagSelect", method = RequestMethod.GET)
    public String tagSelect(Model model, @PathParam("tagName") String tagName){
        List<Item> items = itemService.showItemByTag(tagName);
        model.addAttribute("items", items);
        return "adminItem/adminShowItem";
    }

    @RequestMapping(value = "/edit",method = RequestMethod.GET )
    public String edit(Model model, @PathParam("id") Integer id){
        Item item = itemService.showItemByID(id);
        List<Tag> tags = tagService.showAllTags();
        List<String> itemTagNames = new ArrayList<>();
        for(Tag tag : item.getTags()){
            itemTagNames.add(tag.getName());
        }
        model.addAttribute("item", item);
        model.addAttribute("itemTagNames", itemTagNames);
        model.addAttribute("tags",tags);
        return "adminItem/adminEditItem";
    }

    @ResponseBody
    @RequestMapping(value = "/updateItem",method = RequestMethod.POST )
    public String updateItem(@ModelAttribute Item item, @RequestParam("tagSelect") String[] tagSelect){
        List<Tag> tags = new ArrayList<>();
        for(String tagName : tagSelect){
            if("".equals(tagName)){
                continue;
            }
            Tag tag = new Tag();
            tag.setName(tagName);
            tags.add(tag);
        }
        item.setTags(tags);
        if(itemService.updateItem(item)){
            return "<script>alert('Update item success!');window.location.href='/adminItem/showAll';</script>";
        }else{
            return "<script>alert('Update item failed!!');window.location.href='/adminItem/showAll';</script>";
        }
    }

    @RequestMapping(value = "/addItem",method = RequestMethod.GET )
    public String addItem(Model model){
        List<Tag> tags = tagService.showAllTags();
        model.addAttribute("tags",tags);
        return "adminItem/adminRegisterItem";
    }

    @ResponseBody
    @RequestMapping(value = "/registItem",method = RequestMethod.POST )
    public String registItem(@ModelAttribute Item item, @RequestParam("tagSelect") String[] tagSelect){
        List<Tag> tags = new ArrayList<>();
        for(String tagName : tagSelect){
            if("".equals(tagName)){
                continue;
            }
            Tag tag = new Tag();
            tag.setName(tagName);
            System.out.println();
            tags.add(tag);
        }
        item.setTags(tags);
        if(itemService.registItem(item)){
            return "<script>alert('regist item success!');window.location.href='/adminItem/addItem';</script>";
        }else{
            return "<script>alert('regist item failed!!');window.location.href='/adminItem/addItem';</script>";
        }
    }

    @RequestMapping(value = "/tagManage",method = RequestMethod.GET )
    public String tagManage(Model model){
        List<Tag> tags = tagService.showAllTags();
        model.addAttribute("tags",tags);
        return "adminItem/adminTagManage";
    }

    @ResponseBody
    @RequestMapping(value = "/addTag",method = RequestMethod.GET )
    public String addTag(@RequestParam("tagName") String tagName){
        if(tagService.registTag(tagName)){
            return "<script>alert('regist tag success!');window.location.href='/adminItem/tagManage';</script>";
        }else{
            return "<script>alert('regist tag failed!!');window.location.href='/adminItem/tagManage';</script>";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteTag",method = RequestMethod.GET )
    public String deleteTag(@PathParam("id") Integer id){
        if(tagService.deleteTag(id)){
            return "<script>alert('delete tag success!');window.location.href='/adminItem/tagManage';</script>";
        }else{
            return "<script>alert('delete tag failed!!');window.location.href='/adminItem/tagManage';</script>";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/updateTag",method = RequestMethod.GET )
    public String updateTag(@RequestParam("id") Integer id, @RequestParam("tagName") String tagName){
        if(tagService.updateTag(id,tagName)){
            return "<script>alert('update tag success!');window.location.href='/adminItem/tagManage';</script>";
        }else{
            return "<script>alert('update tag failed!!');window.location.href='/adminItem/tagManage';</script>";
        }
    }

}
