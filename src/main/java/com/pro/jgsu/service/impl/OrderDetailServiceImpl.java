package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.entity.OrderDetail;
import com.pro.jgsu.mapper.OrderDetailMapper;
import com.pro.jgsu.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
