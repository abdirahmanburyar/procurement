package com.procurement.po.mapper;

import com.procurement.po.dto.PurchaseOrderDto;
import com.procurement.po.dto.PurchaseOrderItemDto;
import com.procurement.po.entity.PurchaseOrder;
import com.procurement.po.entity.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderDto toDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setQuotationId(po.getQuotationId());
        dto.setSupplierId(po.getSupplierId());
        dto.setSupplierName(po.getSupplierName());
        dto.setStatus(po.getStatus());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setApprovedBy(po.getApprovedBy());
        dto.setApprovedAt(po.getApprovedAt());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        if (po.getItems() != null) {
            dto.setItems(po.getItems().stream()
                    .map(this::itemToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private PurchaseOrderItemDto itemToDto(PurchaseOrderItem item) {
        PurchaseOrderItemDto dto = new PurchaseOrderItemDto();
        dto.setProductName(item.getProductName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setUnit(item.getUnit());
        return dto;
    }
}

