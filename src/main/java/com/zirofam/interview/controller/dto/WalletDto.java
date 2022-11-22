package com.zirofam.interview.controller.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WalletDto {

    private String user;

    private BigDecimal balance;
}
