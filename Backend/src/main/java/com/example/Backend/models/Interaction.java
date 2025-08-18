package com.example.Backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "interaction")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;

    @Enumerated(EnumType.STRING)
    private InteractionDirection direction;

    private String channel;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String outcome;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum InteractionType {
        CALL("call"),
        SMS("sms"),
        EMAIL("email"),
        CHAT("chat"),
        VISIT("visit"),
        NOTE("note"),
        COMPLAINT("complaint"),
        FEEDBACK("feedback");

        private final String value;

        InteractionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum InteractionDirection {
        INBOUND("inbound"),
        OUTBOUND("outbound");

        private final String value;

        InteractionDirection(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean requiresFollowUp() {
        return followUpDate != null && followUpDate.isAfter(LocalDate.now());
    }

    public boolean isOverdue() {
        return followUpDate != null && followUpDate.isBefore(LocalDate.now());
    }
}
