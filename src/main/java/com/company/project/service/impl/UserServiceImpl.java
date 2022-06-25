package com.company.project.service.impl;

import com.company.project.core.ServiceException;
import com.company.project.dao.RecordMapper;
import com.company.project.dao.UserMapper;
import com.company.project.dto.AttributeDTO;
import com.company.project.dto.Correct;
import com.company.project.dto.RecordDTO;
import com.company.project.dto.UserProfileDTO;
import com.company.project.model.Record;
import com.company.project.model.User;
import com.company.project.service.UserService;
import com.company.project.core.AbstractService;
import com.company.project.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * Created by CodeGenerator on 2022/04/03.
 */
@Service
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RecordMapper recordMapper;

    @Override
    public void register(User user) {
        //portrait检查
        if(user.getPortraitId()==null || user.getJacket()==null || user.getPants()==null){
            throw new ServiceException("请选择用户画像");
        }
        //空字符检查
        if(user.getPassword() == null || user.getUsername() == null || user.getUsername().equals("") || user.getPassword().equals("")){
            throw new ServiceException("用户名和密码不能为空!");
        }
        //密码是否符合规定
        if(!checkPasswordStrength(user.getPassword())){
            throw new ServiceException("密码中字母，数字，特殊字符必须包含至少两种");
        }
        //用户名是否冲突
        User oldUser = userMapper.findUserByName(user.getUsername());
        if(oldUser != null){
            throw new ServiceException("用户名已存在");
        }
        //新增操作
        user.setRegisterDate(new Date());
        userMapper.insert(user);
        //使用扩展类？UserCustom
    }

    @Override
    public UserProfileDTO profile(String token){
        String username = JwtUtils.getName(token);
        User user = userMapper.findUserByName(username);
        if(user == null){
            throw new ServiceException("用户不存在");
        }
        return new UserProfileDTO(user.getUsername(),user.getSex(),user.getPortraitId());
    }

    @Override
    public List<RecordDTO> record(String token){
        String username = JwtUtils.getName(token);
        User user = userMapper.findUserByName(username);
        if(user == null){
            throw new ServiceException("用户不存在");
        }
        return recordMapper.getRecords(username);
    }

    @Override
    public AttributeDTO attribute(String token){
        String username = JwtUtils.getName(token);
        User user = userMapper.findUserByName(username);
        if(user == null){
            throw new ServiceException("用户不存在");
        }
        Integer DS = 0;
        Integer CA = 0;
        Integer CN = 0;
        Integer Cypher = 0;
        Integer Other = 0;
        //获取用户答对的题目
        List<Correct> correctList = recordMapper.getCorrect(username);
        for (Correct c:correctList) {
            if(c.getTag().equals("DS")){
                DS = c.getNum();
            }else if(c.getTag().equals("CA")){
                CA = c.getNum();
            }else if(c.getTag().equals("CN")){
                CN = c.getNum();
            }else if(c.getTag().equals("Cypher")){
                Cypher = c.getNum();
            }else {
                Other = c.getNum();
            }
        }
        Integer correct = DS + CA + CN + Cypher + Other;
        Integer numOfTrials = recordMapper.getAllTrial(username);
        // 有可能结果是null,就会抛出空指针异常
        numOfTrials = numOfTrials==null?0:numOfTrials;
        Integer wrong = numOfTrials - correct;
        return new AttributeDTO(correct,wrong,CA,CN,Cypher,DS,Other);
    }


    /**
     * 检查密码强度，密码长度为8-32，密码中字母，数字，特殊字符必须包含至少两种
     * @param password
     * @return
     */
    private boolean checkPasswordStrength(String password) {
        if (password.length() < 8 || password.length() > 32) {
            return false;
        }

        boolean[] bool = new boolean[3];
        int cnt = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                bool[0] = true;
            } else if (c >= '0' && c <= '9') {
                bool[1] = true;
            } else if (c == '-' || c == '_') {
                bool[2] = true;
            }
        }
        for (boolean b : bool) {
            if (b) {
                cnt += 1;
            }
        }
        return cnt >= 2;
    }

}
