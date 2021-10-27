package com.jsancarb.telegramboot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	@Override
	public void onUpdateReceived(Update update) {
		try {
			if (update.hasMessage()) {
				Message message = update.getMessage();
				if (message.hasText()) {
					handleMessage(message);
				}
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

	private void handleMessage(Message message) throws TelegramApiException {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		if(message.getText().equals("/start")) {
			ReplyKeyboardMarkup key = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboardRow = new ArrayList<>();
			List<KeyboardButton> buttons= new ArrayList<>();
			buttons.add(new KeyboardButton("Tortilla muy hecha"));
			buttons.add(new KeyboardButton("Tortilla medio hecha"));
			buttons.add(new KeyboardButton("Tortilla cruda"));
			keyboardRow.add(new KeyboardRow(buttons));
			key.setKeyboard(keyboardRow);	
			sendMessage.setText("Hola " + message.getChat().getFirstName()+"\n te voy a hacer una pregunta exixtencial\n¿Como te gusta la tortilla?");
			sendMessage.setReplyMarkup(key);
		}else if(message.getText().equals("Tortilla muy hecha")){
			sendMessage.setText("Menos mal que soy un robot, si no te dejo de hablar....");
		}else if(message.getText().equals("Tortilla medio hecha")){
			sendMessage.setText("Tiene que haber de todo.....");
		}else if(message.getText().equals("Tortilla cruda")){
			sendMessage.setText("¡Oleeee!");
		}else {
			sendMessage.setText("¡Ale ya esta no seas pesao!");
		}
		execute(sendMessage);
	}

	@Override
	public String getBotUsername() {
		return "jsancarbBot";
	}

	@Override
	public String getBotToken() {
		return "2076236232:AAG0_DURSU4G-rslA4pAMfe7jbKRQPG8p-g";
	}

}
