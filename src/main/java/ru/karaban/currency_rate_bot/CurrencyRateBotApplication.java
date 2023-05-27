package ru.karaban.currency_rate_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableFeignClients
public class CurrencyRateBotApplication {

	public static void main(String[] args) {

		SpringApplication.run(CurrencyRateBotApplication.class, args);
	}

}
