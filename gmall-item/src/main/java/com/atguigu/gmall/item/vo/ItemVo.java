package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 商品详情页
 * @Author: Guan FuQing
 * @Date: 2023/5/6 18:02
 * @Email: moumouguan@gmail.com
 */
@Data
public class ItemVo {

    // 面包屑所需要字段
    private List<CategoryEntity> categories; // 三级分类

    // 品牌所需字段
    private Long brandId; // 品牌id
    private String brandName; // 品牌名称

    // spu 所需字段
    private Long spuId; // spuId
    private String spuName; // spu 名称

    // 基本信息 所需字段
    private Long skuId; // skuId
    private String title; // 标题
    private String subtitle; // 副标题
    private BigDecimal price; // 价格
    private Integer weight; // 重量

    private String defaultImage; // 默认图片
    private List<SkuImagesEntity> image; // sku 图片列表

    private List<ItemSaleVo> sales; // 营销类型

    private Boolean store = false; // 是否有货

    // [
    //      {attrId: 3, attrName: 机身颜色, attrValues: ['白天白', '暗夜黑']},
    //      {attrId: 4, attrName: 运行内存, attrValues: ['8G', '12G']}
    //      {attrId: 5, attrName: 机身存储, attrValues: ['256G', '512G']}
    // ]
    private List<SaleAttrValueVo> saleAttrs; // 销售属性列表

    // 当前 sku 的销售属性: {3: 白天白, 4: 12G, 5: 256G}
    private Map<Long, String> saleAttr; // 当前 sku 的销售属性

    // 销售属性组合 与 skuId 的映射关系: {'白天白, 12G, 256G': 100, '白天白, 12G, 128G': 101, '白天白, 8G, 512G': 102}
    // 将 spu 下的所有 skuId 进行查询组装，销售属性 + skuId 这种格式，当用户选中相应的销售属性 直接跳转到对应页面
    private String skuJsons;

    // spu 的描述信息
    private List<String> spuImages;

    // 规格参数分组
    private List<ItemGroupVo> groups;

}
