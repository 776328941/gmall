package com.atguigu.gmall.pms.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SpuDescService;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("spuDescService")
public class SpuDescServiceImpl extends ServiceImpl<SpuDescMapper, SpuDescEntity> implements SpuDescService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuDescEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuDescEntity>()
        );

        return new PageResultVo(page);
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional
    public void saveSpuDesc(SpuVo spu, Long spuId) {
        List<String> spuImages = spu.getSpuImages();
        // spuImages 不为空才进行保存 spu 信息介绍表
        if (CollectionUtils.isNotEmpty(spuImages)) {
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            // 本质与 spu 是同一张表, 没有自己的 Id 需要 设置 spuId
            spuDescEntity.setSpuId(spuId); // 设置图片 id
            // 将集合以 "," 拼接符 拼接到一起形成新的 字符串
            spuDescEntity.setDecript(StringUtils.join(spuImages, ",")); // ["1", "2"] -> "1,2"
//            descMapper.insert(spuDescEntity);
            save(spuDescEntity);
        }
    }
}