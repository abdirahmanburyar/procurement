package com.procurement.inventory.repository;

import com.procurement.inventory.entity.InventoryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryAdjustmentRepository extends JpaRepository<InventoryAdjustment, Long> {
    Optional<InventoryAdjustment> findByAdjustmentNumber(String adjustmentNumber);
}

