package com.flab.fbayshop.product.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.flab.fbayshop.common.dto.request.PageRequest;
import com.flab.fbayshop.product.model.Product;

@Mapper
public interface ProductMapper {

    void insertProduct(Product product);

    void insertProductType(Product product);

    Optional<Product> findProductById(Long productId);

    List<Product> selectProductList(PageRequest request);
}
