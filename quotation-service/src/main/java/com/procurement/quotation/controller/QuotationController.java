package com.procurement.quotation.controller;

import com.procurement.quotation.dto.CreateQuotationRequest;
import com.procurement.quotation.dto.QuotationDto;
import com.procurement.quotation.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotations")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationDto> createQuotation(@Valid @RequestBody CreateQuotationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quotationService.createQuotation(request));
    }

    @GetMapping
    public ResponseEntity<List<QuotationDto>> getQuotations(
            @RequestParam(required = false) Long enquiryId) {
        if (enquiryId != null) {
            return ResponseEntity.ok(quotationService.getQuotationsByEnquiryId(enquiryId));
        }
        return ResponseEntity.ok(quotationService.getAllQuotations());
    }
}

