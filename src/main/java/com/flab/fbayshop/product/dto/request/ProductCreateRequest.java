package com.flab.fbayshop.product.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.model.ProductType;
import com.flab.fbayshop.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "{spring.validation.title.NotBlank.message}")
    @Size(max = 200, message = "{spring.validation.title.Max.message}")
    private String title;

    @Size(max =500, message = "{spring.validation.subtitle.Max.message}")
    private String subtitle;

    @Size(max = 3000, message = "{spring.validation.content.Max.message}")
    private String content;

    @NotNull(message = "{spring.validation.price.NotNull.message}")
    @DecimalMin(value = "0", message = "{spring.validation.price.Min.message}")
    private BigDecimal price;

    @NotNull(message = "{spring.validation.stock.NotNull.message}")
    @Min(value = 1, message = "{spring.validation.stock.Min.message}")
    @Max(value = Integer.MAX_VALUE, message = "{spring.validation.stock.Max.message}")
    private Integer stock;

    @NotNull(message = "{spring.validation.productType.NoSelected.message}")
    @NotEmpty(message = "{spring.validation.productType.NoSelected.message}")
    private List<String> productTypeList;

    @DecimalMin(value = "0", message = "{spring.validation.price.Min.message}")
    private BigDecimal sellPrice;

    @NotNull(message = "{spring.validation.category.NoSelected.message}")
    private Long categoryId;

    public Product toEntity(User seller) {
        return Product.builder()
            .title(this.title)
            .subtitle(this.subtitle)
            .content(this.content)
            .price(this.price)
            .stock(this.stock)
            .productTypeList(
                this.productTypeList.stream().map(ProductType::findByCode).collect(Collectors.toList()))
            .categoryId(categoryId)
            .seller(seller)
            .sellPrice(this.sellPrice)
            .build();
    }
}
