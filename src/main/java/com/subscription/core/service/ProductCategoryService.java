package com.subscription.core.service;

import com.subscription.core.dto.ProductCategoryUpsertDTO;
import com.subscription.core.entity.ProductCategory;
import com.subscription.core.repository.ProductCategoryRepository;
import com.subscription.core.util.LambdaUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public String upsertProductCategory(ProductCategoryUpsertDTO productCategoryRequest) {
        boolean isUpdate = Optional.ofNullable(productCategoryRequest.getProductId())
            .map(productCategoryRepository::findByProductId)
            .orElse(List.of())
            .stream()
            .anyMatch(pc -> pc.getCategory().equals(productCategoryRequest.getCategory()));
            
        ProductCategory productCategory = Optional.ofNullable(productCategoryRequest.getProductId())
            .map(productCategoryRepository::findByProductId)
            .orElse(List.of())
            .stream()
            .filter(pc -> pc.getCategory().equals(productCategoryRequest.getCategory()))
            .findFirst()
            .map(existingProductCategory -> {
                updateProductCategory(existingProductCategory, productCategoryRequest);
                return existingProductCategory;
            })
            .orElse(createProductCategory(productCategoryRequest));
        
        productCategoryRepository.save(productCategory);
        return isUpdate ? "ProductCategory updated successfully" : "ProductCategory created successfully";
    }

    private ProductCategory createProductCategory(ProductCategoryUpsertDTO dto) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(dto.getCategory());
        productCategory.setGstSlab(dto.getGstSlab());
        productCategory.setStatus(dto.getStatus());
        return productCategory;
    }

    private void updateProductCategory(ProductCategory productCategory, ProductCategoryUpsertDTO productCategoryRequest) {
        LambdaUtil.updateIfNotNull(productCategoryRequest.getCategory(), productCategory::setCategory);
        LambdaUtil.updateIfNotNull(productCategoryRequest.getGstSlab(), productCategory::setGstSlab);
        LambdaUtil.updateIfNotNull(productCategoryRequest.getStatus(), productCategory::setStatus);
    }
}
