package com.example.Backend.services;

import com.example.Backend.dtos.customer.CustomerRequest;
import com.example.Backend.dtos.customer.CustomerResponse;
import com.example.Backend.exceptions.CustomerException;
import com.example.Backend.mappers.CustomerMapper;
import com.example.Backend.models.Customer;
import com.example.Backend.models.Customer.CustomerTier;
import com.example.Backend.repositorys.CustomerRepository;
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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerRequest request) {
        validateCustomerRequest(request);

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .dob(request.getDob())
                .address(request.getAddress())
                .tier(request.getTier() != null ? request.getTier() : CustomerTier.REGULAR)
                .notes(request.getNotes())
                .isActive(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = findCustomerById(id);

        // Validate unique constraints
        if (!customer.getPhone().equals(request.getPhone()) &&
            customerRepository.existsByPhone(request.getPhone())) {
            throw new CustomerException("Phone number already exists: " + request.getPhone());
        }

        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail()) &&
            customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerException("Email already exists: " + request.getEmail());
        }

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setDob(request.getDob());
        customer.setAddress(request.getAddress());
        customer.setTier(request.getTier());
        customer.setNotes(request.getNotes());

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = findCustomerById(id);
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new CustomerException("Customer not found with phone: " + phone));
        return customerMapper.toResponse(customer);
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findByIsActiveTrue(pageable)
                .map(customerMapper::toResponse);
    }

    public Page<CustomerResponse> searchCustomers(String search, Pageable pageable) {
        return customerRepository.findActiveCustomersWithSearch(search, pageable)
                .map(customerMapper::toResponse);
    }

    public List<CustomerResponse> getCustomersByTier(CustomerTier tier) {
        return customerRepository.findByTierAndIsActiveTrue(tier).stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomersWithBirthdayToday() {
        LocalDate today = LocalDate.now();
        return customerRepository.findCustomersWithBirthdayToday(today.getMonthValue(), today.getDayOfMonth())
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomersWithBirthdayInRange(LocalDate startDate, LocalDate endDate) {
        return customerRepository.findCustomersByBirthdayRange(startDate, endDate).stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void upgradeCustomerTier(Long id, CustomerTier newTier) {
        Customer customer = findCustomerById(id);
        customer.setTier(newTier);
        customerRepository.save(customer);
    }

    public void deactivateCustomer(Long id) {
        Customer customer = findCustomerById(id);
        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    public void activateCustomer(Long id) {
        Customer customer = findCustomerById(id);
        customer.setIsActive(true);
        customerRepository.save(customer);
    }

    public boolean isPhoneAvailable(String phone) {
        return !customerRepository.existsByPhone(phone);
    }

    public boolean isEmailAvailable(String email) {
        return email == null || !customerRepository.existsByEmail(email);
    }

    public long getCustomerCount() {
        return customerRepository.count();
    }

    public long getCustomerCountByTier(CustomerTier tier) {
        return customerRepository.countByTierAndIsActiveTrue(tier);
    }

    public List<Object[]> getCustomerTierStatistics() {
        return customerRepository.getCustomerTierStatistics();
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerException("Customer not found with id: " + id));
    }

    private void validateCustomerRequest(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new CustomerException("Phone number already exists: " + request.getPhone());
        }

        if (request.getEmail() != null && customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerException("Email already exists: " + request.getEmail());
        }
    }
}
