package ru.devazz.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.devazz.server.api.*;

import javax.jms.MessageListener;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика Proxy объектов
 */
@Component
public class ProxyFactory {

	/** Экземпляр класса */
	private static ProxyFactory instance;

	/** Карта соответствия типов и имен сервисов */
	private Map<Class<? extends ICommonService>, Object> mapServiceType = new HashMap<>();

	private Map<String, Subscriber> subscriberMap = new HashMap<>();

	@Autowired
	private IUserService userService;

	@Autowired
	private IEventService eventService;

	@Autowired
	private IHelpService helpService;

	@Autowired
	private IReportService reportService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ISearchService searchService;

	@Autowired
	private ISubordinationElementService subordinationElementService;

	@Autowired
	private ITaskHistoryService taskHistoryService;

	@Autowired
	private ITaskService taskService;

	/**
	 * Конструктор
	 */
	private ProxyFactory() {
	}

	/**
	 * Возвращает единственный экземпляр фабрики
	 *
	 * @return единственный экземпляр фабрики
	 */
	public static ProxyFactory getInstance() {
		if (null == instance) {
			instance = new ProxyFactory();
		}
		return instance;
	}

	/**
	 * Возвращает сервис
	 *
	 * @param aType тип сервиса
	 * @return сервис
	 */
	@SuppressWarnings("unchecked")
	public <T extends ICommonService> T getService(Class<T> aType) {
		T service = (T) mapServiceType.get(aType);
		if (null == service) {
			try {
				for (Field field : getClass().getDeclaredFields()) {
					if (aType.getSimpleName().equals(field.getType().getSimpleName())) {
						service = (T) field.get(this);
						if (null != service) {
							mapServiceType.put(aType, service);
							break;
						}
					}
				}
			} catch (Exception e) {
				//TODO логер
				e.printStackTrace();
			}
		}
		return service;
	}

	public void deleteMessageListener(String clientName) {
		subscriberMap.remove(clientName);
	}

	/**
	 * Добавляет слушателя JMS сообщений
	 *
	 * @param aListener слушатель JMS сообщений
	 */
	public void addMessageListener(String clientName, String queueName, MessageListener aListener) {
		try {
			Subscriber subscriber =
					new Subscriber("tcp://127.0.0.1:61616", "admin", "password");
			subscriber.run(clientName, queueName, aListener);
			subscriberMap.put(clientName, subscriber);
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

}
