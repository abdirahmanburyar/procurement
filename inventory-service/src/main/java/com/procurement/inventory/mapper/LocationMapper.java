package com.procurement.inventory.mapper;

import com.procurement.inventory.dto.LocationDto;
import com.procurement.inventory.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    public LocationDto toDto(Location location) {
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        dto.setCode(location.getCode());
        dto.setName(location.getName());
        dto.setAddress(location.getAddress());
        dto.setCity(location.getCity());
        dto.setCountry(location.getCountry());
        dto.setType(location.getType());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
        return dto;
    }
}

