package com.atguigu.gmall.index.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/3 05:01
 * @Email: moumouguan@gmail.com
 */
@Service
public class LockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 测试. 每次 将 number 值设置为 0 通过 ab 压测工具 ab -n 5000 -c 100 192.168.0.111:8888/index/test/lock 测试高并发下是否出现并发问题(number 未到 5000 即出现并发问题)
     *  ab压测: ab  -n（一次发送的请求数）  -c（请求的并发数） 访问路径
     *      1. 将 number 值设置为 0, 通过浏览器访问 每访问一次 number + 1
     *      2. 通过 ab 压测工具 ab -n 5000 -c 100 192.168.0.111:8888/index/test/lock 测试高并发下是否出现并发问题(number 未到 5000 即出现并发问题)
     *          最终 number 值为 201 出现并发性问题
     *      3. 添加 synchronized jvm 锁 将 number 设置为 0 压测 5000
     *          最终 number 值为 5000, 要注意的是 这只是单机工程
     *      4. copy 2 份实例 将 number 设置为 0 压测 5000
     *          最终 number 值为 1928, 出现并发性问题. 理论值在 5000 / 3 至 5000(极限情况下 3 台服务同时放入一个线程 同时到达 都将 num 转换为某一个数字 ++.)
     *
     * 基于 redis 实现分布式锁。借助于 setnx 指令 当 key 不存在即设置成功返回 1 当 key 存在即设置失败返回 0(加锁 解锁 重试)
     *      分布式锁特征: 独占排他互斥使用
     *
     *      存在的问题
     *          1. 死锁
     *              一个线程获取到锁 还没有执行到释放锁操作 服务器宕机. 其他线程获取不到锁 即使 服务器重启 这把锁也无法被释放掉. 其他线程一直执行递归操作 最终导致服务器资源耗尽而宕机
     *                  添加过期时间在 set(获取锁) 时去设置过期时间
     *
     *          2. 防误删
     *              如果业务逻辑的执行时间是7s, A 服务获取锁 业务没有执行完 锁3秒被自动释放, B 服务获取到锁 业务没有执行完 锁3秒被自动释放, C 服务获取锁执行业务逻辑.
     *              A 服务业务执行完成 释放锁, 这时释放的是 C 的锁. 导致 C 业务只执行了 1s 就被别人释放. 最终等于没有锁(可能会释放其他服务器的锁)
     *                  setnx 获取锁时, 设置一个指定的唯一值(例如：uuid); 释放前获取这个值, 判断是否自己的锁(注意删除缺乏原子性)
     */
    public void testLock() {

        String uuid = UUID.randomUUID().toString();

        /**
         * 加锁 setIfAbsent 类似与 setNx 当 key 不存在即设置成功 否 则 失败
         *      分布式锁本质就是对 key 的争抢, 谁先设置成功谁就先获取锁
         */
        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS); // 解决死锁 添加过期时间在 set(获取锁) 时, 去设置过期时间;

        if (!flag) {
            // 加锁失败, 进行递归调用进行重试
            try {
                // 睡眠一段时间(如果不设置睡眠不停的重试也可能会导致栈内存溢出) 模拟让抢到锁的线程执行业务逻辑 减少竞争
                Thread.sleep(30);
                // 设置锁(加锁)失败重新调用该方法进行重试
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            // 添加过期时间 （缺乏原子性：如果在 setnx 和 expire 之间出现异常，锁也无法释放)
//            redisTemplate.expire("lock", 3, TimeUnit.SECONDS);

            String number = redisTemplate.opsForValue().get("number");

            if (StringUtils.isBlank(number)) {
                redisTemplate.opsForValue().set("number", "1");
            }

            int num = Integer.parseInt(number);

            redisTemplate.opsForValue().set("number", String.valueOf(++num));

            if (StringUtils.equals(redisTemplate.opsForValue().get("lock"), uuid)) { // 解锁时判断是否是自己的锁
                // 释放锁
                redisTemplate.delete("lock");
            }
        }
    }
}