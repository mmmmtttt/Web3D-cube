package com.company.project.web;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.dto.AttributeDTO;
import com.company.project.dto.LoginUserDTO;
import com.company.project.dto.RecordDTO;
import com.company.project.dto.UserProfileDTO;
import com.company.project.model.Record;
import com.company.project.model.User;
import com.company.project.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by CodeGenerator on 2022/04/03.
 * Controller可以返回 Model&View, String, void
*/
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public Result add(User user) {
        //使用形参中的model将model数据传到页面
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 用户注册
     */
    @PostMapping(value = "/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return ResultGenerator.genSuccessResult();
    }


    /**
     * 获取用户档案
     */
    @GetMapping(value = "/profile")
    public Result profile(@RequestHeader("Authorization") String token){
        UserProfileDTO userProfileDTO = userService.profile(token.replace("Bearer ",""));
        return ResultGenerator.genSuccessResult(userProfileDTO);
    }

    /**
     * 获取用户解题记录
     */
    @GetMapping(value = "/records")
    public Result record(@RequestHeader("Authorization") String token){
        //从token里面得到username
        List<RecordDTO> records = userService.record(token.replace("Bearer ",""));
        return ResultGenerator.genSuccessResult(records);
    }

    /**
     * 获取用户战绩
     */
    @GetMapping(value = "/attributes")
    public Result attribute(@RequestHeader("Authorization") String token){
        //从token里面得到username
        AttributeDTO attribute = userService.attribute(token.replace("Bearer ",""));
        return ResultGenerator.genSuccessResult(attribute);
    }


    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        userService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(User user) {
        userService.update(user);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = userService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
