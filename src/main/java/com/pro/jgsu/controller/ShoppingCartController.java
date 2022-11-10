package com.pro.jgsu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pro.jgsu.common.BaseContext;
import com.pro.jgsu.common.CustomException;
import com.pro.jgsu.common.R;
import com.pro.jgsu.entity.ShoppingCart;
import com.pro.jgsu.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.callback.LanguageCallback;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 新增购物车记录
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //购物车数据ShoppingCart(id=null, name=口味蛇, userId=null, dishId=1397851668262465537, setmealId=null,
        // dishFlavor=热饮, number=null, amount=168, image=0f4bd884-dc9c-4cf9-b59e-7d5958fec3dd.jpg, createTime=null)
        //log.info("购物车数据{}",shoppingCart);
        //获得当前用户id，指定购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询当前菜品/套餐是否已经与当前用户关联
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //判断当前添加的是套餐还是菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else if(setmealId != null){
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }else {
            throw new CustomException("业务异常！！！");
        }
        //判断当前菜品的口味信息
        //queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);


        //如果存在，则在原有shoppingCart中加一
        if(cart != null){
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shoppingCartService.updateById(cart);
        }else {
            //如果不存在，则新增shoppingCart数据,并设置数量为一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return R.success(cart);
    }

    /**
     * 减少购物车菜品或套餐
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        ShoppingCart cart = null;

        // 获取要减少的菜品或套餐信息
        Long dishId = shoppingCart.getDishId();
        if(dishId == null){
            // 减少的是套餐数量
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
            cart = shoppingCartService.getOne(queryWrapper);
        } else {
            // 减少的是菜品数量
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            cart = shoppingCartService.getOne(queryWrapper);
        }

        Integer number = cart.getNumber();
        if(number > 1){
            cart.setNumber(number - 1);
            shoppingCartService.updateById(cart);
        } else {
            shoppingCartService.removeById(cart.getId());
        }

        return R.success(cart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功!");
    }
}
