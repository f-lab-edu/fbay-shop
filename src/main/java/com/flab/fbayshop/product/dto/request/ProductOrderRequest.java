package com.flab.fbayshop.product.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderRequest {

    @NotNull(message = "{spring.validation.productId.NoSelected.message}")
    private Long productId;

    @NotNull(message = "{spring.validation.quantity.NotNull.message}")
    @Min(value = 1, message = "{spring.validation.quantity.Min.message}")
    private Integer quantity;
}
