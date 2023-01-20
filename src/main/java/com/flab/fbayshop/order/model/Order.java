package com.flab.fbayshop.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.flab.fbayshop.user.model.Address;
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
public class Order {

    private Long orderId;

    private User buyer;

    private Address address;

    private String orderStatus;

    private BigDecimal totalPrice;

    private List<OrderDetail> orderDetailList;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Order(Long orderId, User buyer, Address address, String orderStatus, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.buyer = buyer;
        this.address = address;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }

}
