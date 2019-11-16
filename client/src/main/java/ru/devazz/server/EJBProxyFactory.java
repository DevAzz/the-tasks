package ru.devazz.server;

import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.bean.events.EventServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.help.HelpServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.history.TaskHistoryServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.report.ReportServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.role.RoleServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.search.SearchServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Фабрика EJBProxy объектов
 */
public class EJBProxyFactory {

	/** Экземпляр класса */
	private static EJBProxyFactory instance;

	/** Карта соответствия типов и имен сервисов */
	private Map<Class<? extends ICommonService>, String> mapServiceNameType = new HashMap<>();

	/** Карта соответствия типов и имен сервисов */
	private Map<Class<? extends ICommonService>, Object> mapServiceType = new HashMap<>();

	/**
	 * Конструктор
	 */
	private EJBProxyFactory() {
		String deployName = "ejb:/hqtasks.server-0.0.1-SNAPSHOT/";
		mapServiceNameType.put(UserServiceRemote.class, deployName
				+ "UserServiceBean!ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote");
		mapServiceNameType.put(SubordinatioElementServiceRemote.class, deployName
				+ "SubordinationElementServiceBean!ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote");
		mapServiceNameType.put(TaskServiceRemote.class, deployName
				+ "TaskServiceBean!ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote");
		mapServiceNameType.put(RoleServiceRemote.class, deployName
				+ "RoleServiceBean!ru.sciencesquad.hqtasks.server.bean.role.RoleServiceRemote");
		mapServiceNameType.put(EventServiceRemote.class, deployName
				+ "EventServiceBean!ru.sciencesquad.hqtasks.server.bean.events.EventServiceRemote");
		mapServiceNameType.put(ReportServiceRemote.class, deployName
				+ "ReportServiceBean!ru.sciencesquad.hqtasks.server.bean.report.ReportServiceRemote");
		mapServiceNameType.put(SearchServiceRemote.class, deployName
				+ "SearchServiceBean!ru.sciencesquad.hqtasks.server.bean.search.SearchServiceRemote");
		mapServiceNameType.put(HelpServiceRemote.class, deployName
				+ "HelpServiceBean!ru.sciencesquad.hqtasks.server.bean.help.HelpServiceRemote");
		mapServiceNameType.put(TaskHistoryServiceRemote.class, deployName
				+ "TaskHistoryServiceBean!ru.sciencesquad.hqtasks.server.bean.history.TaskHistoryServiceRemote");
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
	 * @throws NamingException в случае ошибки
	 */
	@SuppressWarnings("unchecked")
	public <T extends ICommonService> T getService(Class<T> aType) throws NamingException {
		T service = null;
		if (null != aType) {
			if (null == mapServiceType.get(aType)) {
				String beanName = mapServiceNameType.get(aType);
				final Hashtable<String, String> jndiProperties = new Hashtable<>();
				jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
						"org.jboss.naming.remote.client.InitialContextFactory");
				jndiProperties.put(Context.PROVIDER_URL, Utils.getInstance().getConnectionURL());
				jndiProperties.put(Context.SECURITY_PRINCIPAL,
						Utils.getInstance().getServerConnectionUser());
				jndiProperties.put(Context.SECURITY_CREDENTIALS,
						Utils.getInstance().getServerConnectionPassword());

				Context context = new InitialContext(jndiProperties);
				service = (T) context.lookup(beanName);
				mapServiceType.put(aType, service);
				context.close();
			} else {
				service = (T) mapServiceType.get(aType);
			}
		}
		return service;
	}

	/**
	 * Добавляет слушателя JMS сообщений
	 *
	 * @param aListener слушатель JMS сообщений
	 */
	public void addMessageListener(MessageListener aListener) {
		try {
			Subscriber subscriber = new Subscriber();
			subscriber.createSubscriber();
			subscriber.addMessageListener(aListener);
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}

	}

}
