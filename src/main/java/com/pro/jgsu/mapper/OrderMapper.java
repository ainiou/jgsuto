package com.pro.jgsu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pro.jgsu.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
