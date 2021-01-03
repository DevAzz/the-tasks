package ru.devazz.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.devazz.server.api.*;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.QueueNameEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Фабрика Proxy объектов
 */
@Component
public class ProxyFactory {

	/** Экземпляр класса */
	private static ProxyFactory instance;

	/** Карта соответствия типов и имен сервисов */
	private final Map<Class<? extends ICommonService>, Object> mapServiceType = new HashMap<>();

	private final Map<String, IMessageListener> subscriberMap = new ConcurrentHashMap<>();

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
		instance = this;
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


	@JmsListener(destination = QueueNameEnum.EVENT_QUEUE)
	public void receiveEvent(ObjectEvent event) {
		subscriberMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains(QueueNameEnum.EVENT_QUEUE))
				.forEach(entry -> entry.getValue().onEvent(event));
	}

	@JmsListener(destination = QueueNameEnum.SUB_EL_QUEUE)
	public void receiveSubElEvent(ObjectEvent event) {
		subscriberMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains(QueueNameEnum.SUB_EL_QUEUE))
				.forEach(entry -> entry.getValue().onEvent(event));
	}


	@JmsListener(destination = QueueNameEnum.TASKS_HISTORY_QUEUE)
	public void receiveTaskHistoryEvent(ObjectEvent event) {
		subscriberMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains(QueueNameEnum.TASKS_HISTORY_QUEUE))
				.forEach(entry -> entry.getValue().onEvent(event));
	}

	@JmsListener(destination = QueueNameEnum.TASKS_QUEUE)
	public void receiveTaskEvent(ObjectEvent event) {
		subscriberMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains(QueueNameEnum.TASKS_QUEUE))
				.forEach(entry -> entry.getValue().onEvent(event));
	}

	@JmsListener(destination = QueueNameEnum.USERS_QUEUE)
	public void receiveUserEvent(ObjectEvent event) {
		subscriberMap.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains(QueueNameEnum.USERS_QUEUE))
				.forEach(entry -> entry.getValue().onEvent(event));
	}

	/**
	 * Добавляет слушателя JMS сообщений
	 *
	 * @param aListener слушатель JMS сообщений
	 */
	public void addMessageListener(
			String clientName,
			IMessageListener aListener
	) {
		subscriberMap.put(clientName, aListener);
	}

}
