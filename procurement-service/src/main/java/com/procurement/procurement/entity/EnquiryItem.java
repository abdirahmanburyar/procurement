package com.procurement.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "enquiry_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquiry_id", nullable = false)
    private Enquiry enquiry;

    private String productName;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private String specifications;
}

