package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.mapper.TbUserMapper;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.service.PassportService;
import com.bjsxt.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class PassportServiceImpl implements PassportService {

    @Autowired
    private TbUserMapper userMapper;

    /**
     * 校验用户数据是否有效
     * 检查表格tb_user中有没有相同数据
     *
     * @param value 校验的数据
     * @param flag  类型
     * @return
     */
    @Override
    public BaizhanResult check(String value, Integer flag) {

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        switch (flag) {
            case 1:
                queryWrapper.eq("username", value);
                break;
            case 2:
                queryWrapper.eq("phone", value);
                break;
            case 3:
                queryWrapper.eq("email", value);
                break;
            default:
                //其他请求参数，错误参数
                return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
        TbUser current = userMapper.selectOne(queryWrapper);
        if (current == null) {
            //数据可用
            return BaizhanResult.ok();
        }

        //数据不可用
        return BaizhanResult.error("用户名/手机号/电子邮箱不可用，请重新输入！2");
    }

    /**
     * 注册
     * 1. 校验数据的完整性，用户名/手机号/邮箱是否可用，用户名/手机号/密码/邮箱是否有效
     * 2. 保存数据到数据库
     *
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult register(TbUser user) {

        String username = user.getUsername();
        //校验用户名是否有效
        if (null == username || username.trim().length() < 4 || username.trim().length() > 16) {
            //用户名不可用
            return BaizhanResult.error("用户名输入错误，请重新输入，用户名必须是4-16位之间");
        }

        String email = user.getEmail();
        if (null == email || !email.matches("^(.)+((\\\\.).*)*@(.*\\\\.)+.*$")) {
            return BaizhanResult.error("邮箱输入错误，请重新输入");
        }

        String password = user.getPassword();
        if (null == password || password.trim().length() < 6 || password.trim().length() > 16) {
            return BaizhanResult.error("密码输入错误，请重新输入");
        }

        String phone = user.getPhone();
        if (null == phone || !phone.matches("^1(\\d){10}")) {
            return BaizhanResult.error("手机号格式错误，请重新输入");
        }

        //生成用户主键
        user.setId(IDUtils.genItemId());
        Date now = new Date();
        user.setCreated(now);
        user.setUpdated(now);
        //密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        int rows = userMapper.insert(user);
        if (rows != 1) {
            //保存错误
            throw new RuntimeException("保存用户到数据库错误");
        }
        return BaizhanResult.ok();
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public BaizhanResult login(String username, String password) {
        //用户名+密码
        QueryWrapper<TbUser> userQueryWrapper = new QueryWrapper<>();
        //查询条件为多条件并且约束时，直接使用多条件限制即可
        userQueryWrapper.eq("username", username)
                .eq("password", DigestUtils.md5DigestAsHex(password.getBytes()));
        TbUser loginUser = userMapper.selectOne(userQueryWrapper);
        if (loginUser != null) {
            //查询成功，已登录，注意：数据脱敏，只要数据不在数据库中保存，数据必须脱敏
            loginUser.setPassword(null);
            updateUserUpdated(loginUser);
            return BaizhanResult.ok(loginUser);
        }

        //手机号+密码
        QueryWrapper<TbUser> phoneQueryWrapper = new QueryWrapper<>();
        phoneQueryWrapper.eq("phone", username)
                .eq("password", DigestUtils.md5DigestAsHex(password.getBytes()));
        loginUser = userMapper.selectOne(phoneQueryWrapper);
        if (loginUser != null) {
            loginUser.setPassword(null);
            updateUserUpdated(loginUser);
            return BaizhanResult.ok(loginUser);
        }

        //邮箱+密码
        QueryWrapper<TbUser> emailQueryWrapper = new QueryWrapper<>();
        emailQueryWrapper.eq("email", username)
                .eq("password", DigestUtils.md5DigestAsHex(password.getBytes()));
        loginUser = userMapper.selectOne(emailQueryWrapper);
        if (loginUser != null) {
            loginUser.setPassword(null);
            updateUserUpdated(loginUser);
            return BaizhanResult.ok(loginUser);
        }

        //登录失败
        return BaizhanResult.error("用户名或密码错误");
    }

    @Override
    public void logout(TbUser loginuser) {

        updateUserUpdated(loginuser);
    }

    /**
     * 更新用户最近的访问时间
     *
     * @param user
     */
    private void updateUserUpdated(TbUser user) {
        //修改update的时间
        user.setUpdated(new Date());
        int rows = userMapper.updateById(user);
        if (rows != 1) {
            throw new RuntimeException("更新用户最近访问时间失败");
        }


    }

}
