package com.community_blog.controller.advice;

import com.alibaba.fastjson.JSON;
import com.community_blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString()); //日志记录异常信息
        }

        String xRequestedWith = request.getHeader("x-requested-with");

        //判断是不是异步请求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(Result.error("服务器异常!")));
        } else {
            response.sendRedirect(request.getContextPath() + "/common/error");
        }
    }
}
