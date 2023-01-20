package com.flab.fbayshop.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.fbayshop.error.exception.InvalidParameterException;
import com.flab.fbayshop.order.dto.request.OrderCreateRequest;
import com.flab.fbayshop.order.mapper.OrderMapper;
import com.flab.fbayshop.order.model.Order;
import com.flab.fbayshop.order.model.OrderDetail;
import com.flab.fbayshop.product.dto.request.ProductOrderRequest;
import com.flab.fbayshop.product.exception.ProductNotFoundException;
import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.service.ProductService;
import com.flab.fbayshop.user.exception.UserProcessException;
import com.flab.fbayshop.user.model.Address;
import com.flab.fbayshop.user.model.User;
import com.flab.fbayshop.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;

    private final ProductService productService;

    private final UserService userService;

    @Transactional
    public Order getOrderById(Long orderId) {
        return orderMapper.findOrderById(orderId).orElseThrow();
    }

    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {

        if (request == null) {
            throw new InvalidParameterException();
        }

        User buyer = userService.getUserById(userId);

        Address address;

        // 주소지 등록
        if (request.getAddressId() == null) {
            address = userService.registAddress(request.toAddressEntity(buyer));
        } else {
            address = userService.findAddressById(request.getAddressId());
        }

        if (address == null) {
            throw new UserProcessException();
        }

        // 상품 재고 차감
        List<Product> productList = productService.decreaseStock(request.getProductList());

        if (productList == null || productList.size() != request.getProductList().size()) {
            throw new ProductNotFoundException();
        }

        BigDecimal totalPrice = BigDecimal.ZERO; // 주문 총 가격
        List<OrderDetail> orderDetailList = new ArrayList<>(); // 주문 상품 리스트

        for (ProductOrderRequest productOrderRequest : request.getProductList()) {
            Long productId = productOrderRequest.getProductId();

            Product product = productList.stream()
                .filter(prod -> prod.getProductId().equals(productId))
                .findAny()
                .orElseThrow(ProductNotFoundException::new);

            if (productOrderRequest.getQuantity() == null || productOrderRequest.getQuantity() <= 0) {
                throw new InvalidParameterException();
            }

            BigDecimal orderDetailTotalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(productOrderRequest.getQuantity()));

            orderDetailList.add(OrderDetail.builder()
                .product(product)
                .quantity(productOrderRequest.getQuantity())
                .unitPrice(product.getPrice())
                .totalPrice(orderDetailTotalPrice).build());

            totalPrice = orderDetailTotalPrice.add(totalPrice);
        }

        Order order = request.toEntity(buyer, address);
        order.setTotalPrice(totalPrice);
        order.setOrderDetailList(orderDetailList);

        // 주문 정보 등록
        int res = orderMapper.insertOrder(order);

        if (res != 1) {
            throw new UserProcessException();
        }

        // 주문 상품리스트 정보 등록
        res = orderMapper.insertOrderDetailList(order);

        if (res != orderDetailList.size()) {
            throw new UserProcessException();
        }

        return order;
    }


}
