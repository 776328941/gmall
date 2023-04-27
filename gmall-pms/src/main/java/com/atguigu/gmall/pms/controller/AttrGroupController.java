package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
 * 属性分组
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2023-04-26 01:50:50
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * baseCrud: 7. 根据分类 id 查询分类下的组及规格参数
     *
     * 　请求路径
     * 　　　　http://api.gmall.com/pms/attrgroup/withattrs/225
     * 　　　　　　　　　　　　　　　　/pms/attrgroup/withattrs/{catId}
     *
     * @param cid
     * @return
     */
    @GetMapping("/withattrs/{catId}")
    public ResponseVo<List<AttrGroupEntity>> queryGroupsWithAttrsByCid(@PathVariable("catId") Long cid) {
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.queryGroupsWithAttrsByCid(cid);

        return ResponseVo.ok(attrGroupEntities);
    }

    /**
     * baseCrud: 2. 根据分类 id 查询属性规格分组
     *
     *      请求路径
     *          http://api.gmall.com/pms/attrgroup/category/225
     *                              /pms/attrgroup/category/{cid}
     * @param cid
     * @return
     */
    @GetMapping("/category/{cid}")
    public ResponseVo<List<AttrGroupEntity>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        // select * from pms_attr_group where category_id = cid;
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(
                new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCategoryId, cid)
        );

        return ResponseVo.ok(attrGroupEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrGroupByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = attrGroupService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrGroupEntity> queryAttrGroupById(@PathVariable("id") Long id){
		AttrGroupEntity attrGroup = attrGroupService.getById(id);

        return ResponseVo.ok(attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		attrGroupService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
