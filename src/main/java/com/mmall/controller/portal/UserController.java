package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Olaf
 * @create 2019-10-09 11:12
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**用户登录*/
    @ResponseBody
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**用户退出*/
    @ResponseBody
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("退出成功");
    }

    /**用户注册*/
    @ResponseBody
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**验证用户名和邮箱*/
    @ResponseBody
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }

    /**获取用户登录信息*/
    @ResponseBody
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前信息");
    }

    /**获取重置密码的问题*/
    @ResponseBody
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**校验问题是否正确*/
    @ResponseBody
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer) {
       return iUserService.checkAnswer(username, question, answer);
    }

    /**重置密码*/
    @ResponseBody
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetRestPassword(String username,String password,String passwordNew,String forgetToken){
       return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
    }

    /**登录状态下重置密码*/
    @ResponseBody
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    public ServerResponse<User> resetPassword(String passwordOld,String passwordNew,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**更新个人信息(只能在登录状态下更新个人信息)*/
    @ResponseBody
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    public ServerResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //前台封装的User对象是没有userId的，需要从session中获取
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**获取用户详细信息*/
    @ResponseBody
    @RequestMapping(value = "get_information.do",method = RequestMethod.POST)
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录，status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
