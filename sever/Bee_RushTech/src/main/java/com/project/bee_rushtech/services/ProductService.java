package com.project.bee_rushtech.services;

import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.repositories.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product handleCreateProduct(Product product) {
        return this.productRepository.save(product);

    }
}
