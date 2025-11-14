package com.procurement.inventory.controller;

import com.procurement.inventory.dto.InventoryAdjustmentRequest;
import com.procurement.inventory.dto.StockDto;
import com.procurement.inventory.dto.StockTransferRequest;
import com.procurement.inventory.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStock());
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<StockDto>> getStockByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(stockService.getStockByLocation(locationId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<StockDto> transferStock(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.transferStock(request));
    }

    @PostMapping("/adjustment")
    public ResponseEntity<StockDto> adjustInventory(@Valid @RequestBody InventoryAdjustmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.adjustInventory(request));
    }
}

