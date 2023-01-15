package com.flab.fbayshop.product.web

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.fbayshop.common.dto.response.ApiResponse
import com.flab.fbayshop.common.dto.response.ErrorResponse
import com.flab.fbayshop.common.dto.response.SliceDto
import com.flab.fbayshop.error.dto.ErrorType
import com.flab.fbayshop.product.dto.request.ProductCreateRequest
import com.flab.fbayshop.product.dto.request.ProductOrderRequest
import com.flab.fbayshop.product.dto.response.ProductDetailResponse
import com.flab.fbayshop.product.dto.response.ProductResponse
import com.flab.fbayshop.product.exception.ProductNotFoundException
import com.flab.fbayshop.product.exception.ProductNotSaleException
import com.flab.fbayshop.product.exception.ProductOutOfStockException
import com.flab.fbayshop.product.model.Product
import com.flab.fbayshop.product.model.ProductStatus
import com.flab.fbayshop.product.model.ProductType
import com.flab.fbayshop.product.service.ProductService
import com.flab.fbayshop.user.dto.request.UserSignupRequest
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.Filter
import java.nio.charset.StandardCharsets

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Slf4j
@Transactional
@SpringBootTest
class ProductControllerTest extends Specification {

    private MockMvc mockMvc

    @Autowired
    private WebApplicationContext ctx

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private Filter springSecurityFilterChain

    @Autowired
    private ProductService productService

    @Autowired
    private UserService userService

    private User user

    def setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .defaultRequest(get("/").with(testSecurityContext()))
                .addFilters(springSecurityFilterChain)
                .build()

