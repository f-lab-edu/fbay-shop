package com.flab.fbayshop.product.controller;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flab.fbayshop.auth.dto.AuthUser;
import com.flab.fbayshop.auth.dto.UserInfo;
import com.flab.fbayshop.common.dto.request.PageRequest;
import com.flab.fbayshop.common.dto.response.ApiResponse;
import com.flab.fbayshop.common.dto.response.SliceDto;
import com.flab.fbayshop.product.dto.request.ProductCreateRequest;
import com.flab.fbayshop.product.dto.response.ProductDetailResponse;
import com.flab.fbayshop.product.dto.response.ProductResponse;
import com.flab.fbayshop.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> selectProductList(PageRequest request) {
        return ApiResponse.ok(SliceDto.of(
            productService.selectProductList(request).stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList()), request));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> registProduct(@AuthUser UserInfo userInfo,
        @Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.created(productService.registProduct(userInfo.getId(), request).getProductId());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductDetail(@PathVariable("productId") Long productId) {
        return ApiResponse.ok(ProductDetailResponse.of(productService.getProductById(productId)));
    }

}
