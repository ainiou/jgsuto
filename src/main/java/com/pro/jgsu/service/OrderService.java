package com.pro.jgsu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.jgsu.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户提交订单去支付
     * @param orders
     */
    public void submit(Orders orders);
}
