package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.common.BaseContext;
import com.pro.jgsu.common.CustomException;
import com.pro.jgsu.entity.*;
import com.pro.jgsu.mapper.OrderMapper;
import com.pro.jgsu.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户提交订单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户id
        long userId = BaseContext.getCurrentId();
        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
            //判断购物车数据是否为空，为空则报 业务异常
        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，无法下单！！");
        }
        //查询当前用户数据
        User currentUser = userService.getById(userId);
        //查询当前地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook currentAddressBook = addressBookService.getById(addressBookId);
        if(currentAddressBook == null){
            throw new CustomException("地址为空,无法下单！！");
        }

        // 生成订单号
        long orderId = IdWorker.getId();

        // 计算订单总金额
        AtomicInteger amount = new AtomicInteger(0); // 保证线程安全
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue()); // +=
            return orderDetail;
        }).collect(Collectors.toList());

        // 向订单表插入数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get())); //总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(currentUser.getName());
        orders.setConsignee(currentAddressBook.getConsignee());
        orders.setPhone(currentAddressBook.getPhone());
        orders.setAddress((currentAddressBook.getProvinceName() == null ? "" : currentAddressBook.getProvinceName())
                + (currentAddressBook.getCityName() == null ? "" : currentAddressBook.getCityName())
                + (currentAddressBook.getDistrictName() == null ? "" : currentAddressBook.getDistrictName())
                + (currentAddressBook.getDetail() == null ? "" : currentAddressBook.getDetail()));
        this.save(orders);

        // 向订单明细表插入数据(多条数据)
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
