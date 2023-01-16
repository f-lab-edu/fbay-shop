package com.flab.fbayshop.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.fbayshop.common.dto.request.PageRequest;
import com.flab.fbayshop.product.dto.request.ProductCreateRequest;
import com.flab.fbayshop.product.dto.request.ProductOrderRequest;
import com.flab.fbayshop.product.exception.ProductDuplicateRequestException;
import com.flab.fbayshop.product.exception.ProductNotFoundException;
import com.flab.fbayshop.product.exception.ProductNotSaleException;
import com.flab.fbayshop.product.exception.ProductOutOfStockException;
import com.flab.fbayshop.product.mapper.ProductMapper;
import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.model.ProductStatus;
import com.flab.fbayshop.user.exception.UserProcessException;
import com.flab.fbayshop.user.model.User;
import com.flab.fbayshop.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final UserService userService;

    @Transactional
    public Product registProduct(Long userId, ProductCreateRequest request) {

        User seller = userService.getUserById(userId);

        Product product = request.toEntity(seller);

        productMapper.insertProduct(product);

        if (product.getProductTypeList().size() > 0) {
            productMapper.insertProductType(product);
        }

        return product;
    }

    @Transactional
    public Product getProductById(Long productId) {
        return productMapper.findProductById(productId).orElseThrow(ProductNotFoundException::new);
    }

    @Transactional
    public List<Product> selectProductList(PageRequest request) {
        return productMapper.selectProductList(request);
    }

    @Transactional
    public List<Product> decreaseStock(List<ProductOrderRequest> requests) {

        List<Long> productIdList = requests.stream()
            .map(ProductOrderRequest::getProductId)
            .distinct()
            .collect(Collectors.toList());

        // 중복된 상품이 요청된 경우
        if (requests.size() != productIdList.size()) {
            throw new ProductDuplicateRequestException();
        }

        List<Product> productList = productMapper.findProductsByIdsForUpdate(productIdList);

        // 주문요청 상품리스트 개수와 조회된 상품리스트 개수가 다른 경우
        if (productList == null || productList.size() != requests.size()) {
            throw new ProductNotFoundException();
        }

        List<Product> afterProductList = productList.stream().peek(product -> {
            ProductOrderRequest request = requests.stream()
                .filter(req -> req.getProductId().equals(product.getProductId()))
                .findAny().orElseThrow(ProductNotFoundException::new);

            // 판매 중인 상품이 아닌 경우
            if (!ProductStatus.SALE.equals(product.getProductStatus())) {
                throw new ProductNotSaleException();
            }

            int quantity = request.getQuantity(); // 주문 수량
            int stock = product.getStock(); // 상품 재고

            // 재고가 부족한 경우
            if (quantity <= 0 || stock < quantity) {
                throw new ProductOutOfStockException();
            }

            product.decreaseStock(quantity);

            // 재고가 떨어진 경우
            if (product.getStock() <= 0) {
                product.updateStatus(ProductStatus.SOLD_OUT);
            }

        }).collect(Collectors.toList());

        int res = productMapper.updateProducts(afterProductList);

        if (res != 1) {
            throw new UserProcessException();
        }

        return productList;
    }

}
