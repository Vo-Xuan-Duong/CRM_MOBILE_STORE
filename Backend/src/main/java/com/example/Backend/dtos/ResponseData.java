package com.example.Backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData <T>{
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private T data;
}
