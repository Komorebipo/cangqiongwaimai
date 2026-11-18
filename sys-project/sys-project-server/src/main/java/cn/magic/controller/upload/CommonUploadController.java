package cn.magic.controller.upload;

import cn.magic.utils.ResultVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonUploadController {
    //图片上传的路径
    @Value("${web.upload-path}")
    private String webUploadpath;

    @RequestMapping("/upload")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file){
        String Url = "";
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String fileExtenionName = fileName.substring(fileName.indexOf("."));
        //新的文件名
        String newName = UUID.randomUUID().toString()+fileExtenionName;
        String path = webUploadpath;
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
            //设置权限
            fileDir.setWritable(true);
        }
        File targetFile = new File(path,newName);
        try{
            file.transferTo(targetFile); //保存文件
            Url = "/" + targetFile.getName();
        }catch (Exception e){
            return  null;
        }

        return ResultVo.success("上传图片成功！！！","/files" + Url);
    }
}