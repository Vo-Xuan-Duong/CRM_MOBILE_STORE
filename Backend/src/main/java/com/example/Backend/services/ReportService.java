package com.example.Backend.services;

import com.example.Backend.dtos.report.*;
import com.example.Backend.models.Customer;
import com.example.Backend.models.Order;
import com.example.Backend.repositorys.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardStatsDto getDashboardStats() {
        Long totalCustomers = customerRepository.count();
        Long totalOrders = orderRepository.count();
        Long totalProducts = productRepository.count();
        
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        BigDecimal monthlyRevenue = orderRepository.getRevenueByPeriod(startOfMonth, LocalDateTime.now());
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;
        
        Long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING.name());
        Long completedOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED.name());
        Long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED.name());

        Long activeCustomers = customerRepository.countByStatus(Customer.CustomerStatus.ACTIVE.name());
        Long lowStockProducts = productRepository.countLowStockProducts();
        
        Double averageOrderValue = orderRepository.getAverageOrderValue();
        if (averageOrderValue == null) averageOrderValue = 0.0;
        
        // Calculate growth rates
        LocalDateTime lastMonth = startOfMonth.minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.withDayOfMonth(1);
        
        Long customersLastMonth = customerRepository.countCustomersByPeriod(startOfLastMonth, startOfMonth);
        BigDecimal revenueLastMonth = orderRepository.getRevenueByPeriod(startOfLastMonth, startOfMonth);
        if (revenueLastMonth == null) revenueLastMonth = BigDecimal.ZERO;
        
        Long customersThisMonth = customerRepository.countCustomersByPeriod(startOfMonth, LocalDateTime.now());
        
        Integer customerGrowthRate = calculateGrowthRate(customersLastMonth, customersThisMonth);
        Integer revenueGrowthRate = calculateGrowthRate(revenueLastMonth, monthlyRevenue);
        
        return DashboardStatsDto.builder()
                .totalCustomers(totalCustomers)
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .activeCustomers(activeCustomers)
                .lowStockProducts(lowStockProducts)
                .averageOrderValue(averageOrderValue)
                .customerGrowthRate(customerGrowthRate)
                .revenueGrowthRate(revenueGrowthRate)
                .build();
    }

    public List<SalesReportDto> getSalesReport(LocalDate startDate, LocalDate endDate, String period) {
        return orderRepository.getSalesReportByPeriod(startDate, endDate, period);
    }

    public List<CustomerReportDto> getCustomerReport(int page, int size, String sortBy, String sortDirection) {
        return customerRepository.getCustomerReport(page, size, sortBy, sortDirection);
    }

    public List<ProductReportDto> getProductReport(String category, String sortBy, String sortDirection) {
        return productRepository.getProductReport(category, sortBy, sortDirection);
    }

    public List<RevenueReportDto> getRevenueReport(LocalDate startDate, LocalDate endDate, String period) {
        return orderRepository.getRevenueReportByPeriod(startDate, endDate, period);
    }

    public List<TopProductDto> getTopProducts(int limit, LocalDate startDate, LocalDate endDate) {
        return productRepository.getTopProductsByRevenue(limit, startDate, endDate);
    }

    private Integer calculateGrowthRate(Long previous, Long current) {
        if (previous == null || previous == 0) return current != null && current > 0 ? 100 : 0;
        if (current == null) current = 0L;
        return (int) Math.round(((double) (current - previous) / previous) * 100);
    }

    private Integer calculateGrowthRate(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0;
        }
        if (current == null) current = BigDecimal.ZERO;
        BigDecimal growth = current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return growth.intValue();
    }
}
