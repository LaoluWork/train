package com.laolu.train.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
//        Thread.sleep(100);
//        int i = RandomUtil.randomInt(1, 10);
//        if (i <= 2) {
//            throw new RuntimeException("测试异常");
//        }
        return "Hello Business!";
    }

}
