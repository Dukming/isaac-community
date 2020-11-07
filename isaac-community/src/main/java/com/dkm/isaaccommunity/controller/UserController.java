package com.dkm.isaaccommunity.controller;

import com.alibaba.fastjson.JSONObject;
import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.config.GlobalConstant;
import com.dkm.isaaccommunity.service.UserService;
import com.dkm.isaaccommunity.util.FileUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;


@Controller
@RequestMapping("/user")
@RequiresRoles(value = {"2"})
public class UserController {

    @Autowired
    private UserService userService;

//    @ResponseBody
//    @RequestMapping(value = "/getUserHeadImg", method = RequestMethod.GET)
//    public JSONObject getUserHeadImg(){
//        String imgPath = userService.getSessionUser().getImgAddr();
//        JSONObject result = new JSONObject();
//        result.put("imgpath", imgPath);
//        return result;
//    }

    @RequestMapping(value = "/toUserPage", method = RequestMethod.GET)
    public String toUserPage(Model model){
        User user = userService.getSessionUser();
        model.addAttribute("user", user);
        return "question/userInfo";
    }

    @ResponseBody
    @RequestMapping(value = "/uploadUserHeadImg", method = RequestMethod.POST)
    public JSONObject uploadUserHeadImg(HttpServletRequest request){
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        MultipartFile file = multiRequest.getFile("file");

        // 拿到文件名
        String filename = file.getOriginalFilename();

        //判断上传文件格式
        String fileType = file.getContentType();

        JSONObject result = new JSONObject();

        // 存放上传图片的文件夹
        File fileDir = FileUtil.getImgDirFile(GlobalConstant.USER_HEAD_IMG_PATH_PREFIX);

        if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpeg")) {
            try {
                // 构建真实的文件路径
                String localPath = fileDir.getAbsolutePath() + File.separator + filename;
                File newFile = new File(localPath);

                // 上传图片到 -》 “绝对路径”
                file.transferTo(newFile);

                //保存图片名称到数据库
                if(userService.saveHeadImg(filename)){
                    result.put("code", 1);
                    result.put("message", "上传成功！");
                    result.put("imgAddr", GlobalConstant.USER_HEAD_PRE_ADDR + filename);
                    return result;
                }else{
                    result.put("code", 2);
                    result.put("message", "上传失败，请重试！");
                    return result;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            result.put("code", 2);
            result.put("message", "上传文件类型不符！");
            return result;
        }

        result.put("code", 2);
        result.put("message", "上传失败，请重试！");
        return result;
    }
}
