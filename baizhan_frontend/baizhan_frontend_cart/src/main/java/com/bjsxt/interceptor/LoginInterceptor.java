package com.bjsxt.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 前置拦截器，校验用户是否已登录
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        System.out.println("登录拦截器执行校验");
        //获取用户登录标记
        Object loginUser = request.getSession().getAttribute("loginUser");

        if (loginUser == null) {
            //未登录
            //根据返回结果{status=403}做登录校验失败处理
            //返回的响应状态码，都是20x，成功响应
            response.setStatus(HttpServletResponse.SC_OK);  //  响应状态码200
            response.setContentType("application/json;charset=UTF-8");  //响应类型
            response.getWriter().println("{\"status\":403, \"msg\":\"请登录！\"}");
            response.getWriter().flush();
            return false;
        }
        //已登录
        return true;
    }

}
