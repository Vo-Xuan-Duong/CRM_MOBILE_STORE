package com.example.Backend.dtos.customercare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCareTaskDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String title;
    private String description;
    private String type;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private String notes;
    private String completionNotes;
    private Long assignedToId;
    private String assignedToName;
    private String createdByName;
    private Boolean isOverdue;
    private Long daysUntilDue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
