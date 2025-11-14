package com.procurement.procurement.controller;

import com.procurement.procurement.dto.CreateEnquiryRequest;
import com.procurement.procurement.dto.EnquiryDto;
import com.procurement.procurement.service.EnquiryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<EnquiryDto> createEnquiry(@Valid @RequestBody CreateEnquiryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enquiryService.createEnquiry(request));
    }

    @GetMapping
    public ResponseEntity<List<EnquiryDto>> getAllEnquiries() {
        return ResponseEntity.ok(enquiryService.getAllEnquiries());
    }
}

