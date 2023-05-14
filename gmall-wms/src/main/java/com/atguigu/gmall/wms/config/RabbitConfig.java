package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/14 12:46
 * @Email: moumouguan@gmail.com
 */
@Configuration // 声明该类是一个配置类
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct // 构造方法执行之后就会执行, 项目时添加此配置类 调用该类的无参构造方法初始化. 添加该注解构造方法执行之后就会执行设置两个回调
//    @PreDestroy // 对象销毁之前执行
    public void init() {
        // 确认消息是否到达交换机的回调, 不管消息是否到达交换机都会执行
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息已到达交换机");
            } else {
                System.err.println("消息没有达到交换机: 原因 " + cause);
            }
        });

        // 确认消息是否到达队列的回调, 只有消息没有到达队列才会执行
        // 例如 消息没有达到交换机: 原因 channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'spring_test_exchange2' in vhost '/admin', class-id=60, method-id=40)
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                System.err.println("消息没有到达队列: " + " 交换机 " + exchange + " 路由键 " + routingKey
                        + " 消息内容 " + replyText + " 状态码 " + replyCode + " 消息内容 " + new String(message.getBody()))
        );
    }

    /**
     * 定时解锁库存操作流程
     *      wms 验库存锁定库存成功，发送消息到业务交换机，经过延迟队列. 这个队列会有一定的延迟时间
     *      时间到了就会把信息转发给死信交换机。转发到死信队列 并发送消息 给 wms 服务进行解锁库存
     *
     */

    /**
     * 业务交换机: ORDER_EXCHANGE
     */

    /**
     * 延迟队列: STOCK_TTL_QUEUE
     */
    @Bean
    public Queue ttlQueue() {
        // 返回一个持久化 生存时间是 10 秒的延迟队列(单位是毫秒)
        return QueueBuilder.durable("STOCK_TTL_QUEUE")
                .ttl(100000) // 需要比自动关单时间长一些，避免 订单未关闭 库存已解锁的情况发生
                // 时间到了经过那个死信交换机进入那个死信队列
                .deadLetterExchange("ORDER_EXCHANGE") // 指定死信交换机名称
                // rk
                .deadLetterRoutingKey("stock.unlock") // 经过什么 rk 会将消息转发到死信队列
                .build();
    }

    /**
     * 把延迟队列绑定到业务交换机: rk = stock.ttl
     */
    @Bean
    public Binding ttlBinding() {
//    public Binding ttlBinding(Queue ttlQueue) {
//        return BindingBuilder
        // 把什么队列绑定到什么交换机
//                .bind(ttlQueue)
//                .to() // 业务交换机 是通过注解方式生成, 此处拿不到.

        // 只能通过构造方法声明, 把 ORDER_TTL_QUEUE 队列通过 order.ttl rk 绑定到 ORDER_EXCHANGE 交换机
        return new Binding("STOCK_TTL_QUEUE", Binding.DestinationType.QUEUE, "ORDER_EXCHANGE", "stock.ttl", null);
    }

    /**
     * 死信交换机: ORDER_EXCHANGE
     */

    /**
     * 死信队列: STOCK_UNLOCK_QUEUE(目的就是解锁库存, 进入该队列也会解锁库存, 并且无需声明 已经通过注解形式声明过了)
     *      解锁库存 STOCK_UNLOCK_QUEUE 队列已经实现, 现在我们也需要解锁库存. 把 STOCK_UNLOCK_QUEUE 该队列当作死信队列使用
     */
//    @Bean
//    public Queue deadQueue() {
//        // 死信队列本质就是一个普通的队列
//        return QueueBuilder.durable("STOCK_UNLOCK_QUEUE").build();
//    }

    /**
     * 把死信队列绑定到死信交换机: stock.unlock
     *      此处也无需绑定, 也已经通过注解形式绑定过了
     */
//    @Bean
//    public Binding deadBinding() {
//        return new Binding("STOCK_UNLOCK_QUEUE", Binding.DestinationType.QUEUE, "ORDER_EXCHANGE", "stock.unlock", null);
//    }
}
