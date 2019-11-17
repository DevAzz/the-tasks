package ru.devazz.server;

import org.springframework.beans.factory.annotation.Autowired;
import ru.devazz.server.api.*;

import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика EJBProxy объектов
 */
public class EJBProxyFactory {

	/** Экземпляр класса */
	private static EJBProxyFactory instance;

	/** Карта соответствия типов и имен сервисов */
	private Map<Class<? extends ICommonService>, Object> mapServiceType = new HashMap<>();

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
	private EJBProxyFactory() {
		mapServiceType.put(IUserService.class, userService);
		mapServiceType.put(IEventService.class, eventService);
		mapServiceType.put(IHelpService.class, helpService);
		mapServiceType.put(IReportService.class, reportService);
		mapServiceType.put(IRoleService.class, roleService);
		mapServiceType.put(ISearchService.class, searchService);
		mapServiceType.put(ISubordinationElementService.class, subordinationElementService);
		mapServiceType.put(ITaskHistoryService.class, taskHistoryService);
		mapServiceType.put(ITaskService.class, taskService);
	}

	/**
	 * Возвращает единственный экземпляр фабрики
	 *
	 * @return единственный экземпляр фабрики
	 */
	public static EJBProxyFactory getInstance() {
		if (null == instance) {
			instance = new EJBProxyFactory();
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
		return (T) mapServiceType.get(aType);
	}

	/**
	 * Добавляет слушателя JMS сообщений
	 *
	 * @param aListener слушатель JMS сообщений
	 */
	public void addMessageListener(MessageListener aListener) {
		try {
			Subscriber subscriber =
					new Subscriber("tcp://127.0.0.1:61616", "test_queue", "admin", "password");
			subscriber.run(aListener);
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

}
