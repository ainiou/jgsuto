package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.common.CustomException;
import com.pro.jgsu.dto.SetmealDto;
import com.pro.jgsu.entity.Setmeal;
import com.pro.jgsu.entity.SetmealDish;
import com.pro.jgsu.mapper.SetmealMapper;
import com.pro.jgsu.service.SetmealDishService;
import com.pro.jgsu.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal表
        this.save(setmealDto);
        //保存套餐与菜品关联信息，操作setmeal_dish表
        //获取套餐内的菜品列表，并依次赋上套餐id，
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，存入setmeal_dish表中
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态是否停售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);//得到ids列表中对应套餐的状态，若count>0,则证明有套餐在售
        if(count > 0){
            //如果不能删除抛出业务异常
            throw new CustomException("选中的套餐存在正在售卖的套餐，请先停售后再试！");
        }
        //删除 setmeal 中的数据
        this.removeByIds(ids);
        //删除套餐与菜品的关联数据，删 setmeal_dish
        //根据setmealid删除对应的setmeal_dish 数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }
}
