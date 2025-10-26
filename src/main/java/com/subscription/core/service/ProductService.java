package com.subscription.core.service;

import com.subscription.core.dto.ProductUpsertDTO;
import com.subscription.core.entity.Product;
import com.subscription.core.repository.ProductRepository;
import com.subscription.core.util.LambdaUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String upsertProduct(ProductUpsertDTO productRequest) {
        boolean isUpdate = Optional.ofNullable(productRequest.getSku())
            .map(productRepository::findBySku)
            .orElse(Optional.empty())
            .isPresent();
            
        Product product = Optional.ofNullable(productRequest.getSku())
            .map(productRepository::findBySku)
            .orElse(Optional.empty())
            .map(existingProduct -> {
                updateProduct(existingProduct, productRequest);
                return existingProduct;
            })
            .orElse(createProduct(productRequest));
        
        productRepository.save(product);
        return isUpdate ? "Product updated successfully" : "Product created successfully";
    }

    private Product createProduct(ProductUpsertDTO dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setBasePrice(dto.getBasePrice());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setSku(dto.getSku());
        product.setStatus(dto.getStatus());
        return product;
    }

    private void updateProduct(Product product, ProductUpsertDTO productRequest) {
        LambdaUtil.updateIfNotNull(productRequest.getProductName(), product::setProductName);
        LambdaUtil.updateIfNotNull(productRequest.getBasePrice(), product::setBasePrice);
        LambdaUtil.updateIfNotNull(productRequest.getDescription(), product::setDescription);
        LambdaUtil.updateIfNotNull(productRequest.getImageUrl(), product::setImageUrl);
        LambdaUtil.updateIfNotNull(productRequest.getSku(), product::setSku);
        LambdaUtil.updateIfNotNull(productRequest.getStatus(), product::setStatus);
    }
}
