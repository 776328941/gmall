package com.atguigu.gmall.pms.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.service.SpuService;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper descMapper; // 1.2 保存 pms_spu_desc 本质与 spu 是同一张表

    @Autowired
    private SpuAttrValueService baseAttrService; // 1.3 保存 pms_spu_attr_value 基本属性值表

    @Autowired
    private SkuMapper skuMapper; // 2.1 保存 pms_sku

    @Autowired
    private SkuImagesService imagesService; // 2.2 保存 pms_sku_images 本质与 sku 是同一张表, 如果不为空才需要保存图片

    @Autowired
    private SkuAttrValueService saleAttrService;  // 2.3 保存 pms_sku_attr_value 销售属性值表

    @Autowired
    private GmallSmsClient smsClient; // 3. 保存 营销 相关信息

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 1. 查本类
     * 　　http://api.gmall.com/pms/spu/category/225?t=1649653070604&pageNum=1&pageSize=10&key=7
     * 　　　　select * from pms_spu where category_id = cid and (id = key or name like '%key%');
     * 　　http://api.gmall.com/pms/spu/category/225?t=1649653070604&pageNum=1&pageSize=10&key=
     * 　　　　select * from pms_spu where category_id = cid;
     * 2. 查全站
     * 　　http://api.gmall.com/pms/spu/category/0?t=1649653070604&pageNum=1&pageSize=10&key=
     * 　　　　select * from pms_spu;
     * 　　http://api.gmall.com/pms/spu/category/0?t=1649653070604&pageNum=1&pageSize=10&key=7
     * 　　　　select * from pms_spu where (id = key or name like '%key%');
     *
     * @param cid
     * @param paramVo
     * @return
     */
    @Override
    public PageResultVo querySpuByCidAndPage(Long cid, PageParamVo paramVo) {
        LambdaQueryWrapper<SpuEntity> wrapper = new LambdaQueryWrapper<>();

        // 当 cid = 0 时, 不拼接查询条件 查询全部分类, 否则 拼接查询条件 查询本类
        if (cid != 0) {
            wrapper.eq(SpuEntity::getCategoryId, cid);
        }

        // 获取查询条件
        String key = paramVo.getKey();

        // 当查询条件不为空时才需要拼接查询条件
        if (StringUtils.isNotBlank(key)) {
            /**
             * 此处的 .and 是消费型函数式接口 Consumer, t -> t.eq 相当于 stream 操作集合. stream t -> 提供的是集合的某一个元素, 而 wrapper 提供的自身
             *      select * from pms_spu where (id = key or name like '%key%');
             */
            wrapper.and(t -> t.eq(SpuEntity::getId, key).or().like(SpuEntity::getName, key));
        }

        /**
         * page 是 IService 提供的分页方法
         *      default <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper)
         *          他有两个参数, 一个是 page, 一个是 wrapper
         *              <E extends IPage<T>> E page 形参, 限定为 IPage 以及他的子类
         *              wrapper 查询条件
         */
        IPage<SpuEntity> page = this.page(
                /**
                 *      // 返回 IPage 对象
                 *      public <T> IPage<T> getPage(){
                 *
                 *         return new Page<>(pageNum, pageSize);
                 *     }
                 */
                paramVo.getPage(),
                // 查询条件
                wrapper
        );

        return new PageResultVo(page);
    }

    @Override
    public void bigSave(SpuVo spu) {
        // 1. 保存 spu 相关信息
        // 1.1 保存 spu 表
        spu.setCreateTime(new Date());
        // 再创建时间会导致不一致, 直接获取上一个设置的时间即可
        spu.setUpdateTime(spu.getCreateTime());
        save(spu); // 由于 `SpuVo` 对象继承自 `SpuEntity` 类，因此可以将 `SpuVo` 对象看作是 `SpuEntity` 对象的一个特殊版本，而 `SpuEntity` 对象可以直接保存到数据库中。因此，`SpuVo` 对象也可以直接保存到数据库中，这并不会产生任何问题。

        // 保存完 spu 后主键回显 抽取 spuId 给以下保存方法使用
        Long spuId = spu.getId();

        // 1.2 保存 pms_spu_desc 本质与 spu 是同一张表(不需要批量新增使用 mapper 即可)
        List<String> spuImages = spu.getSpuImages();
        // spuImages 不为空才进行保存 spu 信息介绍表
        if (CollectionUtils.isNotEmpty(spuImages)) {
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            // 本质与 spu 是同一张表, 没有自己的 Id 需要 设置 spuId
            spuDescEntity.setSpuId(spuId); // 设置图片 id
            // 将集合以 "," 拼接符 拼接到一起形成新的 字符串
            spuDescEntity.setDecript(StringUtils.join(spuImages, ",")); // ["1", "2"] -> "1,2"
            descMapper.insert(spuDescEntity);
        }

        // 1.3 保存 pms_spu_attr_value 基本属性值表(需要使用批量保存使用 service)
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        // baseAttrs 不为空才需要保存 spu 基本信息
        if (CollectionUtils.isNotEmpty(baseAttrs)) {
            baseAttrService.saveBatch(
                    // 将 List<SpuAttrValueVo> 转换为 List<spuAttrValueEntity>
                    baseAttrs.stream().map(spuAttrValueVo -> {
                        SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();

                        // 将 spuAttrValueVo 中的值赋值给 spuAttrValueEntity, 需要在设值前 拷贝, 否则会出现 数据丢失
                        BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity); // 源 -> 对象(从 源中 拷贝到 对象)
                        // 设置 spuId
                        spuAttrValueEntity.setSpuId(spuId);
                        // 设置排序字段
                        spuAttrValueEntity.setSort(
                                // 如果 spuAttrValueVo.getSort() 值为 null 则设置为 0
                                Optional.ofNullable(spuAttrValueVo.getSort()).orElse(0)
                        );

                        return spuAttrValueEntity;
                    }).collect(Collectors.toList())
            );
        }

        // 2. 保存 sku 相关信息
        List<SkuVo> skus = spu.getSkus();
        // skus 不为 null 才进行保存
        if (CollectionUtils.isNotEmpty(skus)) {
            skus.forEach(skuVo -> {
                // 2.1 保存 pms_sku
                skuVo.setSpuId(spuId); // 设置 spuId
                skuVo.setBrandId(spu.getBrandId()); // 设置品牌 Id
                skuVo.setCategoryId(spu.getCategoryId()); // 设置分类 Id

                // 获取 图片列表
                List<String> images = skuVo.getImages();
                // 如果图片列表不为空才设置默认图片
                if (CollectionUtils.isNotEmpty(images)) {
                    skuVo.setDefaultImage(
                            // 判断 默认图片是否为空, 如果不为空设置为默认图片, 如果为空将 images 第一张设置为默认图片. 以后前端设置默认图片无需更改代码
                            Optional.ofNullable(skuVo.getDefaultImage()).orElse(images.get(0))
                    );
                }

                skuMapper.insert(skuVo);

                // 获取 保存后回显的 skuId, 提供给下方使用
                Long skuId = skuVo.getId();

                // 2.2 保存 pms_sku_images 本质与 sku 是同一张表, 如果不为空才需要保存图片
                if (CollectionUtils.isNotEmpty(images)) {
                    imagesService.saveBatch(
                            // 需要将 List<String> 转换为 List<skuImagesEntity>
                            images.stream().map(
                                    image -> {
                                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                                        skuImagesEntity.setSkuId(skuId); // 设置 sku Id
                                        skuImagesEntity.setUrl(image); // 将 集合的每一个图片 设置为 Url
                                        skuImagesEntity.setSort(0); // 设置排序字段

                                        skuImagesEntity.setDefaultStatus( // 设置默认图片
                                                // 将刚刚设置的默认图片与 遍历的图片比对, 相等 为 1 不等 为 0. url 互联网唯一
                                                StringUtils.equals(skuVo.getDefaultImage(), image) ? 1 : 0
                                        );

                                        return skuImagesEntity;
                                    }
                            ).collect(Collectors.toList())
                    );
                }

                // 2.3 保存 pms_sku_attr_value 销售属性值表
                // 2.3 保存 pms_sku_attr_value 销售属性值表
                List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
                if (CollectionUtils.isNotEmpty(saleAttrs)) {
                    saleAttrs.forEach(skuAttrValueEntity -> {
                        skuAttrValueEntity.setSkuId(skuId); // 设置 skuId
                        skuAttrValueEntity.setSort(0); // 设置排序字段
                    });

                    saleAttrService.saveBatch(saleAttrs);
                }

                // 3. 保存 营销 相关信息 远程 -> com/atguigu/gmall/sms/SkuBoundsController
                SkuSaleVo skuSaleVo = new SkuSaleVo();
                BeanUtils.copyProperties(skuVo, skuSaleVo);
                skuSaleVo.setSkuId(skuId);
                smsClient.saveSales(skuSaleVo);
            });
        }
    }

}