package com.alien.bank.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseModel {
    private HttpStatus status;
    private boolean success;
    private Object data;
    private Object errors;
}
