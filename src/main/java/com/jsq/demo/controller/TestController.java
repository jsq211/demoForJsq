package com.jsq.demo.controller;

import com.jsq.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 方法测试类
 * @author jsq
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/batchInsert")
    public Integer batchInsert(){
        return testService.testBatchInsert();
    }
    @GetMapping("/insert")
    public Integer insert(){
        return testService.testInsert();
    }
}
