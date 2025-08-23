package com.example.Backend.services;

import com.example.Backend.dtos.interaction.InteractionRequest;
import com.example.Backend.dtos.interaction.InteractionResponse;
import com.example.Backend.exceptions.ResourceNotFoundException;
import com.example.Backend.models.Customer;
import com.example.Backend.models.Interaction;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.CustomerRepository;
import com.example.Backend.repositorys.InteractionRepository;
import com.example.Backend.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public InteractionResponse createInteraction(InteractionRequest request, Long userId) {
        log.info("Creating interaction for customer: {}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Interaction interaction = Interaction.builder()
                .customer(customer)
                .user(user)
                .type(request.getType())
                .subject(request.getSubject())
                .content(request.getContent())
                .priority(request.getPriority())
                .status(Interaction.InteractionStatus.OPEN)
                .followUpDate(request.getFollowUpDate() != null ? request.getFollowUpDate().toLocalDate() : null)
                .build();

        Interaction savedInteraction = interactionRepository.save(interaction);
        log.info("Interaction created successfully with id: {}", savedInteraction.getId());

        return mapToResponse(savedInteraction);
    }

    public InteractionResponse updateInteraction(Long id, InteractionRequest request) {
        log.info("Updating interaction with id: {}", id);

        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + id));

        interaction.setType(request.getType());
        interaction.setSubject(request.getSubject());
        interaction.setContent(request.getContent());
        interaction.setPriority(request.getPriority());
        interaction.setFollowUpDate(request.getFollowUpDate() != null ? request.getFollowUpDate().toLocalDate() : null);

        Interaction updatedInteraction = interactionRepository.save(interaction);
        log.info("Interaction updated successfully");

        return mapToResponse(updatedInteraction);
    }

    @Transactional(readOnly = true)
    public InteractionResponse getInteractionById(Long id) {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + id));
        return mapToResponse(interaction);
    }

    @Transactional(readOnly = true)
    public Page<InteractionResponse> getAllInteractions(Pageable pageable) {
        Page<Interaction> interactions = interactionRepository.findAll(pageable);
        return interactions.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsByCustomer(Long customerId) {
        List<Interaction> interactions = interactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return interactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsByUser(Long userId) {
        List<Interaction> interactions = interactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return interactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsByType(String type) {
        Interaction.InteractionType interactionType = Interaction.InteractionType.valueOf(type.toUpperCase());
        List<Interaction> interactions = interactionRepository.findByTypeOrderByCreatedAtDesc(interactionType);
        return interactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Interaction> interactions = interactionRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        return interactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> getInteractionsRequiringFollowUp() {
        // Get all interactions and filter those requiring follow-up in service layer
        List<Interaction> allInteractions = interactionRepository.findAll();
        List<Interaction> interactions = allInteractions.stream()
                .filter(interaction -> interaction.getFollowUpDate() != null)
                .sorted((a, b) -> a.getFollowUpDate().compareTo(b.getFollowUpDate()))
                .collect(Collectors.toList());
        return interactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public void closeInteraction(Long id) {
        log.info("Closing interaction with id: {}", id);

        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + id));

        interaction.setStatus(Interaction.InteractionStatus.CLOSED);
        // Note: Model doesn't have resolvedAt field, using outcome field to track closure
        interaction.setOutcome("Closed on " + LocalDateTime.now());

        interactionRepository.save(interaction);
        log.info("Interaction closed successfully");
    }

    public void reopenInteraction(Long id) {
        log.info("Reopening interaction with id: {}", id);

        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + id));

        interaction.setStatus(Interaction.InteractionStatus.OPEN);
        interaction.setOutcome(null); // Clear the outcome when reopening

        interactionRepository.save(interaction);
        log.info("Interaction reopened successfully");
    }

    public void markAsInProgress(Long id) {
        log.info("Marking interaction as in progress with id: {}", id);

        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with id: " + id));

        interaction.setStatus(Interaction.InteractionStatus.IN_PROGRESS);

        interactionRepository.save(interaction);
        log.info("Interaction marked as in progress successfully");
    }

    private InteractionResponse mapToResponse(Interaction interaction) {
        return InteractionResponse.builder()
                .id(interaction.getId())
                .customerId(interaction.getCustomer().getId())
                .customerName(interaction.getCustomer().getFullName())
                .userId(interaction.getUser().getId())
                .userName(interaction.getUser().getFullName())
                .type(interaction.getType())
                .subject(interaction.getSubject())
                .content(interaction.getContent())
                .priority(interaction.getPriority())
                .status(interaction.getStatus())
                .requiresFollowUp(interaction.requiresFollowUp()) // Use method instead of field
                .followUpDate(interaction.getFollowUpDate() != null ? interaction.getFollowUpDate().atStartOfDay() : null) // Convert LocalDate to LocalDateTime
                .resolvedAt(null) // Model doesn't have resolvedAt field, set to null for now
                .createdAt(interaction.getCreatedAt())
                .updatedAt(null) // Model doesn't have updatedAt field, set to null for now
                .build();
    }
}
