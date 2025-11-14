package com.procurement.procurement.mapper;

import com.procurement.procurement.dto.EnquiryDto;
import com.procurement.procurement.dto.EnquiryItemDto;
import com.procurement.procurement.entity.Enquiry;
import com.procurement.procurement.entity.EnquiryItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EnquiryMapper {
    public EnquiryDto toDto(Enquiry enquiry) {
        EnquiryDto dto = new EnquiryDto();
        dto.setId(enquiry.getId());
        dto.setEnquiryNumber(enquiry.getEnquiryNumber());
        dto.setTitle(enquiry.getTitle());
        dto.setDescription(enquiry.getDescription());
        dto.setStatus(enquiry.getStatus());
        dto.setCreatedAt(enquiry.getCreatedAt());
        dto.setUpdatedAt(enquiry.getUpdatedAt());
        dto.setItems(enquiry.getItems().stream()
                .map(this::itemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public EnquiryItemDto itemToDto(EnquiryItem item) {
        EnquiryItemDto dto = new EnquiryItemDto();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setSpecifications(item.getSpecifications());
        return dto;
    }
}

