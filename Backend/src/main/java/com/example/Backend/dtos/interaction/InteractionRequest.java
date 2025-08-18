package com.example.Backend.dtos.interaction;

import com.example.Backend.models.Interaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Interaction type is required")
    private Interaction.InteractionType type;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Priority is required")
    private Interaction.InteractionPriority priority;

    private Boolean requiresFollowUp = false;

    private LocalDateTime followUpDate;
}
