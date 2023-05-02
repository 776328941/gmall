package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;
import com.atguigu.gmall.pms.vo.SpuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * spu信息
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2023-04-26 01:50:50
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spu")
public class SpuController {

    // 在 Spring 中通过 @Autowired 注入的都是代理类对象, 因为通过代理类调用方法 事务注解才会生效
    // 注意在 Spring 中默认是 JDK 代理。 但是 Spring Boot 2.x 以及之后都是 CGLiB 代理 更加通用
    @Autowired
    private SpuService spuService;

    /**
     * baseCrud: 4. 分页查询 spu 列表
     *
     * 　请求路径
     * 　　　　http://api.gmall.com/pms/spu/category/0?t=1668931202224&pageNum=1&pageSize=10&key=
     * 　　　　　　　　　　　　　　　　/pms/spu/category/{categoryId}
     * 　参数类型
     * 　　　　占位符: /{xxx} -> @PathVariable("xxx")
     * 　　　　普通参数: ?xxx=xxx、form -> @RequestParam(xxx) 、对象直接接收
     * 　　　　json: @RequestBody 对象
     * 　　　　cookie: @CookieValue(value = "参数名称", required = "是否必须, 默认是 ture", defaultValue = "指定默认值")
     * 　　　　请求头信息: @RequestHeader(value = "参数名称", required = "是否必须, 默认是 ture", defaultValue = "指定默认值")
     *
     * @param cid
     * @param paramVo
     * @return
     */
    @GetMapping("/category/{categoryId}")
    public ResponseVo<PageResultVo> querySpuByCidAndPage(@PathVariable("categoryId") Long cid, PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.querySpuByCidAndPage(cid, paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySpuByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * es 数据导入 提供远程接口, 1. 分批、分页查询 spu
     *      阉割版的 http 协议, 支持占位符, 支持普通参数, 不支持 form 表单
     *
     *      普通参数 ? 只能使用 @requestParam 一一接收
     * 		     不支持 fome 表单传入对象
     *
     *      json: 传递多个参数, @RequestBody 接收, 只支持 Post 请求
     */
    @PostMapping("/json")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo) {
        PageResultVo pageResultVo = spuService.queryPage(paramVo);

        return ResponseVo.ok((List<SpuEntity>) pageResultVo.getList());
    }


    /**
     * 信息
     * es 数据同步, 根据 spuId 查询 spu
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id){
		SpuEntity spu = spuService.getById(id);

        return ResponseVo.ok(spu);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SpuVo spu) {
        // 大保存方法是通过 代理类调用的 进而 bigSave 方法 事务注解才能生效, 方法前后添加事务代码(回滚 提交)
		spuService.bigSave(spu);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuEntity spu){
		spuService.updateById(spu);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		spuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
