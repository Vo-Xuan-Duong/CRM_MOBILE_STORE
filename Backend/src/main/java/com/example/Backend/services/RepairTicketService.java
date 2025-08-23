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
                .issueDesc(request.getIssueDescription()) // Use issueDesc instead of deviceInfo
                .status(RepairTicket.RepairStatus.RECEIVED) // Use RECEIVED instead of PENDING
                .priority(request.getPriority())
                .estimateCost(request.getEstimatedCost()) // Use estimateCost instead of estimatedCost
                .actualCost(BigDecimal.ZERO)
                .receivedAt(LocalDateTime.now()) // Use receivedAt instead of receivedDate
                .build();

        RepairTicket savedTicket = repairTicketRepository.save(repairTicket);
        log.info("Repair ticket created successfully with id: {}", savedTicket.getId());

        return mapToResponse(savedTicket);
    }

    public RepairTicketResponse updateRepairTicket(Long id, RepairTicketRequest request) {
        log.info("Updating repair ticket with id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        ticket.setIssueDesc(request.getIssueDescription()); // Use issueDesc instead of deviceInfo
        ticket.setPriority(request.getPriority());
        ticket.setEstimateCost(request.getEstimatedCost()); // Use estimateCost instead of estimatedCost

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
        // Use a simpler repository method that should exist
        List<RepairTicket> allTickets = repairTicketRepository.findAll();
        List<RepairTicket> tickets = allTickets.stream()
                .filter(ticket -> ticket.getTechnician() != null && ticket.getTechnician().getId().equals(technicianId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
        return tickets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void startRepair(Long id, Long technicianId) {
        log.info("Starting repair for ticket id: {} by technician: {}", id, technicianId);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + technicianId));

        if (ticket.getStatus() != RepairTicket.RepairStatus.RECEIVED) { // Use RECEIVED instead of PENDING
            throw new IllegalStateException("Only received tickets can be started");
        }

        ticket.setStatus(RepairTicket.RepairStatus.REPAIRING); // Use REPAIRING instead of IN_PROGRESS
        ticket.setTechnician(technician); // Use technician instead of assignedTechnician
        // Model doesn't have startDate field, skip this

        repairTicketRepository.save(ticket);
        log.info("Repair started successfully");
    }

    public void completeRepair(Long id, BigDecimal actualCost, String diagnosis) { // Use diagnosis instead of completionNotes
        log.info("Completing repair for ticket id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        if (ticket.getStatus() != RepairTicket.RepairStatus.REPAIRING) { // Use REPAIRING instead of IN_PROGRESS
            throw new IllegalStateException("Only repairing tickets can be completed");
        }

        ticket.setStatus(RepairTicket.RepairStatus.DONE); // Use DONE instead of COMPLETED
        ticket.setActualCost(actualCost);
        ticket.setDiagnosis(diagnosis); // Use diagnosis instead of completionNotes
        ticket.setClosedAt(LocalDateTime.now()); // Use closedAt instead of completedDate

        repairTicketRepository.save(ticket);
        log.info("Repair completed successfully");
    }

    public void deliverDevice(Long id) {
        log.info("Delivering device for ticket id: {}", id);

        RepairTicket ticket = repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found with id: " + id));

        if (ticket.getStatus() != RepairTicket.RepairStatus.DONE) { // Use DONE instead of COMPLETED
            throw new IllegalStateException("Only done tickets can be delivered");
        }

        ticket.setStatus(RepairTicket.RepairStatus.DELIVERED);
        // Model doesn't have deliveredDate field, skip this

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
        // Model doesn't have cancellationReason field, use diagnosis instead
        ticket.setDiagnosis("Cancelled: " + cancellationReason);

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
                .serialNumber(ticket.getSerialUnit() != null ? ticket.getSerialUnit().getImei() : null) // Use imei instead of serialNumber
                .issueDescription(ticket.getIssueDesc()) // Use issueDesc field
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .estimatedCost(ticket.getEstimateCost()) // Use estimateCost field
                .actualCost(ticket.getActualCost())
                .assignedTechnicianId(ticket.getTechnician() != null ? ticket.getTechnician().getId() : null)
                .assignedTechnicianName(ticket.getTechnician() != null ? ticket.getTechnician().getFullName() : null)
                .completionNotes(ticket.getDiagnosis()) // Use diagnosis for completion notes
                .receivedDate(ticket.getReceivedAt()) // Use receivedAt field
                .completedDate(ticket.getClosedAt()) // Use closedAt for completedDate field
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
