package com.flab.fbayshop.product.model;

import java.math.BigDecimal;
import java.util.List;

import com.flab.fbayshop.user.model.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Product {

    private Long productId;

    private String title;

    private String subtitle;

    private String content;

    private Long categoryId;

    private List<Category> categoryList;

    private User seller;

    private List<ProductType> productTypeList;

    private List<String> productTypeCodeList;

    private BigDecimal price;

    private Integer stock;

    private BigDecimal sellPrice;

    private String createdAt;

    private String updatedAt;

    @Builder
    public Product(Long productId, String title, String subtitle, String content, Long categoryId,
        List<Category> categoryList, User seller, List<ProductType> productTypeList, List<String> productTypeCodeList, BigDecimal price, Integer stock,
        BigDecimal sellPrice, String createdAt, String updatedAt) {
        this.productId = productId;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.categoryId = categoryId;
        this.categoryList = categoryList;
        this.seller = seller;
        this.productTypeList = productTypeList;
        this.productTypeCodeList = productTypeCodeList;
        this.price = price;
        this.stock = stock;
        this.sellPrice = sellPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
