package com.atguigu.gmall.oms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/13 04:06
 * @Email: moumouguan@gmail.com
 */
public interface GmallOmsApi {

    @PostMapping("oms/order/save/{userId}")
    public ResponseVo saveOrder(@RequestBody OrderSubmitVo orderSubmitVo, @PathVariable("userId") Long userId);

}
