package com.durgesh.banking.dto;

public record TransferFundDto(Long fromAccountId,
                              Long toAccountId,
                              double amount) {
}
