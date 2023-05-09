package com.atguigu.gmall.cart.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/9 23:26
 * @Email: moumouguan@gmail.com
 */
@Slf4j
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    /**
     * 处理未捕获异常方法, 当 异步方法的返回值是非 future 对象时使用
     * @param throwable
     * @param method
     * @param objects
     */
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {

        // TODO: 记录日志 或者 记录到数据库中
        log.error(
                "异步任务执行失败. 失败信息: {}, 方法: {}, 参数列表: {}",
                throwable.getMessage(), method.getName(), Arrays.asList(objects)
        );
    }
}
