package ru.karaban.currency_rate_bot.bot;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.karaban.currency_rate_bot.bot.components.BotCommands;
import ru.karaban.currency_rate_bot.bot.components.BotCurrency;
import ru.karaban.currency_rate_bot.bot.components.BuildMessage;

import ru.karaban.currency_rate_bot.bot.components.PageNumber;
import ru.karaban.currency_rate_bot.bot.config.BotConfig;
import ru.karaban.currency_rate_bot.bot.stratage.HandleCallBack;
import ru.karaban.currency_rate_bot.service.CurrencyRateService;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Data
public class CurrencyRateBot extends TelegramLongPollingBot {


    private final BotConfig config;
    private final CurrencyRateService currencyRateService;
    private final BuildMessage buildMessage;
    private final List<HandleCallBack> handleCallBackMap;
    private final PageNumber currentPageNumber;
    private final BotCurrency botCurrency;
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callback = Arrays.stream(callbackQuery.getData().split(":")).findFirst().orElseThrow();
        switch (callback) {
            case "ORIGINAL":
                botCurrency.setSourceCurrency(callbackQuery.getMessage().getChatId(),callbackQuery.getData().substring(10));
                log.info("Select source currency: " + botCurrency.getSourceCurrency(callbackQuery.getMessage().getChatId()));
                return;
            case "TARGET":
                botCurrency.setTargetCurrency(callbackQuery.getMessage().getChatId(),callbackQuery.getData().substring(8));
                log.info("Select source target: " + botCurrency.getSourceCurrency(callbackQuery.getMessage().getChatId()));
                return;
        }
        Map<String, HandleCallBack> stratagesMap = new HashMap<>();
        for (HandleCallBack handleCallBack : handleCallBackMap) {
            stratagesMap.putAll(handleCallBack.getStratage(callback));
        }
        List<List<InlineKeyboardButton>> lists = stratagesMap.get(callback).handleCallBack(callbackQuery);
        execute(SendMessage.builder()
                .text("Пожалуйста выберете валюты для конвертации")
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(lists).build())
                .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/set_currency":
                        execute(SendMessage.builder()
                                .text("Пожалуйста выберете валюты для конвертации")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buildMessage.buildMessage(message.getChatId(), message.getFrom().getFirstName(), message.getFrom().getLastName(), 0)).build())
                                .build());
                        currentPageNumber.setCurrentPage(message.getChatId(), 0);
                        return;
                    case "/help":
                        execute(SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text(BotCommands.HELP_TEXT)
                                .build());
                }
            }
        }
        if (message.hasText()) {
            String messageText = message.getText();
            Optional<Long> value = parseLong(messageText);
            if (value.isPresent()) {
                double rate = 0;
                String sourceCurrency = botCurrency.getSourceCurrency(message.getChatId());
                String targetCurrency = botCurrency.getTargetCurrency(message.getChatId());
                if (targetCurrency.equals("RUB")) {
                    rate = currencyRateService.getRateToRub(sourceCurrency).doubleValue() * value.get();
                } else if (sourceCurrency.equals("RUB")) {
                    rate = value.get() / currencyRateService.getRateToRub(targetCurrency).doubleValue();
                } else {
                    rate = currencyRateService.getDifferentCurrencyRate(sourceCurrency, targetCurrency).doubleValue() * value.get();
                }
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString()).text(String.format("%d %s is %4.2f %s",
                                value.get(), sourceCurrency, rate, targetCurrency))
                        .build());
            }
        }
    }

    private Optional<Long> parseLong(String messageText) {
        try {
            return Optional.of(Long.parseLong(messageText));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
