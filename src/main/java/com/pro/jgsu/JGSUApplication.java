package com.pro.jgsu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author XWH
 * @Time 2022/7/26 下午 20:47
 */
@Slf4j
@SpringBootApplication
public class JGSUApplication {
    public static void main(String[] args) {
        SpringApplication.run(JGSUApplication.class,args);
        log.info("项目启动成功");

    }
}
