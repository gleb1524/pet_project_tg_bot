package ru.karaban.currency_rate_bot.bot.components.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.karaban.currency_rate_bot.bot.components.PageNumber;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class PageNumberHashMap implements PageNumber {
    private final Map<Long, Integer> currentPage = new HashMap<>();

    @Override
    public int getCurrentPage(Long chatId) {
        log.info("Send page number in chat " + chatId);
        return currentPage.getOrDefault(chatId, 0);
    }

    @Override
    public void setCurrentPage(Long chatId, Integer pageNumber) {
        currentPage.put(chatId, pageNumber);
    }

    public PageNumberHashMap() {
        log.info("init PageNumberHashMap");
    }
}
