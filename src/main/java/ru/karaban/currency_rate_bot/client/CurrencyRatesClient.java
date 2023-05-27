package ru.karaban.currency_rate_bot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.karaban.currency_rate_bot.model.CurrencyResponse;

@FeignClient(value = "cbrf", url = "https://www.cbr-xml-daily.ru/daily_json.js")
public interface CurrencyRatesClient {
    @GetMapping
    CurrencyResponse getCurrenciesRates(@RequestHeader String contentType);
}
