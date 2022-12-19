package com.flab.fbayshop.product.service

import com.flab.fbayshop.common.dto.request.PageRequest
import com.flab.fbayshop.common.dto.response.SliceDto
import com.flab.fbayshop.product.dto.request.ProductCreateRequest
import com.flab.fbayshop.product.dto.response.ProductResponse
import com.flab.fbayshop.product.exception.ProductNotFoundException
import com.flab.fbayshop.product.mapper.ProductMapper
import com.flab.fbayshop.product.model.Product
import com.flab.fbayshop.product.model.ProductType
import com.flab.fbayshop.user.exception.UserNotFoundException
import com.flab.fbayshop.user.model.User
import com.flab.fbayshop.user.service.UserService
import org.spockframework.spring.SpringBean
import spock.lang.Specification

import java.util.stream.Collectors

class ProductServiceTest extends Specification {

    @SpringBean
    private ProductMapper productMapper = Mock()

    @SpringBean
    private UserService userService = Mock()

    private ProductService productService = new ProductService(productMapper, userService)

    def "상품 등록 - 성공"() {
        given:
        Long userId = 1L
        ProductCreateRequest request = new ProductCreateRequest("상품명", "상품 부제목", "상품 설명", 10000 as BigDecimal, 10, List.of(ProductType.BUY_NOW.code), null, 1L)

        User seller = User.builder().userId(userId).nickname("테스트 닉네임1").build()
        userService.getUserById(_ as Long) >> seller
        productMapper.insertProduct(_ as Product) >> request.toEntity(seller)

        when:
        Product savedProduct = productService.registProduct(userId, request)

        then:
        println savedProduct
        savedProduct != null
        savedProduct.getTitle() == request.getTitle()
        savedProduct.getSubtitle() == request.getSubtitle()
        savedProduct.getContent() == request.getContent()
        savedProduct.getCategoryId() == request.getCategoryId()
        savedProduct.getSeller().getUserId() == userId
        for (int i = 0; i < savedProduct.getProductTypeList().size(); i++) {
            savedProduct.getProductTypeList().get(i).getCode() == request.getProductTypeList().get(i)
        }
        savedProduct.getPrice() == request.getPrice()
        savedProduct.getStock() == request.getStock()
        savedProduct.getSellPrice() == request.getSellPrice()

    }

    def "상품 등록 - 실패(회원정보 없음)"() {
        given:
        Long userId = 1L
        userService.getUserById(userId) >> { throw new UserNotFoundException() }

        when:
        productService.registProduct(userId, new ProductCreateRequest())

        then:
        thrown(UserNotFoundException)
    }

    def "상품 목록 조회 - 성공"() {
        given:
        int size = 6
        int totalSize = 20
        PageRequest request = new PageRequest(1, null, null, size, null, null)

        List<Product> productList = new ArrayList<>()
        for (int i = 1; i <= totalSize; i++) {
            productList.add(Product.builder().productId((long) i).build())
        }
        productMapper.selectProductList(_ as PageRequest) >> productList.subList(0, request.getSize() + 1)

        when:
        List<ProductResponse> list = productService.selectProductList(request).stream().map(product -> ProductResponse::of).collect(Collectors.toList())
        def sliceDto = SliceDto.of(list, request)

        then:
        sliceDto.getItems().size() == request.getSize()
        sliceDto.hasNext
    }

    def "상품 목록 페이징 조회 - 성공"() {
        given:
        int pageSize = 6
        int totalSize = 20
        long cursorId = 18L

        PageRequest request = new PageRequest(1, null, cursorId, pageSize, null, null)

        List<Product> productList = new ArrayList<>()
        for (int i = 1; i <= totalSize; i++) {
            productList.add(Product.builder().productId((long) i).build())
        }
        productMapper.selectProductList(_ as PageRequest) >> productList.subList((int) cursorId, (int) Math.min(productList.size(), cursorId + (request.getSize() + 1)))

        when:
        List<ProductResponse> list = productService.selectProductList(request).stream().map(product -> ProductResponse::of).collect(Collectors.toList())
        SliceDto<ProductResponse> sliceDto = SliceDto.of(list, request)

        then:
        sliceDto.getItems().size() < request.getSize()
        sliceDto.getItems().size() == totalSize - (int) cursorId
        !sliceDto.hasNext
    }

    def "상품 상세 조회 - 성공"() {
        given:
        Long productId = 1L
        Product product = Product.builder()
                .productId(productId)
                .build()
        productMapper.findProductById(productId) >> Optional.of(product)

        when:
        Product savedProduct = productService.getProductById(productId)

        then:
        savedProduct == product
    }

    def "상품 상세 조회 - 실패(상품정보 없음)"() {
        given:
        Long productId = 1L
        productMapper.findProductById(productId) >> Optional.empty()

        when:
        productService.getProductById(productId)

        then:
        thrown(ProductNotFoundException)
    }

}
