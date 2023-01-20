package com.flab.fbayshop.user.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Address {

    private Long addressId;

    private String roadAddress;

    private String jibunAddress;

    private String addressDetail;

    private Integer zoneCode;

    private String receiverName;

    private String receiverContact;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Address(Long addressId, String roadAddress, String jibunAddress, String addressDetail, Integer zoneCode,
        String receiverName, String receiverContact, Long userId) {
        this.addressId = addressId;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.addressDetail = addressDetail;
        this.zoneCode = zoneCode;
        this.receiverName = receiverName;
        this.receiverContact = receiverContact;
        this.userId = userId;
    }

}
