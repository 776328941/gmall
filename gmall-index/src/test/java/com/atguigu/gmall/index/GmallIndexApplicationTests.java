package com.atguigu.gmall.index;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GmallIndexApplicationTests {

    @Autowired
    private GmallPmsClient gmallClient;

    @Test
    void contextLoads() {
        ResponseVo<List<CategoryEntity>> listResponseVo = gmallClient.queryLevel23CategoriesByPid(2L);
        List<CategoryEntity> data = listResponseVo.getData();
        data.forEach(System.out::println);
    }

}
