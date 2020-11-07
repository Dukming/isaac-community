package com.dkm.isaaccommunity.controller;

import com.dkm.isaaccommunity.bean.Item;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.service.TestService;
import com.dkm.isaaccommunity.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/que", method = RequestMethod.GET)
    public String question(){return "question/questionIndex";}

    @RequestMapping(value = "/fenyeTEST", method = RequestMethod.GET)
    public String showAll(Model model){

        List<Item> items = testService.showAllItems();
        model.addAttribute("items", items);
        return "fenyeTEST";
    }

    @ResponseBody
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public Map<String, Object> uploadONE(@RequestParam("file") MultipartFile file){
        String result_msg="";//上传结果信息

        Map<String,Object> root=new HashMap<String, Object>();

        if (file.getSize() / 1000 > 100){
            result_msg="图片大小不能超过100KB";
        }
        else{
            //判断上传文件格式
            String fileType = file.getContentType();
            if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpeg")) {
                //上传后保存的文件名(需要防止图片重名导致的文件覆盖)
                //获取文件名
                String fileName = file.getOriginalFilename();
                //获取文件后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //重新生成文件名
                fileName = UUID.randomUUID()+suffixName;
                // 存放上传图片的文件夹
                File fileDir = FileUtil.getImgDirFile(GlobalConstant.TO_SEARCHER_IMG_PATH_PREFIX);
                String localPath = fileDir.getAbsolutePath() + File.separator + fileName;
                if (FileUtil.upload(file, localPath)) {
                    //文件存放的相对路径(一般存放在数据库用于img标签的src)
                    String relativePath="/toSearcher/"+fileName;
                    root.put("relativePath",relativePath);//前端根据是否存在该字段来判断上传是否成功
                    result_msg="图片上传成功";
                    try{
                        testService.showScore(localPath);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
                else{
                    result_msg="图片上传失败";
                }
            }
            else{
                result_msg="图片格式不正确";
            }
        }

        root.put("result_msg",result_msg);
        return root;
    }

    @ResponseBody
    @RequestMapping(value = "/test2", method = RequestMethod.POST)
    public String uploadTWO(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }


        // 拿到文件名
        String filename = file.getOriginalFilename();

        // 存放上传图片的文件夹
        File fileDir = FileUtil.getImgDirFile(GlobalConstant.TO_SEARCHER_IMG_PATH_PREFIX);
        // 输出文件夹绝对路径  -- 这里的绝对路径是相当于当前项目的路径而不是“容器”路径
        System.out.println(fileDir.getAbsolutePath());

        try {
            // 构建真实的文件路径
            String localPath = fileDir.getAbsolutePath() + File.separator + filename;
            File newFile = new File(localPath);
            System.out.println(newFile.getAbsolutePath());

            // 上传图片到 -》 “绝对路径”
            file.transferTo(newFile);
            try{
                testService.showScore(localPath);
            }catch (IOException e){
                e.printStackTrace();
            }
            return "上传成功!";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败！";
    }
}
