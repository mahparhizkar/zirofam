package com.zirofam.interview.controller.dto;

import com.zirofam.interview.domain.enumeration.AccountingStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FinancialDto {

    private String id;

    private AccountingStatus status;

    private String user;

    private BigDecimal amount;
}
