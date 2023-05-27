package ru.karaban.currency_rate_bot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class CurrencyDto {

    private String code;
    private Long id;
    private BigDecimal rateToRUB;
}
