package com.project.bee_rushtech.dtos;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private Long quantity;
}
