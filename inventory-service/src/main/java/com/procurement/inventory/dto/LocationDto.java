package com.procurement.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LocationDto {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String country;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

