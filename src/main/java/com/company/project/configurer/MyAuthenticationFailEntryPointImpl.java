package com.company.project.configurer;

import com.alibaba.fastjson.JSONObject;
import com.company.project.core.ResultGenerator;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyAuthenticationFailEntryPointImpl implements AuthenticationEntryPoint {

    public MyAuthenticationFailEntryPointImpl() {
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String jsonMessage = JSONObject.toJSONString(ResultGenerator.genFailResult("你当前访问的接口权限不足，请检查后再试"));

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(jsonMessage);

        httpServletResponse.setStatus(403);

    }


}
