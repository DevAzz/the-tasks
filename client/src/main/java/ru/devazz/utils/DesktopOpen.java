package ru.devazz.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Заупуск внешних файлов программой по умолчанию в системе
 */
public class DesktopOpen {

	/**
	 * Конструктор
	 *
	 * @param file путь до файла
	 */
	public DesktopOpen(String file) {
		super();
		openSystemSpecific(file);
	}

	/**
	 * Подбор команды запуска со спицификой ОС
	 *
	 * @param Полный путь к файлу
	 * @return успешность
	 */
	private boolean openSystemSpecific(String file) {

		String os = getOs();

		if (os.indexOf("linux") != -1) {
			if (runCommand("xdg-open", "%s", file)) {
				return true;
			}
			if (runCommand("kde-open", "%s", file)) {
				return true;
			}
			if (runCommand("gnome-open", "%s", file)) {
				return true;
			}
		}
		if (os.indexOf("win") != -1) {
			if (runCommand("explorer", "%s", file)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Выполнение команды
	 *
	 * @param команда
	 * @param аргументы
	 * @param путь к файлу
	 * @return успешность
	 */
	private boolean runCommand(String command, String args, String file) {

		String[] parts = prepareCommand(command, args, file);

		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null) {
				return false;
			}

			try {
				int retval = p.exitValue();
				if (retval == 0) {
					return false;
				} else {
					return false;
				}
			} catch (IllegalThreadStateException itse) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Подготовка команды для выполнения
	 *
	 * @param команда
	 * @param аргументы
	 * @param путь к файлу
	 * @return подготовленная комманда
	 */
	private String[] prepareCommand(String command, String args, String file) {

		List<String> parts = new ArrayList<>();
		parts.add(command);

		if (args != null) {
			for (String s : args.split(" ")) {
				s = String.format(s, file);
				parts.add(s.trim());
			}
		}

		return parts.toArray(new String[parts.size()]);
	}

	/**
	 * Получние типа операционной системы
	 *
	 * @return имя ос
	 */
	public String getOs() {
		String s = System.getProperty("os.name").toLowerCase();
		return s;
	}
}
