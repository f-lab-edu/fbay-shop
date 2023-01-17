package com.flab.fbayshop.order.service

import com.flab.fbayshop.error.exception.InvalidParameterException
import com.flab.fbayshop.order.dto.request.OrderCreateRequest
import com.flab.fbayshop.order.mapper.OrderMapper
import com.flab.fbayshop.order.model.Order
import com.flab.fbayshop.product.dto.request.ProductOrderRequest
import com.flab.fbayshop.product.exception.ProductNotFoundException
import com.flab.fbayshop.product.model.Product
import com.flab.fbayshop.product.service.ProductService
import com.flab.fbayshop.user.model.Address
import com.flab.fbayshop.user.model.User
import com.flab.fbayshop.user.service.UserService
import org.spockframework.spring.SpringBean
import spock.lang.Specification

class OrderServiceTest extends Specification {

    @SpringBean
    private OrderMapper orderMapper = Mock()

    @SpringBean
    private UserService userService = Mock()

    @SpringBean
    private ProductService productService = Mock()

    private OrderService orderService = new OrderService(orderMapper, productService, userService)

    private User user = User.builder()
            .userId(1L)
            .nickname("테스트 닉네임1")
            .build()

    private Address address = Address.builder()
            .addressId(1L)
            .roadAddress("주소 테스트")
            .build()

    private List<Product> productList = List.of(
            Product.builder()
                    .productId(1L)
                    .price(BigDecimal.valueOf(13_000))
                    .stock(3)
                    .build(),
            Product.builder()
                    .productId(2L)
                    .price(BigDecimal.valueOf(7_000))
                    .stock(2)
                    .build())

    def "즉시 구매 - 성공"() {
        given:
        userService.getUserById(_ as Long) >> user
        userService.registAddress(_ as Address) >> address
        productService.decreaseStock(_ as List) >> productList
        orderMapper.insertOrder(_) >> 1
        orderMapper.insertOrderDetailList(_) >> productList.size()

        OrderCreateRequest request = new OrderCreateRequest(
                jibunAddress: "지번 주소",
                addressDetail: "주소 상세",
                zoneCode: 1000,
                receiverName: "수신자명",
                receiverContact: "010-0000-0000",
                productList: List.of(
                        new ProductOrderRequest(productId: 1L, quantity: 2),
                        new ProductOrderRequest(productId: 2L, quantity: 1)))
        when:
        Order order = orderService.createOrder(user.getUserId(), request)

        then:
        order.getAddress() == address
        order.getBuyer() == user
        order.getTotalPrice() == BigDecimal.valueOf(33_000)
        order.getOrderDetailList().get(0).getProduct().getProductId() == 1L
        order.getOrderDetailList().get(0).getProduct().getPrice() == BigDecimal.valueOf(13_000)
        order.getOrderDetailList().get(0).getQuantity() == 2
        order.getOrderDetailList().get(0).getTotalPrice() == BigDecimal.valueOf(26_000)

        order.getOrderDetailList().get(1).getProduct().getProductId() == 2L
        order.getOrderDetailList().get(1).getProduct().getPrice() == BigDecimal.valueOf(7_000)
        order.getOrderDetailList().get(1).getQuantity() == 1
        order.getOrderDetailList().get(1).getTotalPrice() == BigDecimal.valueOf(7_000)
    }

    def "즉시 구매 - 실패(상품정보 없음)"() {
        given:
        userService.getUserById(_ as Long) >> user
        userService.registAddress(_ as Address) >> address
        productService.decreaseStock(_ as List) >> null
        orderMapper.insertOrder(_) >> 1
        orderMapper.insertOrderDetailList(_) >> productList.size()

        OrderCreateRequest request = new OrderCreateRequest(
                jibunAddress: "지번 주소",
                addressDetail: "주소 상세",
                zoneCode: 1000,
                receiverName: "수신자명",
                receiverContact: "010-0000-0000",
                productList: List.of(
                        new ProductOrderRequest(productId: 1L, quantity: 2),
                        new ProductOrderRequest(productId: 2L, quantity: 1)))
        when:
        orderService.createOrder(user.getUserId(), request)

        then:
        thrown(ProductNotFoundException)
    }

    def "즉시 구매 - 실패(상품아이디 누락)"() {
        given:
        userService.getUserById(_ as Long) >> user
        userService.registAddress(_ as Address) >> address
        productService.decreaseStock(_ as List) >> productList
        orderMapper.insertOrder(_) >> 1
        orderMapper.insertOrderDetailList(_) >> productList.size()

        OrderCreateRequest request = new OrderCreateRequest(
                jibunAddress: "지번 주소",
                addressDetail: "주소 상세",
                zoneCode: 1000,
                receiverName: "수신자명",
                receiverContact: "010-0000-0000",
                productList: List.of(
                        new ProductOrderRequest(productId: null, quantity: 2),
                        new ProductOrderRequest(productId: 2L, quantity: 1)))
        when:
        orderService.createOrder(user.getUserId(), request)

        then:
        thrown(ProductNotFoundException)
    }

    def "즉시 구매 - 실패(구매수량 누락)"() {
        given:
        userService.getUserById(_ as Long) >> user
        userService.registAddress(_ as Address) >> address
        productService.decreaseStock(_ as List) >> productList
        orderMapper.insertOrder(_) >> 1
        orderMapper.insertOrderDetailList(_) >> productList.size()

        OrderCreateRequest request = new OrderCreateRequest(
                jibunAddress: "지번 주소",
                addressDetail: "주소 상세",
                zoneCode: 1000,
                receiverName: "수신자명",
                receiverContact: "010-0000-0000",
                productList: List.of(
                        new ProductOrderRequest(productId: 1L, quantity: null),
                        new ProductOrderRequest(productId: 2L, quantity: 1)))
        when:
        orderService.createOrder(user.getUserId(), request)

        then:
        thrown(InvalidParameterException)
    }

}
