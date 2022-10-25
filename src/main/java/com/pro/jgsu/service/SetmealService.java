package com.pro.jgsu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.jgsu.dto.SetmealDto;
import com.pro.jgsu.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品关联信息
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐及套餐关联的菜品信息，操作 setmeal 和 setmeal_dish 两张表
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
