package com.jsancarb.telegramboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private static final String TELEGRAM_USER = "jsancarbBot";
	private static final String TELEGRAM_KEY = "2076236232:AAG0_DURSU4G-rslA4pAMfe7jbKRQPG8p-g";
	private static final String AEMET_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqc2FuY2FyYkBnbWFpbC5jb20iLCJqdGkiOiJkZTQ1M2RhYS0wNzcxLTRiZGItYjYxYi04ZmZkOGNkMWE3MTkiLCJpc3MiOiJBRU1FVCIsImlhdCI6MTYzNTQ4NTY5NSwidXNlcklkIjoiZGU0NTNkYWEtMDc3MS00YmRiLWI2MWItOGZmZDhjZDFhNzE5Iiwicm9sZSI6IiJ9.RLQJS9N8uqPQzhkntKsqPWPDkB4ht92jI_BIWf598S8";
	private static final String AEMET_URL = "https://opendata.aemet.es/opendata/api/prediccion/provincia/hoy/";
	private static final String OPEN_PROVINCE = "https://public.opendatasoft.com/api/records/1.0/search/?dataset=provincias-espanolas&q=";

	@Override
	public void onUpdateReceived(Update update) {
		try {
			if (update.hasMessage()) {
				Message message = update.getMessage();
				if (message.getText().equals("/start")) {
					try {
						System.out.println(provinceCode("Barcelona"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handleBanna(message);
				} else if (message.getText().equals("/tortilla")) {
					handleReplyKeyboard(message);
				}
			}
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

	private void handleBanna(Message message) throws TelegramApiException {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("\uD83C\uDF4C");
		execute(sendMessage);
	}

	private void handleReplyKeyboard(Message message) throws TelegramApiException {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		if (message.getText().equals("/tortilla")) {
			ReplyKeyboardMarkup key = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboardRow = new ArrayList<>();
			List<KeyboardButton> buttons = new ArrayList<>();
			buttons.add(new KeyboardButton("Tortilla muy hecha"));
			buttons.add(new KeyboardButton("Tortilla medio hecha"));
			buttons.add(new KeyboardButton("Tortilla cruda"));
			keyboardRow.add(new KeyboardRow(buttons));
			key.setKeyboard(keyboardRow);
			key.setOneTimeKeyboard(true);
			sendMessage.setText("Hola " + message.getChat().getFirstName()
					+ "\n te voy a hacer una pregunta exixtencial\n¿Como te gusta la tortilla?");
			sendMessage.setReplyMarkup(key);
		} else if (message.getText().equals("Tortilla muy hecha")) {
			sendMessage.setText("Menos mal que soy un robot, si no te dejo de hablar....");
		} else if (message.getText().equals("Tortilla medio hecha")) {
			sendMessage.setText("Tiene que haber de todo.....");
		} else if (message.getText().equals("Tortilla cruda")) {
			sendMessage.setText("¡Oleeee!");
		} else {
			sendMessage.setText("¡Ale ya esta, no seas pesao!");
		}
		execute(sendMessage);
	}

	private String weatherPrediction(String provinceCode) {
		return null;
	}

	private String provinceCode(String provinceName) throws IOException {
		String code = null;
		InputStream is = new URL(OPEN_PROVINCE + provinceName).openStream();
		JSONObject provinces = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			provinces = new JSONObject(br.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			is.close();
		}
		System.out.println(provinces);
		for (int i = 0; i < provinces.getJSONArray("records").length(); i++) {
			JSONObject fields = new JSONObject(provinces.getJSONArray("records").get(i));
			code = fields.toString();
		}
		return code;
	}

	@Override
	public String getBotUsername() {
		return TELEGRAM_USER;
	}

	@Override
	public String getBotToken() {
		return TELEGRAM_KEY;
	}

}
