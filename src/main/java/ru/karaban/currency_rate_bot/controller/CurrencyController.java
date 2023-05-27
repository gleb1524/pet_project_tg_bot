//package ru.karaban.currency_rate_bot.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.karaban.currency_rate_bot.client.CurrencyRatesClient;
//import ru.karaban.currency_rate_bot.dto.UserDto;
//import ru.karaban.currency_rate_bot.entity.Currency;
//import ru.karaban.currency_rate_bot.model.CurrencyResponse;
//import ru.karaban.currency_rate_bot.service.CurrencyRateService;
//
//import java.util.List;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping(value = "/", produces = APPLICATION_JSON_VALUE)
//public class CurrencyController {
//
//    private final CurrencyRatesClient currencyRatesClient;
//    private final CurrencyRateService service;
//
//    @GetMapping
//    public CurrencyResponse getCurrencies() {
//        return currencyRatesClient.getCurrenciesRates(APPLICATION_JSON_VALUE);
//    }
//
//    @GetMapping("/get")
//    public List<Currency> getCurrenciesList(UserDto userDto) {
//        return service.getCurrencyRateToRub(UserDto.builder()
//                .id(1L)
//                .name("1")
//                .surname("1")
//                .build());
//    }
//
//}
