package com.procurement.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

