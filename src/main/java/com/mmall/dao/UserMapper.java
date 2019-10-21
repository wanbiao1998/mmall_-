package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //查username是否存在
    int checkUsername(String username);
    //查Email是否存在
    int checkEmail(String email);
    //查用户名和密码是否匹配
    User selectLogin(@Param("username") String username,@Param("password") String password);
    //根据username查问题
    String selectQuestionByUsername(String username);
    //查看答案
    int checkAnswer(@Param("username") String username,@Param("question")String question,@Param("answer")String answer);
    //根据username更新密码
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);
    //检查密码
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);
    //根据userId更新Email
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}