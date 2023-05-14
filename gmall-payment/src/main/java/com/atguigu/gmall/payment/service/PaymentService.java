package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.payment.feign.GmallOmsClient;
import com.atguigu.gmall.payment.mapper.PaymentMapper;
import com.atguigu.gmall.payment.pojo.PaymentInfoEntity;
import com.atguigu.gmall.payment.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/14 13:35
 * @Email: moumouguan@gmail.com
 */
@Service
public class PaymentService {

    @Autowired
    private GmallOmsClient omsClient;

    @Autowired
    private PaymentMapper paymentMapper;

    public OrderEntity queryOrderByToken(String orderToken) {
        ResponseVo<OrderEntity> orderEntityResponseVo = omsClient.queryOrderByToken(orderToken);
        OrderEntity orderEntity = orderEntityResponseVo.getData();

        return orderEntity;
    }

    public Long savePaymentInfo(PayVo payVo) {
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setPaymentStatus(0);
        paymentInfoEntity.setOutTradeNo(payVo.getOut_trade_no());
        paymentInfoEntity.setPaymentType(1);
        paymentInfoEntity.setSubject(payVo.getSubject());
        // paymentInfoEntity.setTotalAmount(orderEntity.getPayAmount());
        paymentInfoEntity.setTotalAmount(new BigDecimal(payVo.getTotal_amount() ));
        paymentInfoEntity.setCreateTime(new Date());
        paymentMapper.insert(paymentInfoEntity);
        return paymentInfoEntity.getId();
    }
}
