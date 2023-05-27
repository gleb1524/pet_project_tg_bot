package ru.karaban.currency_rate_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.karaban.currency_rate_bot.dto.CurrencyDto;
import ru.karaban.currency_rate_bot.dto.CurrencyRateOrderDto;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.service.CurrencyRateOrderService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders", produces = APPLICATION_JSON_VALUE)
public class CurrencyOrderController {

    private final CurrencyRateOrderService service;

    @GetMapping()
    public List<CurrencyRateOrderDto> getAllOrders(UserDto userDto) {
        return service.getAllOrders(userDto);
    }

    @PostMapping()
    public void createOrders() {
    }
}
