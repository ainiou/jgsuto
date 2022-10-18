package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pro.jgsu.common.R;
import com.pro.jgsu.dto.DishDto;
import com.pro.jgsu.entity.Category;
import com.pro.jgsu.entity.Dish;
import com.pro.jgsu.service.CategoryService;
import com.pro.jgsu.service.DishFlavorService;
import com.pro.jgsu.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //根据更新时间排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝，使用 DishDto 对象来获取 category_name 属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //拿到 pageInfo 中的数据集合
        List<Dish> records = pageInfo.getRecords();
        //处理数据集合，加上 category_name 属性,然后赋给新的 dishDto 集合
        List<DishDto> list = records.stream().map(item ->{
            //定义一个DishDto对象，将 item 拷贝到dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);


            Long categoryId = item.getCategoryId();//获取菜品分类id
            //根据分类id拿到category对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();//获取分类名称
                dishDto.setCategoryName(categoryName);//将分类名称赋给 dishDto 的CategoryName 属性
            }

            return dishDto;
        }).collect(Collectors.toList());
        //将得到的新DishDto集合赋给 dishDtoPage 的records属性
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
}
