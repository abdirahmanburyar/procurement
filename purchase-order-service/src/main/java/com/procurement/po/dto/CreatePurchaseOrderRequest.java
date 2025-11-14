package com.procurement.po.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePurchaseOrderRequest {
    @NotNull(message = "Quotation ID is required")
    private Long quotationId;

    private Long supplierId;
    private String supplierName;

    @Valid
    private List<PurchaseOrderItemDto> items;
}