        UserSignupRequest request = new UserSignupRequest("test@t.c", "qwer1234!", "테스트", "테스트")
        userService.deleteByEmail(request.getEmail())
        this.user = userService.signupUser(request)
    }

    @WithMockUser(username = "test@t.c", password = "1234")
    def "상품 등록 - 성공"() {

        given:
        String requestStr = "{\"title\": \"상품명1\",\n" +
                "        \"subtitle\": \"상품 부제목1\",\n" +
                "        \"content\": \"상품 상세내용1\",\n" +
                "        \"price\": 10000,\n" +
                "        \"stock\": 10,\n" +
                "        \"product_type_list\": [\n" +
                "                \"A\", \"B\"\n" +
                "        ],\n" +
                "        \"sell_price\": 10000,\n" +
                "        \"category_id\": 1" +
                "        }"

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
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
    def "상품 등록 - 실패 (#cause)"() {
        given:
        def actions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        expect:
        actions.andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))

        where:
        request << [
                new ProductCreateRequest("", "부제목", "내용", 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest(null, "부제목", "내용", 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest(new String(new char[300]), "부제목", "내용", 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", null, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", 10000, 0, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", 10000, null, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", 10000, 1, new ArrayList<>(), 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", 10000, 1, null, 10000, 1L),
                new ProductCreateRequest("제목", "부제목", "내용", 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, null)
        ]
        cause << [
                "제목 미입력",
                "제목 null",
                "제목 길이초과",
                "가격 null",
                "수량 0",
                "수량 null",
                "판매 타입 미입력",
                "판매 타입 null",
                "카테고리 null"
        ]
    }

    @Transactional
    def "상품 목록 조회 - 성공"() {

        given:
        int totalSize = 20
        int size = 6
        for (int i = 1; i <= totalSize; i++) {
            ProductCreateRequest request = new ProductCreateRequest("제목" + i, "부제목" + i, "내용" + i, 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
            productService.registProduct(user.getUserId(), request)
        }

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product?size=" + size)).andDo(MockMvcResultHandlers.print()).andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        ApiResponse<SliceDto<ProductResponse>> apiResponse = objectMapper.readValue(contentStr, new TypeReference<ApiResponse<SliceDto<ProductResponse>>>() {})
        SliceDto<ProductResponse> sliceDto = apiResponse.getData()
        List<ProductResponse> data = sliceDto.getItems()
        data.size() == size
        sliceDto.hasNext == true


    }

    @Transactional
    def "상품 목록 페이징 - 성공"() {
        given:
        int totalSize = 12
        int size = 10
        for (int i = 1; i <= totalSize; i++) {
            ProductCreateRequest request = new ProductCreateRequest("제목" + i, "부제목" + i, "내용" + i, 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
            productService.registProduct(user.getUserId(), request)
        }

        when:
        def result1 = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product?size=" + size)).andReturn()
        String contentStr = result1.getResponse().getContentAsString(StandardCharsets.UTF_8)
        ApiResponse<SliceDto<ProductResponse>> apiResponse1 = objectMapper.readValue(contentStr,
                new TypeReference<ApiResponse<SliceDto<ProductResponse>>>() {})
        SliceDto<ProductResponse> sliceDto1 = apiResponse1.getData()

        then:
        def result2 =  mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product?size=" + size + "&cursor=" + sliceDto1.getNextCursor())).andReturn()
        ApiResponse<SliceDto<ProductResponse>> apiResponse2 = objectMapper.readValue(result2.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<ApiResponse<SliceDto<ProductResponse>>>() {})
        SliceDto<ProductResponse> sliceDto2 = apiResponse2.getData()
        List<ProductResponse> data = sliceDto2.getItems()
        data.size() == totalSize - size
        sliceDto2.hasNext == false
        sliceDto2.nextCursor == null
    }

    @Transactional
    def "상품 상세 조회 - 성공"() {
        given:
        ProductCreateRequest request = new ProductCreateRequest("제목", "부제목", "내용", 10000, 1, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        Product product = productService.registProduct(user.getUserId(), request)

        when:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/" + product.getProductId())).andDo(MockMvcResultHandlers.print()).andReturn()

        then:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        ApiResponse<ProductDetailResponse> apiResponse = objectMapper.readValue(contentStr, new TypeReference<ApiResponse<ProductDetailResponse>>() {})
        ProductDetailResponse response = apiResponse.getData()
        response.getProductId() == product.getProductId()
        response.getTitle() == product.getTitle()
        response.getSubtitle() == product.getSubtitle()
        response.getContent() == product.getContent()
        response.getSellPrice() == product.getSellPrice()
        response.getPrice() == product.getPrice()
        response.getSellerId() == product.getSeller().getUserId()
        response.getSellerNickname() == product.getSeller().getNickname()
        response.getProductTypeList().size() == product.getProductTypeList().size()
    }

    @Transactional
    def "상품 상세 조회 - 실패"() {
        given:
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/-1")).andReturn()

        expect:
        String contentStr = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8)
        def response = objectMapper.readValue(contentStr, ErrorResponse.class)
        response.getCode() == ErrorType.PRODUCT_NOT_FOUND.getCode()
        response.getMessage() == ErrorType.PRODUCT_NOT_FOUND.getMessage()
    }

    @Transactional
    def "상품 재고 감소 - 성공(재고 감소)"() {
        given:
        Integer beforeStock = 10
        Integer orderStock = 5
        ProductCreateRequest createRequest = new ProductCreateRequest("제목", "부제목", "내용", 10000, beforeStock, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        Product beforeProduct = productService.registProduct(user.getUserId(), createRequest)
        ProductOrderRequest orderRequest = new ProductOrderRequest(beforeProduct.getProductId(), orderStock)

        when:
        productService.decreaseStock(List.of(orderRequest))
        Product afterProduct = productService.getProductById(orderRequest.getProductId())

        then:
        afterProduct.getProductId() == beforeProduct.getProductId()
        beforeProduct.getStock() == beforeStock
        afterProduct.getStock() == beforeStock - orderStock
        afterProduct.getProductStatus() == ProductStatus.SALE
    }

    @Transactional
    def "상품 재고 감소 - 성공(품절)"() {
        given:
        Integer beforeStock = 10
        Integer orderStock = 10
        ProductCreateRequest createRequest = new ProductCreateRequest("제목", "부제목", "내용", 10000, beforeStock, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        Product beforeProduct = productService.registProduct(user.getUserId(), createRequest)
        ProductOrderRequest orderRequest = new ProductOrderRequest(beforeProduct.getProductId(), orderStock)

        when:
        productService.decreaseStock(List.of(orderRequest))
        Product afterProduct = productService.getProductById(orderRequest.getProductId())

        then:
        afterProduct.getProductId() == beforeProduct.getProductId()
        beforeProduct.getStock() == beforeStock
        afterProduct.getStock() == 0
        afterProduct.getProductStatus() == ProductStatus.SOLD_OUT
    }

    def "상품 재고 감소 - 실패(상품정보 없음)"() {

        given:
        ProductOrderRequest orderRequest = new ProductOrderRequest(-1L, 1)

        when:
        productService.decreaseStock(List.of(orderRequest))

        then:
        thrown(ProductNotFoundException)
    }

    def "상품 재고 감소 - 실패(재고 없음)"() {
        given:
        Integer beforeStock = 3
        Integer orderStock = 5
        ProductCreateRequest createRequest = new ProductCreateRequest("제목", "부제목", "내용", 10000, beforeStock, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        Product beforeProduct = productService.registProduct(user.getUserId(), createRequest)
        ProductOrderRequest orderRequest = new ProductOrderRequest(beforeProduct.getProductId(), orderStock)

        when:
        productService.decreaseStock(List.of(orderRequest))

        then:
        thrown(ProductOutOfStockException)
    }

    def "상품 재고 감소 - 실패(판매중X)"() {
        given:
        Integer beforeStock = 3
        ProductCreateRequest createRequest = new ProductCreateRequest("제목", "부제목", "내용", 10000, beforeStock, List.of(ProductType.BUY_NOW.getCode(), ProductType.AUCTION.getCode()), 10000, 1L)
        Product beforeProduct = productService.registProduct(user.getUserId(), createRequest)
        ProductOrderRequest orderRequest = new ProductOrderRequest(beforeProduct.getProductId(), beforeStock)
        productService.decreaseStock(List.of(orderRequest))

        when:
        productService.decreaseStock(List.of(orderRequest))

        then:
        thrown(ProductNotSaleException)
    }
}
