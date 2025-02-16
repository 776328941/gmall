package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2023/5/7 13:39
 * @Email: moumouguan@gmail.com
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
//    @ResponseBody
    public String loadData(@PathVariable("skuId") Long skuId, Model model) {
        ItemVo itemVo = itemService.loadData(skuId);
        model.addAttribute("itemVo",itemVo);

        return "item";
    }

}