package com.example.Backend.services;

import com.example.Backend.dtos.customer.CustomerCreateDTO;
import com.example.Backend.dtos.customer.CustomerResponseDTO;
import com.example.Backend.dtos.customer.CustomerSearchRequest;
import com.example.Backend.dtos.customer.CustomerUpdateDTO;
import com.example.Backend.exceptions.CustomerException;
import com.example.Backend.models.Customer;
import com.example.Backend.repositorys.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Tạo khách hàng mới
     */
    public CustomerResponseDTO createCustomer(CustomerCreateDTO createDTO) {
        log.info("Creating new customer with phone: {}", createDTO.getPhone());

        // Kiểm tra số điện thoại đã tồn tại
        if (customerRepository.existsByPhone(createDTO.getPhone())) {
            throw new CustomerException("Số điện thoại đã được sử dụng: " + createDTO.getPhone());
        }

        // Kiểm tra email đã tồn tại (nếu có)
        if (createDTO.getEmail() != null && !createDTO.getEmail().isEmpty()
            && customerRepository.existsByEmail(createDTO.getEmail())) {
            throw new CustomerException("Email đã được sử dụng: " + createDTO.getEmail());
        }

        Customer customer = Customer.builder()
                .fullName(createDTO.getFullName())
                .phone(createDTO.getPhone())
                .email(createDTO.getEmail())
                .gender(createDTO.getGender())
                .address(createDTO.getAddress())
                .notes(createDTO.getNote())
                .tier(Customer.CustomerTier.REGULAR)
                .isActive(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());

        return mapToResponseDTO(savedCustomer);
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    public CustomerResponseDTO updateCustomer(Long customerId, CustomerUpdateDTO updateDTO) {
        log.info("Updating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        // Kiểm tra số điện thoại (nếu thay đổi)
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(customer.getPhone())) {
            if (customerRepository.existsByPhoneAndIdNot(updateDTO.getPhone(), customerId)) {
                throw new CustomerException("Số điện thoại đã được sử dụng: " + updateDTO.getPhone());
            }
            customer.setPhone(updateDTO.getPhone());
        }

        // Kiểm tra email (nếu thay đổi)
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmailAndIdNot(updateDTO.getEmail(), customerId)) {
                throw new CustomerException("Email đã được sử dụng: " + updateDTO.getEmail());
            }
            customer.setEmail(updateDTO.getEmail());
        }

        // Cập nhật các trường khác
        if (updateDTO.getFullName() != null) {
            customer.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getGender() != null) {
            customer.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAddress() != null) {
            customer.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getNote() != null) {
            customer.setNotes(updateDTO.getNote());
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", savedCustomer.getId());

        return mapToResponseDTO(savedCustomer);
    }

    /**
     * Lấy thông tin khách hàng theo ID
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        return mapToResponseDTO(customer);
    }

    /**
     * Lấy thông tin khách hàng theo số điện thoại
     */
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhoneAndIsActiveTrue(phone)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với số điện thoại: " + phone));

        return mapToResponseDTO(customer);
    }

    /**
     * Tìm kiếm khách hàng với bộ lọc
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomers(CustomerSearchRequest searchRequest) {
        log.info("Searching customers with criteria: {}", searchRequest);

        // Tạo Pageable
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getSortDirection()),
                           searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);

        // Tìm kiếm với criteria
        Page<Customer> customers = customerRepository.searchCustomers(
                searchRequest.getFullName(),
                searchRequest.getPhone(),
                searchRequest.getEmail(),
                searchRequest.getGender(),
                null, // tier sẽ xử lý riêng nếu cần
                true, // chỉ lấy khách hàng active
                pageable
        );

        return customers.map(this::mapToResponseDTO);
    }

    /**
     * Lấy danh sách tất cả khách hàng active
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllActiveCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findByIsActiveTrue(pageable);
        return customers.map(this::mapToResponseDTO);
    }

    /**
     * Vô hiệu hóa khách hàng (soft delete)
     */
    public void deactivateCustomer(Long customerId) {
        log.info("Deactivating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setIsActive(false);
        customerRepository.save(customer);

        log.info("Customer deactivated successfully with ID: {}", customerId);
    }

    /**
     * Kích hoạt lại khách hàng
     */
    public void activateCustomer(Long customerId) {
        log.info("Activating customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setIsActive(true);
        customerRepository.save(customer);

        log.info("Customer activated successfully with ID: {}", customerId);
    }

    /**
     * Nâng cấp tier khách hàng
     */
    public CustomerResponseDTO upgradeTier(Long customerId, Customer.CustomerTier newTier) {
        log.info("Upgrading customer tier. ID: {}, New tier: {}", customerId, newTier);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setTier(newTier);
        Customer savedCustomer = customerRepository.save(customer);

        log.info("Customer tier upgraded successfully. ID: {}, Tier: {}", customerId, newTier);
        return mapToResponseDTO(savedCustomer);
    }

    /**
     * Lấy thống kê khách hàng
     */
    @Transactional(readOnly = true)
    public CustomerStatisticsDTO getCustomerStatistics() {
        long totalActive = customerRepository.countActiveCustomers();
        long regularTier = customerRepository.countByTier(Customer.CustomerTier.REGULAR);
        long vipTier = customerRepository.countByTier(Customer.CustomerTier.VIP);
        long potentialTier = customerRepository.countByTier(Customer.CustomerTier.POTENTIAL);
        long newThisMonth = customerRepository.countCustomersCreatedAfter(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        );

        return CustomerStatisticsDTO.builder()
                .totalActiveCustomers(totalActive)
                .regularTierCount(regularTier)
                .vipTierCount(vipTier)
                .potentialTierCount(potentialTier)
                .newCustomersThisMonth(newThisMonth)
                .build();
    }

    /**
     * Chuyển đổi Customer entity thành CustomerResponseDTO
     */
    private CustomerResponseDTO mapToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .gender(customer.getGender())
                .tier(customer.getTier())
                .fullAddress(customer.getAddress())
                .note(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                // TODO: Tích hợp với SalesOrder để lấy thống kê đơn hàng
                .totalOrders(0)
                .totalSpent(0.0)
                .lastOrderDate(null)
                .build();
    }

    public void hardDeleteCustomer(Long customerId) {
        log.info("Hard deleting customer with ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException("Không tìm thấy khách hàng với ID: " + customerId));

        customerRepository.delete(customer);
        log.info("Customer hard deleted successfully with ID: {}", customerId);
    }

    /**
     * DTO cho thống kê khách hàng
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CustomerStatisticsDTO {
        private Long totalActiveCustomers;
        private Long regularTierCount;
        private Long vipTierCount;
        private Long potentialTierCount;
        private Long newCustomersThisMonth;
    }
}
