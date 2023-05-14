package com.atguigu.gmall.payment.interceptors;

import com.atguigu.gmall.payment.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/14 13:35
 * @Email: moumouguan@gmail.com
 */
@Component // 注入到 spring 容器
//@Data
public class LoginInterceptor implements HandlerInterceptor {

//    private UserInfo userInfo;

    // 范型中放入的是真正的载荷, 需要将什么东西传递给后续业务
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>(); // 不要对外直接暴露

    /**
     * 前置方法, 在 Controller 方法执行之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("前置方法, 在 Controller 方法执行之前执行");

        String userIdString = request.getHeader("userId");
        Long userId = null;
        if (StringUtils.isNotBlank(userIdString)) {
            // 到达订单服务一定是登陆状态, 从请求头中获取
            userId = Long.valueOf(userIdString);
        }
        String userName = request.getHeader("userName");

        // 已经获取了登陆信息
        UserInfo userInfo = new UserInfo(userId, null, userName);
        // 把信息放入线程的局部变量
        THREAD_LOCAL.set(userInfo);

        return true; // true 为放行, false 为拦截
    }

    /**
     * 为 THREAD_LOCAL 封装一个静态方法, 方便后续使用直接调用 避免对外暴露导致的修改
     * 封装了一个获取线程局部变量值的静态方法
     * @return
     */
    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }

    /**
     * 完成方法, 在 视图渲染 完成之后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("完成方法, 在 视图渲染 完成之后执行");

        // 调用删除方法，是必须选项。因为使用的是tomcat线程池，请求结束后，线程不会结束。
        // 如果不手动删除线程变量，可能会导致内存泄漏
        THREAD_LOCAL.remove();
    }
}
