package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.pro.jgsu.common.BaseContext;
import com.pro.jgsu.common.R;
import com.pro.jgsu.entity.Orders;
import com.pro.jgsu.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){

        log.info("order数据{}",order);
        orderService.submit(order);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        //构造分页器
        Page pageInfo = new Page(page,pageSize);
        //构造查询器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件，查询当前用户下的订单信息
        long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(Orders::getUserId,currentId);
        //添加排序条件，根据下单使时间降序，使最新订单排在最前
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //调用业务层的分页方法
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
}
