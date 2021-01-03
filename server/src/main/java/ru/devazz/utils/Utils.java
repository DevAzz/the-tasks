package ru.devazz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

public class Utils {

	private static Utils instance;

	/**
	 * Конструктор
	 */
	private Utils() {
	}

	/**
	 * Возвращает единственный экземпляр класса
	 *
	 * @return единственный экземпляр класса DialogUtils
	 */
	public static Utils getInstance() {
		if (null == instance) {
			instance = new Utils();
		}
		return instance;
	}

	/**
	 * Функция хеш суммы SHA1
	 *
	 * @param input - входные данные
	 * @return sha1 хеш от input
	 * @throws NoSuchAlgorithmException в случае ошибки
	 */
	public String sha(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte element : result) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За день"
	 *
	 * @return дата начала отсчета
	 */
	public LocalDateTime getStartDateForFilterDate() {
		return LocalDate.now().atStartOfDay();
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За день"
	 *
	 * @return дата конца отсчета
	 */
	public LocalDateTime getEndDateForFilterDate() {
		return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За неделю"
	 *
	 * @return дата начала отсчета
	 */
	public LocalDateTime getStartDateForFilterWeek() {
		TemporalField field = WeekFields.of(Locale.getDefault()).dayOfWeek();
		return LocalDateTime.of(LocalDate.now().with(field, 1), LocalTime.MIDNIGHT);
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За неделю"
	 *
	 * @return дата конца отсчета
	 */
	public LocalDateTime getEndDateForFilterWeek() {
		TemporalField field = WeekFields.of(Locale.getDefault()).dayOfWeek();
		return LocalDateTime.of(LocalDate.now().with(field, 1), LocalTime.MAX);
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За месяц"
	 *
	 * @return дата начала отсчета
	 */
	public LocalDateTime getStartDateForFilterMonth() {
		return LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIDNIGHT);
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За месяц"
	 *
	 * @return дата конца отсчета
	 */
	public LocalDateTime getEndDateForFilterMonth() {
		return LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MAX);
	}

	/**
	 * Конвертирует base64 строку в обычный текст
	 *
	 * @param aString base64 строка
	 * @return сконвертированный текст
	 */
	public String fromBase64(String aString) {
		String result = "";
		if (null != aString) {
			try {
				result = new String(Base64.getDecoder().decode(aString));
			} catch (IllegalArgumentException e) {
				// TODO Логирование
				e.printStackTrace();

				result = aString;
			}
		}
		return result;
	}

	/**
	 * Конвертирует массив байт строки в Base64
	 *
	 * @param data ковертируемая строка
	 * @return сконвертированная строка
	 */
	public String toBase64(String data) {
		String result = "";
		if (null != data) {
			result = Base64.getEncoder().encodeToString(data.getBytes());
		}
		return result;
	}

}
