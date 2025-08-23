package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "repair_ticket")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ticket number is required")
    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_unit_id")
    private SerialUnit serialUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_id")
    private Warranty warranty;

    @Column(name = "received_at", nullable = false)
    @Builder.Default
    private LocalDateTime receivedAt = LocalDateTime.now();

    @NotBlank(message = "Issue description is required")
    @Column(name = "issue_desc", nullable = false, columnDefinition = "TEXT")
    private String issueDesc;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RepairStatus status = RepairStatus.RECEIVED;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "estimate_cost", precision = 12, scale = 2)
    private BigDecimal estimateCost;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "actual_cost", precision = 12, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "under_warranty", nullable = false)
    @Builder.Default
    private Boolean underWarranty = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RepairPriority priority = RepairPriority.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum RepairStatus {
        RECEIVED("received"),
        DIAGNOSING("diagnosing"),
        WAITING_PARTS("waiting_parts"),
        REPAIRING("repairing"),
        TESTING("testing"),
        DONE("done"),
        DELIVERED("delivered"),
        CANCELLED("cancelled");

        private final String value;

        RepairStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum RepairPriority {
        LOW("low"),
        NORMAL("normal"),
        HIGH("high"),
        URGENT("urgent");

        private final String value;

        RepairPriority(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isActive() {
        return status != RepairStatus.DELIVERED && status != RepairStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == RepairStatus.DONE || status == RepairStatus.DELIVERED;
    }

    public boolean canCancel() {
        return status == RepairStatus.RECEIVED || status == RepairStatus.DIAGNOSING;
    }

    public boolean requiresPayment() {
        return !underWarranty && actualCost != null && actualCost.compareTo(BigDecimal.ZERO) > 0;
    }
}
