package com.company.project.dao;

import com.company.project.core.Mapper;
import com.company.project.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends Mapper<User> {

//    @Select("select * from User where username = #{username}")
    public User findUserByName(String username);

//    @Select("select * from User")
    public List<User> findAllUsers();

//    @Insert("insert into User ()")//how?
    public void addUser(User user);

    public void updateUser(User user);

}