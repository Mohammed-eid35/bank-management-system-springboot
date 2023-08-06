package com.alien.bank.management.system.model.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequestModel {
    @NotNull(message = "Card number is required")
    private String card_number;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount can't be negative")
    private Double amount;
}