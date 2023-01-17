package com.flab.fbayshop.order.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.flab.fbayshop.order.dto.request.OrderCreateRequest;

@Component
public class OrderCreateValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(OrderCreateRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        OrderCreateRequest request = (OrderCreateRequest)target;

        if (request.getProductList() == null || request.getProductList().size() == 0) {
            errors.rejectValue("productList", "invalid productList", new Object[] {request.getProductList()},
                "주문 상품 정보가 없습니다.");
        }

        if (request.getAddressId() != null) {
            return;
        }

        if (!StringUtils.hasText(request.getJibunAddress()) && !StringUtils.hasText(request.getRoadAddress())) {
            errors.rejectValue("jibunAddress", "invalid address", new Object[] {request.getJibunAddress()},
                "주소가 등록되지 않았습니다.");
        }

        if (!StringUtils.hasText(request.getAddressDetail())) {
            errors.rejectValue("addressDetail", "invalid addressDetail",
                new Object[] {request.getAddressDetail()}, "상세 주소가 입력되지 않았습니다.");
        }

        if (!StringUtils.hasText(request.getReceiverName())) {
            errors.rejectValue("receiverName", "invalid receiverName",
                new Object[] {request.getReceiverName()}, "주문자명이 입력되지 않았습니다.");
        }

        if (!StringUtils.hasText(request.getReceiverContact())) {
            errors.rejectValue("receiverContact", "invalid receiverContact",
                new Object[] {request.getReceiverContact()}, "주문자 연락처가 입력되지 않았습니다.");
        }

    }
}
