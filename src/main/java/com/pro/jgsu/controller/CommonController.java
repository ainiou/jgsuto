package com.pro.jgsu.controller;

import com.pro.jgsu.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${jgsuto.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //获取源文件名
        String originalFilename = file.getOriginalFilename();
        //获得后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成新文件名保证文件名唯一，防止覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //文件夹不存在则新建一个文件夹
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流将文件写回到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
