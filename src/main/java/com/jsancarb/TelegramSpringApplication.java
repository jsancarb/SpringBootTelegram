package com.jsancarb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.jsancarb.telegramboot.TelegramBot;

@SpringBootApplication
public class TelegramSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramSpringApplication.class, args);
	}

}
