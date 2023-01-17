package com.flab.fbayshop.order.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flab.fbayshop.auth.dto.AuthUser;
import com.flab.fbayshop.auth.dto.UserInfo;
import com.flab.fbayshop.common.dto.response.ApiResponse;
import com.flab.fbayshop.order.dto.request.OrderCreateRequest;
import com.flab.fbayshop.order.service.OrderService;
import com.flab.fbayshop.order.validator.OrderCreateValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
public class OrderController {

    private final OrderCreateValidator orderCreateValidator;
    private final OrderService orderService;

    @InitBinder("orderCreateRequest")
    protected void initOrderCreateBinder(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(orderCreateValidator);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@AuthUser UserInfo userInfo,
        @Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        return ApiResponse.created(orderService.createOrder(userInfo.getId(), orderCreateRequest).getOrderId());
    }
}
