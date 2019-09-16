package ru.devazz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
	 * @throws NoSuchAlgorithmException
	 */
	public String sha(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
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
	public Date getStartDateForFilterDate() {
		GregorianCalendar calendarStart = new GregorianCalendar();
		calendarStart.set(Calendar.AM_PM, Calendar.AM);
		calendarStart.set(Calendar.HOUR, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За день"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterDate() {
		GregorianCalendar calendarEnd = new GregorianCalendar();
		calendarEnd.set(Calendar.AM_PM, Calendar.PM);
		calendarEnd.set(Calendar.HOUR, 11);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За неделю"
	 *
	 * @return дата начала отсчета
	 */
	public Date getStartDateForFilterWeek() {
		GregorianCalendar calendarStart = new GregorianCalendar();
		calendarStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendarStart.set(Calendar.AM_PM, Calendar.AM);
		calendarStart.set(Calendar.HOUR, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За неделю"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterWeek() {
		GregorianCalendar calendarEnd = new GregorianCalendar();
		calendarEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendarEnd.set(Calendar.AM_PM, Calendar.PM);
		calendarEnd.set(Calendar.HOUR, 11);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За месяц"
	 *
	 * @return дата начала отсчета
	 */
	public Date getStartDateForFilterMonth() {
		GregorianCalendar commonCalendar = new GregorianCalendar();
		GregorianCalendar calendarStart = new GregorianCalendar(commonCalendar.get(Calendar.YEAR),
				commonCalendar.get(Calendar.MONTH), 1, 0, 0, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За месяц"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterMonth() {
		GregorianCalendar commonCalendar = new GregorianCalendar();
		GregorianCalendar calendarEnd = new GregorianCalendar(commonCalendar.get(Calendar.YEAR),
				commonCalendar.get(Calendar.MONTH),
				commonCalendar.getMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
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
			result = new String(Base64.getEncoder().encodeToString(data.getBytes()));
		}
		return result;
	}

}
