package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @author wb
 * @create 2019-10-09 11:14
 */
public interface IUserService {
    /**用户登录*/
    ServerResponse<User> login(String username, String password);
    /**用户注册*/
    ServerResponse<String> register(User user);
    /**校验用户名和邮箱*/
    ServerResponse<String> checkValid(String str,String type);
    /**查问题*/
    ServerResponse<String> selectQuestion(String username);
    /**校验问题是否正确*/
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    /**重置密码*/
    ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken);
    /**登录状态下重置密码*/
    ServerResponse<User> resetPassword(String passwordOld,String passwordNew,User user);
    /**更新个人信息*/
    ServerResponse<User> updateInformation(User user);
    /**获取用户详细信息*/
    ServerResponse<User> getInformation(Integer userId);
    /**校验是否是管理员*/
    ServerResponse<User> checkAdminRole(User user);
}
