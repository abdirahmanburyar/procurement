package com.procurement.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryAdjustmentRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Quantity change is required")
    private BigDecimal quantityChange; // Positive for increase, negative for decrease

    private String reason;
    private String adjustedBy;
}

