package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author wb
 * @create 2019-10-09 11:15
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**登录*/
    @Override
    public ServerResponse<User> login(String username, String password) {
        //查username是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //to do MD5明文加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        //查询用户名和密码是否匹配
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**注册*/
    @Override
    public ServerResponse<String> register(User user) {
        //验证用户名是否存在
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        //验证邮箱是否已存在
        ServerResponse<String> validResponse2 = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse2.isSuccess()){
            return validResponse2;
        }

        //设置为普通用户登录
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //使用MD5工具类对密码加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //将注册好的user信息插入数据库
        int resultCount = userMapper.insert(user);

        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createByErrorMessage("注册成功");
    }

    /**验证邮箱和用户名*/
    @Override
    public ServerResponse<String> checkValid(String str, String type){
        if (StringUtils.isNotBlank(type)){
            //开始校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    /*
    *忘记密码
    * 点击忘记密码，则去数据库中查找该用户的密保问题，并显示在页面中
    */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /*
    * 校验问题答案是否正确
    * 输入密保问题的答案，若正确则把 一个随机生成的字符串 保存在LocalCache中
    */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int checkAnswer = userMapper.checkAnswer(username, question, answer);
        if (checkAnswer > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return  ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }


    /*
    * 重置密码
    * 从LocalCache中取出随机的字符串，与输入的 随机生成的字符串 进行equals比较，若正确则进行重置密码的操作
    */
    @Override
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken){
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if (StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else {
                return  ServerResponse.createByErrorMessage("token错误，请重新获取重置密码");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**登录状态下的重置密码*/
    @Override
    public ServerResponse<User> resetPassword(String passwordOld, String passwordNew, User user){
        //为了防止横向越权，一定要校验用户的旧密码   问题一！！！
        //如果不要求输入当前密码，如果你账号未退出登录，那么任何人都可以直接操作你的网页把你的密码修改了；
        //服务端实际上根据sessionid来判断是否登录，如果sessionid被非法截取，是否我也是可以直接不输入密码伪造重置密码，从而达到修改你密码的目的？
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**更新个人信息*/
    @Override
    public ServerResponse<User> updateInformation(User user){
        //username不能更新,email更新需要根据userId更新， checkEmial()只是查数据库中是否存在email,
        //而我们需要查更新的Email是否跟数据库中已存在Email重复
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在，请更换");
        }

        User updataUser = new User();
        updataUser.setId(user.getId());
        updataUser.setEmail(user.getEmail());
        updataUser.setPhone(user.getPhone());
        updataUser.setQuestion(user.getQuestion());
        updataUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updataUser);
        if (updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updataUser);
        }
        return ServerResponse.createByErrorMessage("更新个人失败");
    }

    /**获取用户详细信息*/
    @Override
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //backend
    /**校验是否是管理员*/
    @Override
    public ServerResponse<User> checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
