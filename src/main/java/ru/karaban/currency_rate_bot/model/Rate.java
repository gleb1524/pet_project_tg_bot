package ru.karaban.currency_rate_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Rate {

    @JsonProperty("CharCode")
    private String code;
    @JsonProperty("NumCode")
    private Long isoCode;
    @JsonProperty("Nominal")
    private int nominal;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private BigDecimal rate;
    @JsonProperty("Previous")
    private BigDecimal previousRate;
}
