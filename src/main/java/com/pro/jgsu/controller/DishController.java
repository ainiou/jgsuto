package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pro.jgsu.common.CustomException;
import com.pro.jgsu.common.R;
import com.pro.jgsu.dto.DishDto;
import com.pro.jgsu.entity.Category;
import com.pro.jgsu.entity.Dish;
import com.pro.jgsu.entity.SetmealDish;
import com.pro.jgsu.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    @Autowired
    private SetmealDishService setmealDishService;

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

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品信息，包括口味信息，操作dish、dish_flavor两种表
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    @PostMapping("/status/{st}")
    public R<String> setStatus(@PathVariable int st,@RequestParam List<Long> ids){
        //处理string 转成Long
//        String[] split = ids.split(",");
//        List<Long> idList = Arrays.stream(split).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        for (Dish dish : list) {
            if (dish!=null)
            {
                dish.setStatus(st);
                dishService.updateById(dish);
            }
        }
        return R.success("操作成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据菜品分类id查询，且菜品为在售状态，即dish.status == 1
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询
        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        //校验删除的菜品中是否含有正在售卖的菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        //统计删除的菜品中在售的菜品数，大于0则不允许删除，抛出业务异常
        int count = dishService.count(queryWrapper);
        if(count > 0){
            throw new CustomException("删除的菜品中含有在售的菜品，请停售后再试");
        }
        //校验是否有套餐关联了菜品，如果关联了套餐则不允许删除，抛出业务异常
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId,ids);

        //List<SetmealDish> list = setmealDishService.list(queryWrapper1);
        //SELECT COUNT( * ) FROM setmeal_dish WHERE (dish_id IN (?))
        int count1 = setmealDishService.count(queryWrapper1);
        if(count1 > 0){
            throw new CustomException("删除的菜品存在于某套餐中，请删除套餐后再试");
        }

        //删除菜品
        dishService.removeByIds(ids);

        return R.success("删除菜品成功");
    }

}
