package com.procurement.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateEnquiryRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Valid
    private List<EnquiryItemDto> items;
}

