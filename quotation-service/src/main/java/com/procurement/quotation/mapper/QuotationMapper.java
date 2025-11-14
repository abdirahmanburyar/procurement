package com.procurement.quotation.mapper;

import com.procurement.quotation.dto.QuotationDto;
import com.procurement.quotation.dto.QuotationItemDto;
import com.procurement.quotation.entity.Quotation;
import com.procurement.quotation.entity.QuotationItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class QuotationMapper {
    public QuotationDto toDto(Quotation quotation) {
        QuotationDto dto = new QuotationDto();
        dto.setId(quotation.getId());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setEnquiryId(quotation.getEnquiryId());
        dto.setSupplierId(quotation.getSupplierId());
        dto.setSupplierName(quotation.getSupplierName());
        dto.setStatus(quotation.getStatus());
        dto.setCreatedAt(quotation.getCreatedAt());
        dto.setUpdatedAt(quotation.getUpdatedAt());
        dto.setItems(quotation.getItems().stream()
                .map(this::itemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public QuotationItemDto itemToDto(QuotationItem item) {
        QuotationItemDto dto = new QuotationItemDto();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setUnit(item.getUnit());
        return dto;
    }
}

