package com.demo.controller;

import com.demo.service.RedisBloomFilterService;
import com.demo.utils.BloomFilterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Reece Lin
 * @version 1.00
 * @time 2020/8/15 19:47
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisBloomFilterService filterService;


    @Autowired
    private BloomFilterHelper bloomFilterHelper;

    @ResponseBody
    @RequestMapping("/add")
    public String addBloomFilter(@RequestParam("openid") String openid) {


        try {
            filterService.add(bloomFilterHelper,"bloom",openid);
        } catch (Exception e) {
            e.printStackTrace();
            return "添加失败";
        }

        return "添加成功";
    }

    @ResponseBody
    @RequestMapping("/check")
    public boolean checkBloomFilter(@RequestParam ("openid") String openid) {
        boolean b = filterService.contains(bloomFilterHelper, "bloom", openid);
        return b;
    }


}
