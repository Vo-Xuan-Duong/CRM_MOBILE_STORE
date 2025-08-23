package com.example.Backend.services;

import com.example.Backend.dtos.warranty.WarrantyRequest;
import com.example.Backend.dtos.warranty.WarrantyResponse;
import com.example.Backend.exceptions.WarrantyException;
import com.example.Backend.mappers.WarrantyMapper;
import com.example.Backend.models.*;
import com.example.Backend.repositorys.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final CustomerRepository customerRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final SerialUnitRepository serialUnitRepository;
    private final WarrantyMapper warrantyMapper;

    public WarrantyResponse createWarranty(WarrantyRequest request) {
        Customer customer = findCustomerById(request.getCustomerId());
        SalesOrderItem orderItem = findSalesOrderItemById(request.getOrderItemId());
        SerialUnit serialUnit = request.getSerialUnitId() != null ?
            findSerialUnitById(request.getSerialUnitId()) : null;

        String warrantyCode = generateWarrantyCode();
        LocalDate endDate = request.getStartDate().plusMonths(request.getMonths());

        Warranty warranty = Warranty.builder()
                .customer(customer)
                .orderItem(orderItem)
                .serialUnit(serialUnit)
                .warrantyCode(warrantyCode)
                .startDate(request.getStartDate())
                .endDate(endDate)
                .months(request.getMonths())
                .status(Warranty.WarrantyStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        Warranty savedWarranty = warrantyRepository.save(warranty);
        return warrantyMapper.toResponse(savedWarranty);
    }

    public WarrantyResponse getWarrantyById(Long id) {
        Warranty warranty = findWarrantyById(id);
        return warrantyMapper.toResponse(warranty);
    }

    public WarrantyResponse getWarrantyByCode(String warrantyCode) {
        Warranty warranty = warrantyRepository.findByWarrantyCode(warrantyCode)
                .orElseThrow(() -> new WarrantyException("Warranty not found with code: " + warrantyCode));
        return warrantyMapper.toResponse(warranty);
    }

    public Page<WarrantyResponse> getAllWarranties(Pageable pageable) {
        return warrantyRepository.findAll(pageable)
                .map(warrantyMapper::toResponse);
    }

    public List<WarrantyResponse> getWarrantiesByCustomer(Long customerId) {
        return warrantyRepository.findByCustomerId(customerId).stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WarrantyResponse> getWarrantiesByStatus(String status) {
        Warranty.WarrantyStatus warrantyStatus = Warranty.WarrantyStatus.valueOf(status.toUpperCase());
        return warrantyRepository.findByStatus(warrantyStatus).stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WarrantyResponse> getWarrantiesExpiringWithinDays(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return warrantyRepository.findExpiringWarranties(cutoffDate).stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WarrantyResponse> getExpiredWarranties() {
        LocalDate today = LocalDate.now();
        return warrantyRepository.findExpiredWarranties(today).stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<WarrantyResponse> getActiveWarranties() {
        return warrantyRepository.findActiveWarranties().stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void voidWarranty(Long id, String reason) {
        Warranty warranty = findWarrantyById(id);
        warranty.setStatus(Warranty.WarrantyStatus.VOID);
        warranty.setNotes((warranty.getNotes() != null ? warranty.getNotes() + "\n" : "") +
                "Voided: " + reason);
        warrantyRepository.save(warranty);
    }

    public void claimWarranty(Long id, String claimReason) {
        Warranty warranty = findWarrantyById(id);
        if (!warranty.canClaim()) {
            throw new WarrantyException("Warranty cannot be claimed");
        }
        warranty.setStatus(Warranty.WarrantyStatus.CLAIMED);
        warranty.setNotes((warranty.getNotes() != null ? warranty.getNotes() + "\n" : "") +
                "Claimed: " + claimReason);
        warrantyRepository.save(warranty);
    }

    public Page<WarrantyResponse> searchWarranties(String keyword, Pageable pageable) {
        return warrantyRepository.searchWarranties(keyword, pageable)
                .map(warrantyMapper::toResponse);
    }

    public List<WarrantyResponse> getWarrantiesByDateRange(LocalDate startDate, LocalDate endDate) {
        return warrantyRepository.findByDateRange(startDate, endDate).stream()
                .map(warrantyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public WarrantyStatistics getWarrantyStatistics() {
        return WarrantyStatistics.builder()
                .totalWarranties(warrantyRepository.count())
                .activeWarranties(warrantyRepository.countByStatus(Warranty.WarrantyStatus.ACTIVE))
                .expiredWarranties(warrantyRepository.countExpiredWarranties(LocalDate.now()))
                .voidWarranties(warrantyRepository.countByStatus(Warranty.WarrantyStatus.VOID))
                .claimedWarranties(warrantyRepository.countByStatus(Warranty.WarrantyStatus.CLAIMED))
                .expiringWithin30Days(warrantyRepository.countExpiringWithinDays(LocalDate.now().plusDays(30)))
                .build();
    }

    // Inner class for warranty statistics
    @Data
    @Builder
    public static class WarrantyStatistics {
        private Long totalWarranties;
        private Long activeWarranties;
        private Long expiredWarranties;
        private Long voidWarranties;
        private Long claimedWarranties;
        private Long expiringWithin30Days;
    }

    private String generateWarrantyCode() {
        String prefix = "WR";
        String date = LocalDate.now().toString().replace("-", "");
        long count = warrantyRepository.count() + 1;
        return String.format("%s%s%06d", prefix, date, count);
    }

    private Warranty findWarrantyById(Long id) {
        return warrantyRepository.findById(id)
                .orElseThrow(() -> new WarrantyException("Warranty not found with id: " + id));
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new WarrantyException("Customer not found with id: " + id));
    }

    private SalesOrderItem findSalesOrderItemById(Long id) {
        return salesOrderItemRepository.findById(id)
                .orElseThrow(() -> new WarrantyException("Sales order item not found with id: " + id));
    }

    private SerialUnit findSerialUnitById(Long id) {
        return serialUnitRepository.findById(id)
                .orElseThrow(() -> new WarrantyException("Serial unit not found with id: " + id));
    }
}
