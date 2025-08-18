package com.example.Backend.dtos.email;

import jakarta.validation.constraints.NotEmpty;
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
public class BulkEmailRequest {

    @NotEmpty(message = "Recipients list cannot be empty")
    private List<String> recipients;

    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> templateVariables;
    private boolean isHtml = true;
}
