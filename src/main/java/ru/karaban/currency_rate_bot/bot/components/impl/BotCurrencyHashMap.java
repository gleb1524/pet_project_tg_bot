package ru.karaban.currency_rate_bot.bot.components.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.karaban.currency_rate_bot.bot.components.BotCurrency;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotCurrencyHashMap implements BotCurrency {
   private final Map<Long, String> sourceCurrency = new HashMap<>();
   private final Map<Long, String> targetCurrency = new HashMap<>();
    @Override
    public void setSourceCurrency(Long chatId, String codeCurrency) {
        sourceCurrency.put(chatId,codeCurrency);
        log.info("Set source currency value: " + codeCurrency);
    }

    @Override
    public void setTargetCurrency(Long chatId, String codeCurrency) {
        targetCurrency.put(chatId, codeCurrency);
        log.info("Set target currency value: " + codeCurrency);
    }

    @Override
    public String getSourceCurrency(Long chatId) {
        return sourceCurrency.get(chatId);
    }

    @Override
    public String getTargetCurrency(Long chatId) {
        return targetCurrency.get(chatId);
    }
}
