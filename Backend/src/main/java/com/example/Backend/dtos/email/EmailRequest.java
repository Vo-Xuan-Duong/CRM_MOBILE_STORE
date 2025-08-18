package com.example.Backend.dtos.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Recipient email is required")
    private String to;

    private List<String> cc;
    private List<String> bcc;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private String templateName;
    private Map<String, Object> templateVariables;
    private List<String> attachments;
    private boolean isHtml = true;
}
