package ru.karaban.currency_rate_bot.converter;

import org.springframework.stereotype.Component;
import ru.karaban.currency_rate_bot.dto.CurrencyDto;
import ru.karaban.currency_rate_bot.dto.CurrencyRateOrderDto;
import ru.karaban.currency_rate_bot.dto.OptionsDto;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.Currency;
import ru.karaban.currency_rate_bot.entity.CurrencyRateOrder;
import ru.karaban.currency_rate_bot.entity.Options;
import ru.karaban.currency_rate_bot.entity.User;

import java.math.BigDecimal;

@Component
public class DtoEntityConverter {
    public Currency dtoToEntityCurrency(CurrencyDto dto) {
        return Currency.builder()
                .isoCode(dto.getId())
                .code(dto.getCode())
                .rateToRUB(dto.getRateToRUB())
                .build();
    }

    public CurrencyDto entityToDtoCurrency(Currency entity) {
        return CurrencyDto.builder()
                .code(entity.getCode())
                .id(entity.getIsoCode())
                .rateToRUB(entity.getRateToRUB())
                .build();
    }

    public User dtoToEntityUser(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .surname(dto.getSurname())
                .build();
    }

    public UserDto entityToDtoUser(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .build();
    }

    public Options dtoToEntityOptions(OptionsDto dto) {
        return Options.builder()
                .currency(dtoToEntityCurrency(dto.getCurrencyDto()))
                .user(dtoToEntityUser(dto.getUserDto()))
                .build();
    }

    public OptionsDto entityToDtoOptions(Options entity) {
        return OptionsDto.builder()
                .currencyDto(entityToDtoCurrency(entity.getCurrency()))
                .userDto(entityToDtoUser(entity.getUser()))
                .build();
    }

    public CurrencyRateOrder dtoToEntityCurrencyRateOrder(CurrencyRateOrderDto dto) {
        return CurrencyRateOrder.builder()
                .currency(dtoToEntityCurrency(dto.getCurrencyDto()))
                .user(dtoToEntityUser(dto.getUserDto()))
                .desiredRate(BigDecimal.valueOf(dto.getDesiredRate()))
                .build();
    }

    public CurrencyRateOrderDto entityToDtoCurrencyRateOrder(CurrencyRateOrder entity){
        return CurrencyRateOrderDto.builder()
                .currencyDto(entityToDtoCurrency(entity.getCurrency()))
                .userDto(entityToDtoUser(entity.getUser()))
                .desiredRate(entity.getDesiredRate().longValue())
                .build();
    }
}
