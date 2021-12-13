package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.service.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 前台passport系统控制器
 */
@RestController
@CrossOrigin
public class PassportController {

    @Autowired
    private PassportService passportService;

    /**
     * 注销，就是销毁HttpSession对象
     *
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    public BaizhanResult logout(HttpSession session) {

        TbUser loginUser = (TbUser) session.getAttribute("loginUser");
        //销毁
        session.invalidate();
        passportService.logout(loginUser);
        return BaizhanResult.ok();
    }

    /**
     * 登录
     *
     * @param username 用户名|手机号|邮箱
     * @param password 密码（明文）
     * @param session
     * @return
     */
    @PostMapping("/user/userLogin")
    public BaizhanResult login(String username, String password, HttpSession session) {

        BaizhanResult result = passportService.login(username, password);
        if (result.getStatus() == 200) {
            //登录成功
            session.setAttribute("loginUser", result.getData());
        } else {
            //登录失败
        }
        return result;
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @PostMapping("/user/userRegister")
    public BaizhanResult register(TbUser user) {

        try {
            return passportService.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 注册信息校验
     *
     * @param value 要校验的数据
     * @param flag  校验数据类型  1-用户名   2-手机号   3-邮箱
     * @return
     */
    @GetMapping("/user/checkUserInfo/{value}/{flag}")
    public BaizhanResult check(@PathVariable String value, @PathVariable("flag") Integer flag) {

        return passportService.check(value, flag);
    }

}
