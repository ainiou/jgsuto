package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pro.jgsu.common.R;
import com.pro.jgsu.entity.Category;
import com.pro.jgsu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //根据sort排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){

        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){

    categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据category 的type属性查询
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件，按 sort属性排序，若相同则按最后更新时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

}
