package com.flab.fbayshop.order.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.flab.fbayshop.order.model.Order;
import com.flab.fbayshop.product.dto.request.ProductOrderRequest;
import com.flab.fbayshop.user.model.Address;
import com.flab.fbayshop.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    private Long addressId;
    private String roadAddress;
    private String jibunAddress;
    private String addressDetail;
    private Integer zoneCode;
    private String receiverName;
    private String receiverContact;

    @NotNull
    private List<@Valid ProductOrderRequest> productList;

    public Order toEntity(User user, Address address) {
        return Order.builder()
            .buyer(user)
            .address(address)
            .build();
    }

    public Address toAddressEntity(User user) {
        return Address.builder()
            .userId(user.getUserId())
            .jibunAddress(this.jibunAddress)
            .roadAddress(this.roadAddress)
            .addressDetail(this.addressDetail)
            .zoneCode(this.zoneCode)
            .receiverName(this.receiverName)
            .receiverContact(this.receiverContact)
            .build();
    }

}
