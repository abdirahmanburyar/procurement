package com.procurement.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDto {
    private Long id;
    private String quotationNumber;
    private Long enquiryId;
    private Long supplierId;
    private String supplierName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuotationItemDto> items;
}

