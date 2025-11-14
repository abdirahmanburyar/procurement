package com.procurement.quotation.service;

import com.procurement.quotation.dto.CreateQuotationRequest;
import com.procurement.quotation.dto.QuotationDto;
import com.procurement.quotation.dto.QuotationItemDto;
import com.procurement.quotation.entity.Quotation;
import com.procurement.quotation.entity.QuotationItem;
import com.procurement.quotation.mapper.QuotationMapper;
import com.procurement.quotation.repository.QuotationRepository;
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
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private QuotationMapper quotationMapper;

    public QuotationDto createQuotation(CreateQuotationRequest request) {
        Quotation quotation = new Quotation();
        quotation.setEnquiryId(request.getEnquiryId());
        quotation.setSupplierId(request.getSupplierId());
        quotation.setSupplierName(request.getSupplierName());
        quotation.setQuotationNumber(generateQuotationNumber());
        quotation.setStatus("DRAFT");

        if (request.getItems() != null) {
            for (QuotationItemDto itemDto : request.getItems()) {
                QuotationItem item = new QuotationItem();
                item.setQuotation(quotation);
                item.setProductName(itemDto.getProductName());
                item.setDescription(itemDto.getDescription());
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(itemDto.getUnitPrice());
                item.setUnit(itemDto.getUnit());
                
                // Calculate total price
                if (item.getQuantity() != null && item.getUnitPrice() != null) {
                    item.setTotalPrice(item.getQuantity().multiply(item.getUnitPrice()));
                }
                
                quotation.getItems().add(item);
            }
        }

        quotation = quotationRepository.save(quotation);
        return quotationMapper.toDto(quotation);
    }

    public List<QuotationDto> getQuotationsByEnquiryId(Long enquiryId) {
        return quotationRepository.findByEnquiryId(enquiryId).stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<QuotationDto> getAllQuotations() {
        return quotationRepository.findAll().stream()
                .map(quotationMapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateQuotationNumber() {
        String prefix = "QUO";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }
}

