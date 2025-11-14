package com.procurement.inventory.service;

import com.procurement.inventory.dto.InventoryAdjustmentRequest;
import com.procurement.inventory.dto.StockDto;
import com.procurement.inventory.dto.StockTransferRequest;
import com.procurement.inventory.entity.*;
import com.procurement.inventory.mapper.StockMapper;
import com.procurement.inventory.repository.*;
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
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private InventoryAdjustmentRepository inventoryAdjustmentRepository;

    @Autowired
    private StockMapper stockMapper;

    public List<StockDto> getAllStock() {
        return stockRepository.findAll().stream()
                .map(stockMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StockDto> getStockByLocation(Long locationId) {
        return stockRepository.findByLocationId(locationId).stream()
                .map(stockMapper::toDto)
                .collect(Collectors.toList());
    }

    public StockDto transferStock(StockTransferRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Location fromLocation = locationRepository.findById(request.getFromLocationId())
                .orElseThrow(() -> new RuntimeException("From location not found with id: " + request.getFromLocationId()));

        Location toLocation = locationRepository.findById(request.getToLocationId())
                .orElseThrow(() -> new RuntimeException("To location not found with id: " + request.getToLocationId()));

        // Get or create stock at from location
        Stock fromStock = stockRepository.findByProductIdAndLocationId(request.getProductId(), request.getFromLocationId())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setLocation(fromLocation);
                    newStock.setQuantity(BigDecimal.ZERO);
                    newStock.setReservedQuantity(BigDecimal.ZERO);
                    newStock.setAvailableQuantity(BigDecimal.ZERO);
                    return stockRepository.save(newStock);
                });

        // Check if enough stock available
        if (fromStock.getAvailableQuantity().compareTo(request.getQuantity()) < 0) {
            throw new RuntimeException("Insufficient stock available. Available: " + fromStock.getAvailableQuantity());
        }

        // Deduct from source location
        fromStock.setQuantity(fromStock.getQuantity().subtract(request.getQuantity()));
        fromStock.setAvailableQuantity(fromStock.getQuantity().subtract(fromStock.getReservedQuantity()));
        stockRepository.save(fromStock);

        // Get or create stock at destination location
        Stock toStock = stockRepository.findByProductIdAndLocationId(request.getProductId(), request.getToLocationId())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setLocation(toLocation);
                    newStock.setQuantity(BigDecimal.ZERO);
                    newStock.setReservedQuantity(BigDecimal.ZERO);
                    newStock.setAvailableQuantity(BigDecimal.ZERO);
                    return stockRepository.save(newStock);
                });

        // Add to destination location
        toStock.setQuantity(toStock.getQuantity().add(request.getQuantity()));
        toStock.setAvailableQuantity(toStock.getQuantity().subtract(toStock.getReservedQuantity()));
        toStock = stockRepository.save(toStock);

        // Create stock movement record
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        movement.setMovementType("TRANSFER");
        movement.setQuantity(request.getQuantity());
        movement.setReason(request.getReason());
        movement.setPerformedBy(request.getPerformedBy());
        stockMovementRepository.save(movement);

        return stockMapper.toDto(toStock);
    }

    public StockDto adjustInventory(InventoryAdjustmentRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + request.getLocationId()));

        // Get or create stock
        Stock stock = stockRepository.findByProductIdAndLocationId(request.getProductId(), request.getLocationId())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setLocation(location);
                    newStock.setQuantity(BigDecimal.ZERO);
                    newStock.setReservedQuantity(BigDecimal.ZERO);
                    newStock.setAvailableQuantity(BigDecimal.ZERO);
                    return stockRepository.save(newStock);
                });

        BigDecimal previousQuantity = stock.getQuantity();
        BigDecimal newQuantity = previousQuantity.add(request.getQuantityChange());

        // Validate negative stock
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Adjustment would result in negative stock");
        }

        stock.setQuantity(newQuantity);
        stock.setAvailableQuantity(newQuantity.subtract(stock.getReservedQuantity()));
        stock = stockRepository.save(stock);

        // Create adjustment record
        InventoryAdjustment adjustment = new InventoryAdjustment();
        adjustment.setAdjustmentNumber(generateAdjustmentNumber());
        adjustment.setProduct(product);
        adjustment.setLocation(location);
        adjustment.setQuantityChange(request.getQuantityChange());
        adjustment.setPreviousQuantity(previousQuantity);
        adjustment.setNewQuantity(newQuantity);
        adjustment.setReason(request.getReason());
        adjustment.setAdjustedBy(request.getAdjustedBy());
        adjustment.setStatus("APPROVED");
        inventoryAdjustmentRepository.save(adjustment);

        // Create stock movement record
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        if (request.getQuantityChange().compareTo(BigDecimal.ZERO) > 0) {
            movement.setToLocation(location);
        } else {
            movement.setFromLocation(location);
        }
        movement.setMovementType(request.getQuantityChange().compareTo(BigDecimal.ZERO) > 0 ? "IN" : "OUT");
        movement.setQuantity(request.getQuantityChange().abs());
        movement.setReferenceNumber(adjustment.getAdjustmentNumber());
        movement.setReason(request.getReason());
        movement.setPerformedBy(request.getAdjustedBy());
        stockMovementRepository.save(movement);

        return stockMapper.toDto(stock);
    }

    private String generateAdjustmentNumber() {
        String prefix = "ADJ";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }
}

