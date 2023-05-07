package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/7 22:17
 * @Email: moumouguan@gmail.com
 */
public interface GmallUmsApi {

    // 1. 用户登陆
    @GetMapping("ums/user/query")
    public ResponseVo<UserEntity> queryUser(
            @RequestParam("loginName")String loginName,
            @RequestParam("password")String password
    );

    // 2. 用户注册
    @PostMapping("ums/user/register")
    public ResponseVo<Object> register(UserEntity userEntity, @RequestParam("code") String code);

    // 3. 校验数据是否可用
    @GetMapping("ums/user/check/{data}/{type}")
    public ResponseVo<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type);
}
