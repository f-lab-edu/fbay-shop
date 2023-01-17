package com.flab.fbayshop.order.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.fbayshop.common.dto.response.ApiResponse
import com.flab.fbayshop.common.dto.response.ErrorResponse
import com.flab.fbayshop.order.dto.request.OrderCreateRequest
import com.flab.fbayshop.order.service.OrderService
import com.flab.fbayshop.product.dto.request.ProductCreateRequest
import com.flab.fbayshop.product.dto.request.ProductOrderRequest
import com.flab.fbayshop.product.model.Product
import com.flab.fbayshop.product.model.ProductType
import com.flab.fbayshop.product.service.ProductService
import com.flab.fbayshop.user.dto.request.UserSignupRequest
import com.flab.fbayshop.user.model.Address
import com.flab.fbayshop.user.model.User
import com.flab.fbayshop.user.service.UserService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.Filter
import java.nio.charset.StandardCharsets

import static com.flab.fbayshop.error.dto.ErrorType.UNAUTHORIZED
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Slf4j
@Transactional
@SpringBootTest
class OrderControllerTest extends Specification {

    private MockMvc mockMvc

    @Autowired
    private WebApplicationContext ctx

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private Filter springSecurityFilterChain

    @Autowired
    private OrderService orderService

    @Autowired
    private UserService userService

    @Autowired
    private ProductService productService

    private User user

    private Address address

    private List<Product> productList = new ArrayList<>()

    def setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .defaultRequest(get("/").with(testSecurityContext()))
                .addFilters(springSecurityFilterChain)
                .build()

        UserSignupRequest userSignupRequest = new UserSignupRequest("test@t.c", "qwer1234!", "테스트", "테스트")
        userService.deleteByEmail(userSignupRequest.getEmail())
        this.user = userService.signupUser(userSignupRequest)

        List<ProductCreateRequest> productCreateRequestList = List.of(
                new ProductCreateRequest("상품명1", "부제목", "내용", 13_000, 3, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest("상품명2", "부제목", "내용", 7_000, 2, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        )

        this.productList = new ArrayList<>()
        for (ProductCreateRequest request : productCreateRequestList) {
            this.productList.add(productService.registProduct(this.user.getUserId(), request))
        }

        this.address = userService.registAddress(Address.builder()
                .userId(this.user.getUserId())
                .jibunAddress("지번 주소")
                .roadAddress("도로명 주소")
                .addressDetail("주소 상세")
                .zoneCode(12345)
                .receiverName("주문자명")
                .receiverContact("010-0000-0000")
                .build())
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 성공(주소 등록)"() {
        given:
        String requestStr = "   {" +
                "   \"jibun_address\": \"지번 주소\",\n" +
                "   \"address_detail\": \"주소 상세\",\n" +
                "   \"zone_code\": 12345,\n" +
                "   \"receiver_name\": \"주문자명\",\n" +
                "   \"receiver_contact\": \"010-0000-0000\",\n" +
                "   \"product_list\": [\n" +
                "       {\n" +
                "          \"product_id\": " + productList.get(0).getProductId() + ",\n" +
                "           \"quantity\": 2\n" +
                "       },\n" +
                "       {\n" +
                "           \"product_id\": " + productList.get(1).getProductId() + ",\n" +
                "           \"quantity\": 1\n" +
                "       }\n" +
                "   ]\n" +
                "}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ApiResponse.class).getCode() == HttpStatus.CREATED.value()
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 성공(주소 선택)"() {
        given:
        String requestStr = "   {" +
                "   \"address_id\": " + this.address.getAddressId() + ",\n" +
                "   \"product_list\": [\n" +
                "       {\n" +
                "          \"product_id\": " + productList.get(0).getProductId() + ",\n" +
                "           \"quantity\": 2\n" +
                "       },\n" +
                "       {\n" +
                "           \"product_id\": " + productList.get(1).getProductId() + ",\n" +
                "           \"quantity\": 1\n" +
                "       }\n" +
                "   ]\n" +
                "}"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        objectMapper.readValue(contentStr, ApiResponse.class).getCode() == HttpStatus.CREATED.value()
    }

    def "즉시 구매 - 실패(권한 없음)"() {
        given:
        String requestStr = "   {" +
                "   \"address_id\": " + this.address.getAddressId() + ",\n" +
                "   \"product_list\": [\n" +
                "       {\n" +
                "          \"product_id\": " + productList.get(0).getProductId() + ",\n" +
                "           \"quantity\": 2\n" +
                "       },\n" +
                "       {\n" +
                "           \"product_id\": " + productList.get(1).getProductId() + ",\n" +
                "           \"quantity\": 1\n" +
                "       }\n" +
                "   ]\n" +
                "}"

        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr))

        expect:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(#cause)"() {
        given:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        expect:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))

        where:
        request << [
                new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", new ArrayList<ProductOrderRequest>()),
                new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", null),
//                new OrderCreateRequest(null, null, null, "주소 상세", 12345, "주문자명", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1))),
//                new OrderCreateRequest(null, "도로명 주소", "지번 주소", "", 12345, "주문자명", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1))),
//                new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1))),
//
        ]
        cause << [
                "주문정보 누락",
                "주문정보 null",
        ]

    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(상품아이디 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", List.of(new ProductOrderRequest(null, 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }


    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(주문수량 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(주소정보 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "", null, "주소 상세", 12345, "주문자명", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(주소상세 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "도로명 주소", "지번 주소", "", 12345, "주문자명", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(주문자명 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "", "010-0000-0000", List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "즉시 구매 - 실패(주문자연락처 누락)"() {
        given:
        def request = new OrderCreateRequest(null, "도로명 주소", "지번 주소", "주소 상세", 12345, "주문자명", null, List.of(new ProductOrderRequest(this.productList.get(0).getProductId(), 1)))

        when:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
    }
}
