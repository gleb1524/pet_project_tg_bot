package ru.karaban.currency_rate_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.karaban.currency_rate_bot.bot.config.BotConfig;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.Currency;
import ru.karaban.currency_rate_bot.service.CurrencyRateService;
import ru.karaban.currency_rate_bot.service.UserService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.karaban.currency_rate_bot.bot.components.BotCommands.HELP_TEXT;
import static ru.karaban.currency_rate_bot.bot.components.BotCommands.LIST_OF_COMMANDS;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateBot extends TelegramLongPollingBot {


    private final BotConfig config;
    private final UserService userService;
    private final CurrencyRateService currencyRateService;
    private int pageNumber = 0;
    private Message message;

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
        if(update.hasCallbackQuery()){
            System.out.println(update.getCallbackQuery().getData());
            String callback = Arrays.stream(update.getCallbackQuery().getData().split(":")).findFirst().orElseThrow();
           switch (callback) {
               case "BACK":
                   pageNumber--;
                   buildMessage(message);
                   return;
               case "NEXT":
                   pageNumber++;
                   buildMessage(message);
                   return;
               case "ORIGINAL":
                   System.out.println("Select source currency");
                   return;
               case "TARGET":
                   System.out.println("Select target currency");
                   return;
           }
        }
        if (update.hasMessage()) {
            this.message = update.getMessage();
            handleMessage(update.getMessage());
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
    }

    @SneakyThrows
    private void buildMessage(Message message) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<String> currencies = currencyRateService
                .getCurrencyRateToRub(createUser(message.getChatId(), message.getFrom().getUserName(), message.getFrom().getLastName()), pageNumber)
                .stream().map(Currency::getCode).collect(Collectors.toList());
        for(String currency : currencies){
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
