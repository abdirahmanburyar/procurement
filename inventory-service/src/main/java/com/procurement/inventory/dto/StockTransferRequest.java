package com.procurement.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockTransferRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "From location ID is required")
    private Long fromLocationId;

    @NotNull(message = "To location ID is required")
    private Long toLocationId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    private String reason;
    private String performedBy;
}

