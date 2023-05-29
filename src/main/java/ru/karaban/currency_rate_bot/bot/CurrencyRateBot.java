package ru.karaban.currency_rate_bot.bot;

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
import ru.karaban.currency_rate_bot.bot.config.BotConfig;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.Currency;
import ru.karaban.currency_rate_bot.service.CurrencyRateService;
import ru.karaban.currency_rate_bot.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateBot extends TelegramLongPollingBot {


    private final BotConfig config;
    private final UserService userService;
    private final CurrencyRateService currencyRateService;
    private int pageNumber = 0;
    private Message message;
    private String sourceCurrency;
    private String targetCurrency;

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
            this.message = update.getMessage();
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callback = Arrays.stream(callbackQuery.getData().split(":")).findFirst().orElseThrow();
        switch (callback) {
            case "BACK":
                pageNumber--;
                if (pageNumber < 0) {
                    pageNumber = 0;
                }
                buildMessage(message);
                return;
            case "NEXT":
                pageNumber++;
                buildMessage(message);
                return;
            case "ORIGINAL":
                sourceCurrency = callbackQuery.getData().substring(10);
                log.info("Select source currency: " + sourceCurrency);
                return;
            case "TARGET":
                targetCurrency = callbackQuery.getData().substring(8);
                log.info("Select target currency: " + targetCurrency);
        }
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
                        buildMessage(message);
                        return;
                }
            }
        }
        if (message.hasText()) {
            String messageText = message.getText();
            Optional<Long> value = parseLong(messageText);
            if (value.isPresent()) {
                double rate = 0;
                if (targetCurrency.equals("RUB")) {
                    rate = currencyRateService.getRateToRub(sourceCurrency).doubleValue()*value.get();
                } else if (sourceCurrency.equals("RUB")) {
                    rate = value.get()/currencyRateService.getRateToRub(targetCurrency).doubleValue();
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


    @SneakyThrows
    private void buildMessage(Message message) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<String> currencies = currencyRateService
                .getAllCurrencyRateToRub(createUser(message.getChatId(), message.getFrom().getUserName(), message.getFrom().getLastName()), pageNumber)
                .stream().map(Currency::getCode).collect(Collectors.toList());
        for (String currency : currencies) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(currency)
                                    .callbackData("ORIGINAL: " + currency)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(currency)
                                    .callbackData("TARGET: " + currency)
                                    .build()
                    )
            );
        }
        buttons.add(
                Arrays.asList(
                        InlineKeyboardButton.builder()
                                .text("RUB")
                                .callbackData("ORIGINAL: RUB")
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("RUB")
                                .callbackData("TARGET: RUB")
                                .build()
                )
        );
        buttons.add(
                Arrays.asList(
                        InlineKeyboardButton.builder()
                                .text("Назад")
                                .callbackData("BACK: " + pageNumber)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Вперед")
                                .callbackData("NEXT: " + pageNumber)
                                .build()
                )
        );
        execute(SendMessage.builder()
                .text("Пожалуйста выберете валюты для конвертации")
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

//    private void botAnswerUtils(String receivedMessage, long chatId, String userName, String userLastname) {
//        switch (receivedMessage) {
//            case "/start":
//                startBot(chatId, userName, userLastname);
//                break;
//            case "/help":
//                sendHelpText(chatId, HELP_TEXT);
//                break;
//            default:
//                break;
//        }
//    }

    private UserDto createUser(Long chatId, String userName, String userSurname) {
        UserDto userDto = UserDto.builder()
                .id(chatId)
                .name(userName)
                .surname(userSurname)
                .build();
        try {
            userService.createUser(userDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return userDto;
    }

    private void sendHelpText(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButtonHelp = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButtonStart = new InlineKeyboardButton();
        inlineKeyboardButtonHelp.setText("Help");
        inlineKeyboardButtonStart.setText("Start");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButtonHelp);
        keyboardButtonsRow1.add(inlineKeyboardButtonStart);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Test");
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
