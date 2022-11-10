package com.pro.jgsu.controller;

import com.pro.jgsu.common.R;
import com.pro.jgsu.entity.Orders;
import com.pro.jgsu.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
