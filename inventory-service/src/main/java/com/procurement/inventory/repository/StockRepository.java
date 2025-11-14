package com.procurement.inventory.repository;

import com.procurement.inventory.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);
    
    List<Stock> findByLocationId(Long locationId);
    
    List<Stock> findByProductId(Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.location.id = :locationId")
    List<Stock> findAllByLocation(@Param("locationId") Long locationId);
}

