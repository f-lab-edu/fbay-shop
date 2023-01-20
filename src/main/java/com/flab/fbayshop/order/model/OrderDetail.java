package com.flab.fbayshop.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.flab.fbayshop.product.model.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderDetail {

    private Long orderDetailId;

    private Long orderId;

    private Product product;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String detailStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public OrderDetail(Long orderDetailId, Long orderId, Product product, BigDecimal unitPrice, Integer quantity,
        BigDecimal totalPrice, String detailStatus) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.detailStatus = detailStatus;
    }
}
