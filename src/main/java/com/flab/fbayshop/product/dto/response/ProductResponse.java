package com.flab.fbayshop.product.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.flab.fbayshop.common.dto.response.PageResponse;
import com.flab.fbayshop.product.model.Category;
import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.model.ProductType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements PageResponse {

    private Long productId;

    private String title;

    private String subtitle;

    private Long sellerId;

    private String sellerNickname;

    private List<ProductType> productTypeList;

    private Long categoryId;

    private List<Category> categoryList;

    private BigDecimal price;

    private BigDecimal sellPrice;

    private String productStatus;

    public static ProductResponse of(Product product) {
        return new ProductResponse(
            product.getProductId()
            , product.getTitle()
            , product.getSubtitle()
            , product.getSeller() == null ? null : product.getSeller().getUserId()
            , product.getSeller() == null ? null : product.getSeller().getNickname()
            , product.getProductTypeCodeList() == null ? null :
            product.getProductTypeCodeList()
                .stream()
                .map(ProductType::findByCode).collect(Collectors.toList())
            , product.getCategoryId()
            , product.getCategoryList()
            , product.getPrice()
            , product.getSellPrice()
            , product.getProductStatus() == null ? null : product.getProductStatus().getCode()
        );
    }

    @Override
    public long getId() {
        return productId;
    }
}
