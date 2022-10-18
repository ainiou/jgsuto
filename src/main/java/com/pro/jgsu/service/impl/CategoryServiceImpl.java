package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.common.CustomException;
import com.pro.jgsu.entity.Category;
import com.pro.jgsu.entity.Dish;
import com.pro.jgsu.entity.Setmeal;
import com.pro.jgsu.mapper.CategoryMapper;
import com.pro.jgsu.service.CategoryService;
import com.pro.jgsu.service.DishService;
import com.pro.jgsu.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        //查询删除前套餐是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0){
            //当前分类已关联至少一个菜品，抛出异常
            throw new CustomException("当前分类关联了菜品，无法删除。");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            //当前分类至少关联一种套餐，抛出异常
            throw new CustomException("当前分类关联了套餐，无法删除。");
        }
        //无关联，正常删除
        super.removeById(id);

    }
}
