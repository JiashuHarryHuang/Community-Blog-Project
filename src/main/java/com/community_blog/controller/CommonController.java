package com.community_blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/common")
@Slf4j
public class CommonController {
    /**
     * 异常页面
     * @return 异常页面
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "/site/error/500";
    }
}
