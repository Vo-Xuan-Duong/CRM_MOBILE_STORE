package com.example.Backend.dtos.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecGroupResponse {

    private Long id;
    private String name;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
