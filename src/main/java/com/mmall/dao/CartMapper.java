package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    /**根据商品ID和用户ID查购物车*/
    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId")Integer productId);
    /**根据用户ID查购物车*/
    List<Cart> selectCartByUserId(Integer userId);
    /**查商品是否被勾选*/
    int selectCartProductCheckedStatusByUserId(Integer userId);int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList")List<String> productIdList);
    /**检查未勾选的商品*/
    int checkedOrUncheckedProduct(@Param("userId") Integer userId,@Param("productId")Integer productId,@Param("checked") Integer checked);
    /**检查购物车商品的数量*/
    int selectCartProductCount(@Param("userId") Integer userId);
    /**检查勾选的商品*/
    List<Cart> selectCheckedCartByUserId(Integer userId);


}