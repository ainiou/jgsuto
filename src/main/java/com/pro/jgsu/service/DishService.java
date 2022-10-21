package com.pro.jgsu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.jgsu.dto.DishDto;
import com.pro.jgsu.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，插入一个菜品的同时添加对应的口味信息
    public void saveWithFlavor(DishDto dishDto);

    //根据ID查询菜品同时返回口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品，同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);
}
