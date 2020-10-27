package com.n26.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class TransactionRequest {
    private BigDecimal amount ;
    private Timestamp timestamp ;
}
