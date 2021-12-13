package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbUser;

/**
 * 前台passport系统服务接口
 */
public interface PassportService {

    BaizhanResult check(String value, Integer flag);

    BaizhanResult register(TbUser user);

    BaizhanResult login(String username, String password);

    void logout(TbUser loginuser);

}
