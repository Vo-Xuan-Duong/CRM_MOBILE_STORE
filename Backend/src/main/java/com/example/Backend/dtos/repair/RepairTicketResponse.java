package com.example.Backend.dtos.repair;

import com.example.Backend.models.RepairTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketResponse {

    private Long id;
    private String ticketNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long serialUnitId;
    private String serialNumber;
    private String deviceInfo;
    private String issueDescription;
    private RepairTicket.RepairStatus status;
    private RepairTicket.RepairPriority priority;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private String completionNotes;
    private String cancellationReason;
    private LocalDateTime receivedDate;
    private LocalDateTime startDate;
    private LocalDateTime completedDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
