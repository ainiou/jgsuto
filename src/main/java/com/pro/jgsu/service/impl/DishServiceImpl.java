package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.dto.DishDto;
import com.pro.jgsu.entity.Dish;
import com.pro.jgsu.entity.DishFlavor;
import com.pro.jgsu.mapper.DishMapper;
import com.pro.jgsu.service.DishFlavorService;
import com.pro.jgsu.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品信息时同时保存菜品口味信息，同时操作两张表，开启事务处理
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到 dish 表
        this.save(dishDto);
        //拿到保存后自动生成的dishId
        Long dishId = dishDto.getId();
        //根据 dishId 保存口味信息
        //利用 stream 流依次将 dishId 存入每一条口味信息中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存口味信息到 dish_flavor 表
        dishFlavorService.saveBatch(flavors);
    }
}
