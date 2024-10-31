package com.project.bee_rushtech.repositories;

import com.project.bee_rushtech.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
