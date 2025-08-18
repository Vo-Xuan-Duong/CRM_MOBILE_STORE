package com.example.Backend.services;

import com.example.Backend.dtos.repair.RepairTicketRequest;
import com.example.Backend.dtos.repair.RepairTicketResponse;
import com.example.Backend.exceptions.ResourceNotFoundException;
import com.example.Backend.models.Customer;
import com.example.Backend.models.RepairTicket;
import com.example.Backend.models.SerialUnit;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.CustomerRepository;
import com.example.Backend.repositorys.RepairTicketRepository;
import com.example.Backend.repositorys.SerialUnitRepository;
import com.example.Backend.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RepairTicketService {

    private final RepairTicketRepository repairTicketRepository;
    private final CustomerRepository customerRepository;
    private final SerialUnitRepository serialUnitRepository;
    private final UserRepository userRepository;

    public RepairTicketResponse createRepairTicket(RepairTicketRequest request) {
        log.info("Creating repair ticket for customer: {}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        SerialUnit serialUnit = null;
        if (request.getSerialUnitId() != null) {
            serialUnit = serialUnitRepository.findById(request.getSerialUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Serial unit not found with id: " + request.getSerialUnitId()));
        }

        // Generate unique ticket number
        String ticketNumber = generateTicketNumber();

        RepairTicket repairTicket = RepairTicket.builder()
                .ticketNumber(ticketNumber)
                .customer(customer)
                .serialUnit(serialUnit)
                .deviceInfo(request.getDeviceInfo())
                .issueDescription(request.getIssueDescription())
                .status(RepairTicket.RepairStatus.PENDING)
                .priority(request.getPriority())
                .estimatedCost(request.getEstimatedCost())
                .actualCost(BigDecimal.ZERO)
                .receivedDate(LocalDateTime.now())
                .build();

        RepairTicket savedTicket = repairTicketRepository.save(repairTicket);
        log.info("Repair ticket created successfully with id: {}", savedTicket.getId());

        return mapToResponse(savedTicket);
    }

    public RepairTicketResponse updateRepairTicket(Long id, RepairTicketRequest request) {
        log.info("Updating repair ticket with id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        ticket.setDeviceInfo(request.getDeviceInfo());
        ticket.setIssueDescription(request.getIssueDescription());
        ticket.setPriority(request.getPriority());
        ticket.setEstimatedCost(request.getEstimatedCost());

        RepairTicket updatedTicket = repairTicketRepository.save(ticket);
        log.info("Repair ticket updated successfully");

        return mapToResponse(updatedTicket);
    }

    @Transactional(readOnly = true)
    public RepairTicketResponse getRepairTicketById(Long id) {
        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public RepairTicketResponse getRepairTicketByTicketNumber(String ticketNumber) {
        RepairTicket ticket = repairTicketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with number: " + ticketNumber));
        return mapToResponse(ticket);
    }

    @Transactional(readOnly = true)
    public Page<RepairTicketResponse> getAllRepairTickets(Pageable pageable) {
        Page<RepairTicket> tickets = repairTicketRepository.findAll(pageable);
        return tickets.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<RepairTicketResponse> getRepairTicketsByCustomer(Long customerId) {
        List<RepairTicket> tickets = repairTicketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return tickets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairTicketResponse> getRepairTicketsByStatus(String status) {
        RepairTicket.RepairStatus repairStatus = RepairTicket.RepairStatus.valueOf(status.toUpperCase());
        List<RepairTicket> tickets = repairTicketRepository.findByStatusOrderByCreatedAtDesc(repairStatus);
        return tickets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RepairTicketResponse> getRepairTicketsByTechnician(Long technicianId) {
        List<RepairTicket> tickets = repairTicketRepository.findByAssignedTechnicianIdOrderByCreatedAtDesc(technicianId);
        return tickets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void startRepair(Long id, Long technicianId) {
        log.info("Starting repair for ticket id: {} by technician: {}", id, technicianId);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + technicianId));

        if (ticket.getStatus() != RepairTicket.RepairStatus.PENDING) {
            throw new IllegalStateException("Only pending tickets can be started");
        }

        ticket.setStatus(RepairTicket.RepairStatus.IN_PROGRESS);
        ticket.setAssignedTechnician(technician);
        ticket.setStartDate(LocalDateTime.now());

        repairTicketRepository.save(ticket);
        log.info("Repair started successfully");
    }

    public void completeRepair(Long id, BigDecimal actualCost, String completionNotes) {
        log.info("Completing repair for ticket id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        if (ticket.getStatus() != RepairTicket.RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress tickets can be completed");
        }

        ticket.setStatus(RepairTicket.RepairStatus.COMPLETED);
        ticket.setActualCost(actualCost);
        ticket.setCompletionNotes(completionNotes);
        ticket.setCompletedDate(LocalDateTime.now());

        repairTicketRepository.save(ticket);
        log.info("Repair completed successfully");
    }

    public void deliverDevice(Long id) {
        log.info("Delivering device for ticket id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        if (ticket.getStatus() != RepairTicket.RepairStatus.COMPLETED) {
            throw new IllegalStateException("Only completed tickets can be delivered");
        }

        ticket.setStatus(RepairTicket.RepairStatus.DELIVERED);
        ticket.setDeliveredDate(LocalDateTime.now());

        repairTicketRepository.save(ticket);
        log.info("Device delivered successfully");
    }

    public void cancelRepair(Long id, String cancellationReason) {
        log.info("Cancelling repair for ticket id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        if (ticket.getStatus() == RepairTicket.RepairStatus.DELIVERED ||
            ticket.getStatus() == RepairTicket.RepairStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel delivered or already cancelled tickets");
        }

        ticket.setStatus(RepairTicket.RepairStatus.CANCELLED);
        ticket.setCancellationReason(cancellationReason);

        repairTicketRepository.save(ticket);
        log.info("Repair cancelled successfully");
    }

    private String generateTicketNumber() {
        String prefix = "RPR";
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        return prefix + timestamp;
    }

    private RepairTicketResponse mapToResponse(RepairTicket ticket) {
        return RepairTicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerId(ticket.getCustomer().getId())
                .customerName(ticket.getCustomer().getFullName())
                .customerPhone(ticket.getCustomer().getPhone())
                .serialUnitId(ticket.getSerialUnit() != null ? ticket.getSerialUnit().getId() : null)
                .serialNumber(ticket.getSerialUnit() != null ? ticket.getSerialUnit().getSerialNumber() : null)
                .deviceInfo(ticket.getDeviceInfo())
                .issueDescription(ticket.getIssueDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .estimatedCost(ticket.getEstimatedCost())
                .actualCost(ticket.getActualCost())
                .assignedTechnicianId(ticket.getAssignedTechnician() != null ? ticket.getAssignedTechnician().getId() : null)
                .assignedTechnicianName(ticket.getAssignedTechnician() != null ? ticket.getAssignedTechnician().getFullName() : null)
                .completionNotes(ticket.getCompletionNotes())
                .cancellationReason(ticket.getCancellationReason())
                .receivedDate(ticket.getReceivedDate())
                .startDate(ticket.getStartDate())
                .completedDate(ticket.getCompletedDate())
                .deliveredDate(ticket.getDeliveredDate())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
