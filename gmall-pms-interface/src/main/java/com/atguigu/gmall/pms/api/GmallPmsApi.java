package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/1 04:52
 * @Email: moumouguan@gmail.com
 */
public interface GmallPmsApi {

    /**
     * es 数据导入 提供远程接口, 1. 分批、分页查询 spu
     *      阉割版的 http 协议, 支持占位符, 支持普通参数, 不支持 form 表单
     *
     *      普通参数 ? 只能使用 @requestParam 一一接收
     * 		     不支持 fome 表单传入对象
     *
     *      json: 传递多个参数, @RequestBody 接收, 只支持 Post 请求
     */
    @PostMapping("pms/spu/json")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    // es 数据同步, 根据 spuId 查询 spu
    // 商品详情页 4. 根据 spuId 查询 spu
    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    // 商品详情页 1. 根据 skuId 查询 sku
    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    // es 数据导入 提供远程接口, 2. 根据 spuId 查询 sku
    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> querySkuBySpuId(@PathVariable("spuId") Long spuId);

    // 商品详情页 5. 根据 skuId 查询 sku 图片列表
    @GetMapping("pms/skuimages/sku/{skuId}")
    public ResponseVo<List<SkuImagesEntity>> querySkuImagesBySkuId(@PathVariable("skuId") Long skuId);

    // es 数据导入 提供远程接口, 4. 根据 品牌id 查询 品牌
    // 商品详情页 3. 根据 sku 中的 品牌 id 查询品牌
    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    // es 数据导入 提供远程接口, 5. 根据 分类id 查询 分类
    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    // index 加载一级分类
    @GetMapping("pms/category/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByPid(@PathVariable("parentId") Long pid);

    // index 根据 pid 查询 二级分类以及二级分类的子分类
    @GetMapping("pms/category/level23/{pid}")
    public ResponseVo<List<CategoryEntity>> queryLevel23CategoriesByPid(@PathVariable("pid") Long pid);

    // 商品详情页 2. 根据 sku 中的 三级分类 id 查询 一二三级分类
    @GetMapping("pms/category/lvl123/{cid3}")
    public ResponseVo<List<CategoryEntity>> queryLvl123CategoriesByCid3(@PathVariable("cid3") Long cid3);

    // es 数据导入 提供远程接口, 6. 查询 销售类型的检索属性和值
    @GetMapping("pms/skuattrvalue/search/attr/value/{cid}")
    public ResponseVo<List<SkuAttrValueEntity>> querySearchAttrValueByCidAndSkuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId
    );

    // 商品详情页 8. 根据 sku 中的 spuId 查询 spu 下的所有销售属性
    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySaleAttrValuesBySpuId(@PathVariable("spuId") Long spuId);

    // 商品详情页 9. 根据 skuId 查询当前 sku 的销售属性
    @GetMapping("pms/skuattrvalue/sku/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySaleAttrValuesBySkuId(@PathVariable("skuId") Long skuId);

    // es 数据导入 提供远程接口, 7. 查询 基本类型的检索属性和值
    @GetMapping("pms/spuattrvalue/search/attr/value/{cid}")
    public ResponseVo<List<SpuAttrValueEntity>> querySearchAttrValueByCidAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("spuId") Long spuId
    );
}