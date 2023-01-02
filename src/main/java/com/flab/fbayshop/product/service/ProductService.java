package com.flab.fbayshop.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.fbayshop.common.dto.request.PageRequest;
import com.flab.fbayshop.product.dto.request.ProductCreateRequest;
import com.flab.fbayshop.product.dto.request.ProductOrderRequest;
import com.flab.fbayshop.product.exception.ProductNotFoundException;
import com.flab.fbayshop.product.exception.ProductNotSaleException;
import com.flab.fbayshop.product.exception.ProductOutOfStockException;
import com.flab.fbayshop.product.mapper.ProductMapper;
import com.flab.fbayshop.product.model.Product;
import com.flab.fbayshop.product.model.ProductStatus;
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
    public void decreaseStock(ProductOrderRequest request) {

        Product product = productMapper.findProductByIdForUpdate(request.getProductId())
            .orElseThrow(ProductNotFoundException::new);

        if (!ProductStatus.SALE.equals(product.getProductStatus())) {
            throw new ProductNotSaleException();
        }

        int quantity = request.getQuantity();

        if (quantity <= 0) {
            throw new ProductOutOfStockException();
        }

        int stock = product.getStock();

        if (stock < quantity) {
            throw new ProductOutOfStockException();
        }

        product.decreaseStock(quantity);

        if (product.getStock() <= 0) {
            product.updateStatus(ProductStatus.SOLD_OUT);
        }

        productMapper.updateProduct(product);
    }

}
