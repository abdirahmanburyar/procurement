package com.procurement.inventory.mapper;

import com.procurement.inventory.dto.StockDto;
import com.procurement.inventory.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {
    public StockDto toDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        if (stock.getProduct() != null) {
            dto.setProductId(stock.getProduct().getId());
            dto.setProductName(stock.getProduct().getName());
            dto.setProductSku(stock.getProduct().getSku());
        }
        if (stock.getLocation() != null) {
            dto.setLocationId(stock.getLocation().getId());
            dto.setLocationName(stock.getLocation().getName());
            dto.setLocationCode(stock.getLocation().getCode());
        }
        dto.setQuantity(stock.getQuantity());
        dto.setReservedQuantity(stock.getReservedQuantity());
        dto.setAvailableQuantity(stock.getAvailableQuantity());
        dto.setCreatedAt(stock.getCreatedAt());
        dto.setUpdatedAt(stock.getUpdatedAt());
        return dto;
    }
}

