package com.company.project.configurer;

import com.alibaba.fastjson.JSONObject;
import com.company.project.core.ResultGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyAccessDenied implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //设置响应状态码
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        //设置响应数据格式
        response.setContentType("application/json;charset=utf-8");
        //输入响应内容
        PrintWriter writer = response.getWriter();

        //设置返回信息
        String jsonMessage = JSONObject.toJSONString(ResultGenerator.genFailResult("你当前访问的接口权限不足"));

        //设置返回信息
        writer.write(jsonMessage);

    }
}
