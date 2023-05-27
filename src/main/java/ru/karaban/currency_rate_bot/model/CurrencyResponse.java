package ru.karaban.currency_rate_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CurrencyResponse {

    @JsonProperty("Valute")
    private Map<String, Rate> rate;
}
