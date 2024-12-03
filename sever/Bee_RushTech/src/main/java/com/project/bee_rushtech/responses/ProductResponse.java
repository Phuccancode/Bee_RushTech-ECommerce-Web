package com.project.bee_rushtech.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends BaseResponse{
    private Long id;
    private String name;
    private Float price;
    @JsonProperty("import_price")
    private Float importPrice;
    private String thumbnail;
    private String description;
    private String brand;
    @JsonProperty("category_id")
    private Long categoryId;
    private Boolean available;
    private String color;
    private Long quantity;
    @JsonProperty("rented_quantity")
    private Long rentedQuantity;
}
