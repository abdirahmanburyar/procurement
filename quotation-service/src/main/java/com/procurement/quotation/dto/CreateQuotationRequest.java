package com.procurement.quotation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuotationRequest {
    @NotNull(message = "Enquiry ID is required")
    private Long enquiryId;

    private Long supplierId;
    private String supplierName;

    @Valid
    private List<QuotationItemDto> items;
}

