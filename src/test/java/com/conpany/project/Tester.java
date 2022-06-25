package com.conpany.project;


import com.company.project.Application;
import com.company.project.dao.UserMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元测试继承该类即可
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
public abstract class Tester {
//    String resource = "";
//    SqlSessionFactory sqlSessionFactory = new SqlSessionFactory()
//
//    UserMapper userMapper = session.getMapper(UserMapper.class);
//    userMapper.findUserByName();
//    session.commit();
}



