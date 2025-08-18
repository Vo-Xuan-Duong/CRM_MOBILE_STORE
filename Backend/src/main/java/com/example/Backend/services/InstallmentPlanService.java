package com.example.Backend.services;

import com.example.Backend.controllers.InstallmentPlanController.InstallmentStatistics;
import com.example.Backend.dtos.installment.InstallmentPlanRequest;
import com.example.Backend.dtos.installment.InstallmentPlanResponse;
import com.example.Backend.exceptions.ResourceNotFoundException;
import com.example.Backend.models.InstallmentPlan;
import com.example.Backend.models.SalesOrder;
import com.example.Backend.repositorys.InstallmentPlanRepository;
import com.example.Backend.repositorys.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InstallmentPlanService {

    private final InstallmentPlanRepository installmentPlanRepository;
    private final SalesOrderRepository salesOrderRepository;

    public InstallmentPlanResponse createInstallmentPlan(InstallmentPlanRequest request) {
        log.info("Creating installment plan for order: {}", request.getOrderId());

        SalesOrder order = salesOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        // Check if installment plan already exists for this order
        if (installmentPlanRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException("Installment plan already exists for this order");
        }

        // Calculate total amount and remaining balance
        BigDecimal totalAmount = request.getPrincipal().add(request.getDownPayment());
        BigDecimal remainingBalance = request.getPrincipal();

        InstallmentPlan installmentPlan = InstallmentPlan.builder()
                .order(order)
                .provider(request.getProvider())
                .principal(request.getPrincipal())
                .downPayment(request.getDownPayment())
                .totalAmount(totalAmount)
                .months(request.getMonths())
                .interestRateApr(request.getInterestRateApr())
                .monthlyPayment(request.getMonthlyPayment())
                .remainingBalance(remainingBalance)
                .status(InstallmentPlan.InstallmentStatus.ACTIVE)
                .startDate(LocalDate.now())
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .build();

        InstallmentPlan savedPlan = installmentPlanRepository.save(installmentPlan);
        log.info("Installment plan created successfully with id: {}", savedPlan.getId());

        return mapToResponse(savedPlan);
    }

    @Transactional(readOnly = true)
    public InstallmentPlanResponse getInstallmentPlanById(Long id) {
        InstallmentPlan plan = installmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment plan not found with id: " + id));
        return mapToResponse(plan);
    }

    @Transactional(readOnly = true)
    public InstallmentPlanResponse getInstallmentPlanByOrderId(Long orderId) {
        InstallmentPlan plan = installmentPlanRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment plan not found for order id: " + orderId));
        return mapToResponse(plan);
    }

    @Transactional(readOnly = true)
    public Page<InstallmentPlanResponse> getAllInstallmentPlans(Pageable pageable) {
        Page<InstallmentPlan> plans = installmentPlanRepository.findAll(pageable);
        return plans.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<InstallmentPlanResponse> getInstallmentPlansByStatus(String status) {
        InstallmentPlan.InstallmentStatus installmentStatus = InstallmentPlan.InstallmentStatus.valueOf(status.toUpperCase());
        List<InstallmentPlan> plans = installmentPlanRepository.findByStatus(installmentStatus);
        return plans.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void completeInstallmentPlan(Long id) {
        log.info("Completing installment plan with id: {}", id);

        InstallmentPlan plan = installmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment plan not found with id: " + id));

        if (plan.getStatus() != InstallmentPlan.InstallmentStatus.ACTIVE) {
            throw new IllegalStateException("Only active installment plans can be completed");
        }

        plan.setStatus(InstallmentPlan.InstallmentStatus.COMPLETED);
        plan.setRemainingBalance(BigDecimal.ZERO);
        plan.setEndDate(LocalDate.now());

        installmentPlanRepository.save(plan);
        log.info("Installment plan completed successfully");
    }

    public void markAsDefaulted(Long id) {
        log.info("Marking installment plan as defaulted with id: {}", id);

        InstallmentPlan plan = installmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment plan not found with id: " + id));

        if (plan.getStatus() == InstallmentPlan.InstallmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot mark completed installment plan as defaulted");
        }

        plan.setStatus(InstallmentPlan.InstallmentStatus.DEFAULTED);
        plan.setEndDate(LocalDate.now());

        installmentPlanRepository.save(plan);
        log.info("Installment plan marked as defaulted successfully");
    }

    @Transactional(readOnly = true)
    public InstallmentStatistics getInstallmentStatistics() {
        long totalPlans = installmentPlanRepository.count();
        long activePlans = installmentPlanRepository.countByStatus(InstallmentPlan.InstallmentStatus.ACTIVE);
        long completedPlans = installmentPlanRepository.countByStatus(InstallmentPlan.InstallmentStatus.COMPLETED);
        long defaultedPlans = installmentPlanRepository.countByStatus(InstallmentPlan.InstallmentStatus.DEFAULTED);

        BigDecimal totalPrincipal = installmentPlanRepository.sumTotalPrincipal();
        BigDecimal totalOutstanding = installmentPlanRepository.sumRemainingBalance();

        return InstallmentStatistics.builder()
                .totalPlans(totalPlans)
                .activePlans(activePlans)
                .completedPlans(completedPlans)
                .defaultedPlans(defaultedPlans)
                .totalPrincipal(totalPrincipal != null ? totalPrincipal : BigDecimal.ZERO)
                .totalOutstanding(totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO)
                .build();
    }

    public void processPayment(Long id, BigDecimal paymentAmount) {
        log.info("Processing payment of {} for installment plan {}", paymentAmount, id);

        InstallmentPlan plan = installmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment plan not found with id: " + id));

        if (plan.getStatus() != InstallmentPlan.InstallmentStatus.ACTIVE) {
            throw new IllegalStateException("Can only process payments for active installment plans");
        }

        BigDecimal newBalance = plan.getRemainingBalance().subtract(paymentAmount);
        plan.setRemainingBalance(newBalance.max(BigDecimal.ZERO));

        // Update next payment date
        plan.setNextPaymentDate(plan.getNextPaymentDate().plusMonths(1));

        // Check if fully paid
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            plan.setStatus(InstallmentPlan.InstallmentStatus.COMPLETED);
            plan.setEndDate(LocalDate.now());
        }

        installmentPlanRepository.save(plan);
        log.info("Payment processed successfully. Remaining balance: {}", plan.getRemainingBalance());
    }

    private InstallmentPlanResponse mapToResponse(InstallmentPlan plan) {
        return InstallmentPlanResponse.builder()
                .id(plan.getId())
                .orderId(plan.getOrder().getId())
                .orderNumber(plan.getOrder().getOrderNumber())
                .customerName(plan.getOrder().getCustomer().getFullName())
                .provider(plan.getProvider())
                .principal(plan.getPrincipal())
                .downPayment(plan.getDownPayment())
                .totalAmount(plan.getTotalAmount())
                .months(plan.getMonths())
                .interestRateApr(plan.getInterestRateApr())
                .monthlyPayment(plan.getMonthlyPayment())
                .remainingBalance(plan.getRemainingBalance())
                .status(plan.getStatus())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .nextPaymentDate(plan.getNextPaymentDate())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
