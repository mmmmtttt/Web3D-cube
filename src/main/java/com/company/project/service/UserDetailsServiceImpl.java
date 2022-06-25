package com.company.project.service;

import com.company.project.dao.UserMapper;
import com.company.project.exception.AuthenticationExceptionImpl;
import com.company.project.model.User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类实现了Spring Security框架中UserDetailsService借口
 */
@Service("userDetailService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws AuthenticationException {
        User user = userMapper.findUserByName(s);
        if (user == null) {
            throw new AuthenticationExceptionImpl("没有找到这个用户");
        }
        //用户的权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        //将更新好的用户权限数组赋给用户对象
        user.setAuthorities(authorities);
        return user;
    }


}
