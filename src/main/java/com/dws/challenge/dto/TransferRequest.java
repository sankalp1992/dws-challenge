package com.dws.challenge.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotEmpty(message = "The accountFromId must not be empty.")
    private String accountFromId;

    @NotEmpty(message = "The accountToId must not be empty.")
    private String accountToId;

    @NotNull(message = "Transfer amount must be provided.")
    @Positive(message = "Transfer amount must be a positive number.")
    private BigDecimal amount;
}

