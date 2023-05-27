package ru.karaban.currency_rate_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.karaban.currency_rate_bot.entity.Currency;
import ru.karaban.currency_rate_bot.entity.User;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class CurrencyRateOrderDto {

    private UserDto userDto;
    private CurrencyDto currencyDto;
    private Long desiredRate;
}
