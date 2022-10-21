package com.pro.jgsu.dto;

import com.pro.jgsu.entity.Setmeal;
import com.pro.jgsu.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
