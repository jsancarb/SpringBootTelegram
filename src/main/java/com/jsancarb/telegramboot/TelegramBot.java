package com.jsancarb.telegramboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.NonNull;

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
					handleStart(message);
				} else if (message.getText().startsWith("/prediccion")) {
					handlePrevention(message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleStart(Message message) throws TelegramApiException {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("Hola " + message.getChat().getFirstName()
				+ " soy tu chatbot favorito 😎 y puedo predecir el tiempo "
				+ "solo tienes que usar el comando /prediccion: + el nombre de la provincia por ejemplo '/prediccion:barcelona'");
		execute(sendMessage);
	}

	private void handlePrevention(Message message) throws Exception {
		Map<String, String> provinces = provinceCode(message.getText().substring(12, message.getText().length()));
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		
		String response = "¿Quisite decir?\n";
		if (provinces.size() < 1) {
			response = "No se han encontrado resultados";
		}
		if (message.getText().length() > 12) {
			for (Entry<String, String> code : provinces.entrySet()) {
				if (provinces.size() == 1) {
					response = weatherPrediction(code.getValue());
				} else {
					response += "/prediccion:" + code.getKey() + "\n";
				}
			}
		} else {
			response = "Es necesario indicar el nombre de la provincia";
		}
		sendMessage.setText(response);
		execute(sendMessage);
	}

	private static String weatherPrediction(String provinceCode) throws IOException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		String prediction = "";

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			URLConnection con = new URL(AEMET_URL + provinceCode + "/?api_key=" + AEMET_KEY).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "ISO-8859-1"));
			String line = "";
			String doc = "";
			while ((line = br.readLine()) != null) {
				doc += line;
			}
			JSONObject pred = new JSONObject(doc);
			con = new URL(pred.getString("datos")).openConnection();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), "ISO-8859-1"));
			while ((line = br.readLine()) != null) {
				prediction += line + "\n";
			}
			prediction = prediction.replaceAll("[\n]{2,}", "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return prediction;
	}

	private Map<String, String> provinceCode(String provinceName) throws IOException {
		Map<String, String> codes = new HashMap<>();
		InputStream is = new URL(OPEN_PROVINCE + provinceName).openStream();
		JSONObject data = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
			data = new JSONObject(br.readLine());
		} catch (IOException e) {
			return codes;
		} finally {
			is.close();
		}
		JSONArray provinces = data.getJSONArray("records");
		for (int i = 0; i < provinces.length(); i++) {
			String codigo = provinces.getJSONObject(i).getJSONObject("fields").getString("codigo");
			String name = provinces.getJSONObject(i).getJSONObject("fields").getString("provincia");
			codes.put(name, codigo);
		}
		return codes;
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
