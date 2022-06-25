package com.company.project.filter;

import com.alibaba.fastjson.JSONObject;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.core.ServiceException;
import com.company.project.exception.AuthenticationExceptionImpl;
import com.company.project.model.User;
import com.company.project.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtLoginFilter(AuthenticationManager authenticationManager) {
        super();
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl("/user/login");
    }

    /**
     * 用户尝试登陆的时候会经过这个函数
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            //尝试登录
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()
                    )
            );
            return authentication;
        } catch (IOException e) {
            throw new AuthenticationExceptionImpl("未知错误");
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        String jsonString = JSONObject.toJSONString(ResultGenerator.genSuccessResult());
        response.getWriter().write(jsonString);
        String token = JwtUtils.generateJwt(user);
        response.setHeader("Access-Control-Expose-Headers",JwtUtils.TOKEN);
        response.addHeader(JwtUtils.TOKEN, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(403);
        response.setContentType("application/json; charset=utf-8");

        String msg = "Bad credentials".equals(failed.getMessage()) ? "密码错误" : failed.getMessage();
        response.sendError(403,msg);
        return;
//        String jsonString = JSONObject.toJSONString(ResultGenerator.genFailResult(msg));
//        response.getWriter().write(jsonString);
    }

}
