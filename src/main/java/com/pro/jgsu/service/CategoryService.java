package com.pro.jgsu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pro.jgsu.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除菜品分类、套餐信息
     * @param id
     */
    public void remove(Long id);
}
