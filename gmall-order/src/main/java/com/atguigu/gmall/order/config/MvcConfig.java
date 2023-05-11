package com.atguigu.gmall.order.config;

import com.atguigu.gmall.order.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/11 17:54
 * @Email: moumouguan@gmail.com
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有路径
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
