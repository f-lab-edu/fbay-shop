package com.flab.fbayshop.product.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.flab.fbayshop.product.model.Category;
import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.model.ProductType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse implements Serializable {

    private Long productId;

    private String title;

    private String subtitle;

    private String content;

    private Long categoryId;

    private List<Category> categoryList;

    private Long sellerId;

    private String sellerNickname;

    private List<ProductType> productTypeList;

    private BigDecimal price;

    private BigDecimal sellPrice;

    private String createdAt;

    private String updatedAt;


    public static ProductDetailResponse of(Product product) {
        return new ProductDetailResponse(product.getProductId(), product.getTitle(), product.getSubtitle(),
            product.getContent(), product.getCategoryId(), product.getCategoryList(),
            product.getSeller() == null ? null : product.getSeller().getUserId(),
            product.getSeller() == null ? null : product.getSeller().getNickname(),
            product.getProductTypeCodeList() == null ? null :
                product.getProductTypeCodeList()
                    .stream()
                    .map(ProductType::findByCode).collect(Collectors.toList()),
            product.getPrice(), product.getSellPrice(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }

}
