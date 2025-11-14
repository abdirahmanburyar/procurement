package com.procurement.po.controller;

import com.procurement.po.dto.CreatePurchaseOrderRequest;
import com.procurement.po.dto.PurchaseOrderDto;
import com.procurement.po.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pos")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderDto> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.createPurchaseOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDto>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDto> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrderDto> submitPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.submitPurchaseOrder(id));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderDto> approvePurchaseOrder(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String approvedBy) {
        return ResponseEntity.ok(purchaseOrderService.approvePurchaseOrder(id, approvedBy));
    }
}

