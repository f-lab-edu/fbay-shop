package com.flab.fbayshop.order.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.flab.fbayshop.order.model.Order;

@Mapper
public interface OrderMapper {

    Optional<Order> findOrderById(Long orderId);

    int insertOrder(Order order);

    int insertOrderDetailList(Order order);

}
