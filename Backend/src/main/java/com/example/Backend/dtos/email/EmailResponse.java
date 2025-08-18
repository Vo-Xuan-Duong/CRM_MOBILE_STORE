package com.example.Backend.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private String id;
    private String to;
    private String subject;
    private String status;
    private String message;
    private LocalDateTime sentAt;
    private boolean success;
}
