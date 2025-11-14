package com.procurement.procurement.service;

import com.procurement.procurement.dto.CreateEnquiryRequest;
import com.procurement.procurement.dto.EnquiryDto;
import com.procurement.procurement.dto.EnquiryItemDto;
import com.procurement.procurement.entity.Enquiry;
import com.procurement.procurement.entity.EnquiryItem;
import com.procurement.procurement.mapper.EnquiryMapper;
import com.procurement.procurement.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnquiryService {

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    private EnquiryMapper enquiryMapper;

    public EnquiryDto createEnquiry(CreateEnquiryRequest request) {
        Enquiry enquiry = new Enquiry();
        enquiry.setTitle(request.getTitle());
        enquiry.setDescription(request.getDescription());
        enquiry.setEnquiryNumber(generateEnquiryNumber());
        enquiry.setStatus("DRAFT");

        if (request.getItems() != null) {
            for (EnquiryItemDto itemDto : request.getItems()) {
                EnquiryItem item = new EnquiryItem();
                item.setEnquiry(enquiry);
                item.setProductName(itemDto.getProductName());
                item.setDescription(itemDto.getDescription());
                item.setQuantity(itemDto.getQuantity());
                item.setUnit(itemDto.getUnit());
                item.setSpecifications(itemDto.getSpecifications());
                enquiry.getItems().add(item);
            }
        }

        enquiry = enquiryRepository.save(enquiry);
        return enquiryMapper.toDto(enquiry);
    }

    public List<EnquiryDto> getAllEnquiries() {
        return enquiryRepository.findAll().stream()
                .map(enquiryMapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateEnquiryNumber() {
        String prefix = "ENQ";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }
}

