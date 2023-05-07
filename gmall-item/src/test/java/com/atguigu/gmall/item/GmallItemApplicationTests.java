package com.atguigu.gmall.item;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GmallItemApplicationTests {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private GmallSmsClient smsClient;

    // 1. 根据 skuId 查询 sku
    @Test
    void contextLoads() {
        ResponseVo<SkuEntity> skuEntityResponseVo = pmsClient.querySkuById(13L);
        SkuEntity skuEntity = skuEntityResponseVo.getData();

        System.out.println("skuEntity = " + skuEntity);
    }

    // 2. 根据 sku 中的 三级分类 id 查询 一二三级分类
    @Test
    public void test() {
        ResponseVo<List<CategoryEntity>> categories = pmsClient.queryLvl123CategoriesByCid3(225L);
        List<CategoryEntity> categoryEntities = categories.getData();

        System.out.println("categoryEntities = " + categoryEntities);
    }

    // 3. 根据 sku 中的 品牌 id 查询品牌
    @Test
    public void test2() {
        ResponseVo<BrandEntity> brandEntityResponseVo = pmsClient.queryBrandById(1L);
        BrandEntity brandEntity = brandEntityResponseVo.getData();

        System.out.println("brandEntity = " + brandEntity);
    }

    // 4. 根据 spuId 查询 spu
    @Test
    public void test3() {
        ResponseVo<SpuEntity> spuEntityResponseVo = pmsClient.querySpuById(13L);
        SpuEntity spuEntity = spuEntityResponseVo.getData();

        System.out.println("spuEntity = " + spuEntity);
    }

    // 5. 根据 skuId 查询 sku 图片列表
    @Test
    public void test5() {
        ResponseVo<List<SkuImagesEntity>> listResponseVo = pmsClient.querySkuImagesBySkuId(13L);
        List<SkuImagesEntity> imagesEntities = listResponseVo.getData();

        System.out.println("imagesEntities = " + imagesEntities);
    }

    // 6. 根据 skuId 查询 sku 的所有营销信息
    @Test
    public void test6() {
        ResponseVo<List<ItemSaleVo>> listResponseVo = smsClient.querySalesBySkuId(13L);
        List<ItemSaleVo> itemSaleVos = listResponseVo.getData();

        System.out.println("itemSaleVos = " + itemSaleVos);
    }
}
