package com.sky.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
        //口味重排序再重组合，保证口味一致性
        if (dishFlavor != null) {
            String[] split = dishFlavor.split(",");
            Arrays.sort(split);
            dishFlavor = String.join(",", split);
            shoppingCart.setDishFlavor(dishFlavor);
        }

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId!= null,ShoppingCart::getUserId,userId)
                .eq(dishId!= null,ShoppingCart::getDishId,dishId)
                .eq(setmealId!= null,ShoppingCart::getSetmealId,setmealId)
                .eq(dishFlavor!= null,ShoppingCart::getDishFlavor,dishFlavor);
        List<ShoppingCart> list =shoppingCartMapper.selectList(queryWrapper);

        //相同存在，加number
        if (list != null && list.size() > 0) {
            ShoppingCart  cart =new ShoppingCart();
            cart.setId(list.get(0).getId());
            cart.setNumber(list.get(0).getNumber() + 1);
            shoppingCartMapper.updateById(cart);
        }
        else {
            //不存在，再插入
            //判断是菜品还是套餐
            //Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //查菜品信息放入冗余字段
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());



            }
            else {
                //查套餐信息放入冗余字段
                // setmealId = shoppingCartDTO.getSetmealId();

                Setmeal setmeal=setmealMapper.selectById(setmealId);

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());

            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(shoppingCart);
        }






    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        return shoppingCartMapper.selectList(queryWrapper);

    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartMapper.delete(queryWrapper);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
//        if (dishFlavor != null) {
//            String[] split = dishFlavor.split(",");
//            Arrays.sort(split);
//            dishFlavor = String.join(",", split);
//            shoppingCart.setDishFlavor(dishFlavor);
//        }
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId!= null,ShoppingCart::getUserId,userId)
                .eq(dishId!= null,ShoppingCart::getDishId,dishId)
                .eq(setmealId!= null,ShoppingCart::getSetmealId,setmealId)
                .eq(dishFlavor!= null,ShoppingCart::getDishFlavor,dishFlavor);
        List<ShoppingCart> list =shoppingCartMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            ShoppingCart cart =new ShoppingCart();
            cart.setId(list.get(0).getId());
            cart.setNumber(list.get(0).getNumber() - 1);
            if (cart.getNumber() > 0) {
                shoppingCartMapper.updateById(cart);
            }
            else {
                shoppingCartMapper.deleteById(cart.getId());
            }
        }
    }


}
