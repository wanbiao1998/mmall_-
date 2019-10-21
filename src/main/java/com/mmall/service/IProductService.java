package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * @author wb
 * @create 2019-10-10 14:24
 */
public interface IProductService {
    /**新增或更新商品*/
    ServerResponse saveOrUpdateProduct(Product product);
    /**商品销售的状态*/
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    /**获取商品详情*/
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    /**商品分页 PageHelper*/
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    /**搜索功能：搜索商品*/
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    /**获取商品详情*/
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
