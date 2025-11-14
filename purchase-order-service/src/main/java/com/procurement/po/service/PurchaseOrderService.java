package com.procurement.po.service;

import com.procurement.po.dto.CreatePurchaseOrderRequest;
import com.procurement.po.dto.PurchaseOrderDto;
import com.procurement.po.dto.PurchaseOrderItemDto;
import com.procurement.po.entity.PurchaseOrder;
import com.procurement.po.entity.PurchaseOrderItem;
import com.procurement.po.mapper.PurchaseOrderMapper;
import com.procurement.po.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderDto createPurchaseOrder(CreatePurchaseOrderRequest request) {
        PurchaseOrder po = new PurchaseOrder();
        po.setQuotationId(request.getQuotationId());
        po.setSupplierId(request.getSupplierId());
        po.setSupplierName(request.getSupplierName());
        po.setPoNumber(generatePONumber());
        po.setStatus("DRAFT");
        po.setTotalAmount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        if (request.getItems() != null) {
            for (PurchaseOrderItemDto itemDto : request.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(po);
                item.setProductName(itemDto.getProductName());
                item.setDescription(itemDto.getDescription());
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setUnit(itemDto.getUnit());

                // Calculate total price
                if (item.getQuantity() != null && item.getUnitPrice() != null) {
                    item.setTotalPrice(item.getQuantity().multiply(item.getUnitPrice()));
                    total = total.add(item.getTotalPrice());
                }

                po.getItems().add(item);
            }
        }
        po.setTotalAmount(total);

        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDto(po);
    }

    public PurchaseOrderDto submitPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));

        if (!"DRAFT".equals(po.getStatus())) {
            throw new RuntimeException("Only DRAFT purchase orders can be submitted");
        }

        po.setStatus("SUBMITTED");
        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDto(po);
    }

    public PurchaseOrderDto approvePurchaseOrder(Long id, String approvedBy) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));

        if (!"SUBMITTED".equals(po.getStatus())) {
            throw new RuntimeException("Only SUBMITTED purchase orders can be approved");
        }

        po.setStatus("APPROVED");
        po.setApprovedBy(approvedBy);
        po.setApprovedAt(LocalDateTime.now());
        po = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toDto(po);
    }

    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toDto)
                .collect(Collectors.toList());
    }

    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));
        return purchaseOrderMapper.toDto(po);
    }

    private String generatePONumber() {
        String prefix = "PO";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }
}

