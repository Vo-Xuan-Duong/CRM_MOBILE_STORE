package com.example.Backend.dtos.interaction;

import com.example.Backend.models.Interaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String userName;
    private Interaction.InteractionType type;
    private String subject;
    private String content;
    private Interaction.InteractionPriority priority;
    private Interaction.InteractionStatus status;
    private Boolean requiresFollowUp;
    private LocalDateTime followUpDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
