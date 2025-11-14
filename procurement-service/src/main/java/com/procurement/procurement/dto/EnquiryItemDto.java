package com.procurement.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryItemDto {
    private Long id;
    private String productName;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private String specifications;
}

