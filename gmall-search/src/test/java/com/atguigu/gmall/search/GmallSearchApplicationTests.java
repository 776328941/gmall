package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;

import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient pmsClient;

    @Test
    void contextLoads() {
        // 索引库的操作对象
        IndexOperations indexOps = restTemplate.indexOps(Goods.class);

        // 判断是有有索引库
        if (!indexOps.exists()) {
            // 创建索引库
            indexOps.create();
            // 声明映射
            indexOps.putMapping(indexOps.createMapping());
        }

        Integer pageNum = 1; // 页码
        Integer pageSize = 100; // 当前页数量

        /**
         * 本质是将每一个 sku 转换成 goods, 先分批查询 100 条 spu, 遍历每个 spu 并查询出找个 spu 下的所有 sku 将其转换成 goods 保存到 es 中
         * 考虑到数据量较大一次性导入不够合理, 应当分批查询 借助 do while, 每次 查询 100 条 spu. 当 pageSize 不足 100 条时结束循环
         *
         * 假设 db 中有 101 条 spu. 我们首次查询 100 条, pageSize 被设置 为 100 会继续下一次循环导入 第 101 条
         * 假设 db 中有 100 条 spu. 我们首次查询 100 条, pageSize 被设置 为 100 会继续下一次循环 没有数据多一次循环也无所谓
         */
        do {


            pageNum++; // 此页查询完成 查询下一页
            // TODO 查询当前页 spu 总条数 赋值给 pageSize;
        } while (pageSize == 100);// 当前页没有 100 条记录, 结束循环
    }

    // es 数据导入 提供远程接口, 1. 分批、分页查询 spu
    @Test
    public void test() {
        ResponseVo<List<SpuEntity>> spuResponseVo = pmsClient.querySpuByPageJson(
                new PageParamVo(1, 100, null)
        );

        List<SpuEntity> spuEntities = spuResponseVo.getData();
        spuEntities.forEach(System.out::println);
    }
}
