package com.project.bee_rushtech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
