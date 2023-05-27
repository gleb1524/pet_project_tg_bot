package ru.karaban.currency_rate_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OptionsDto {

    private UserDto userDto;
    private CurrencyDto currencyDto;
}
