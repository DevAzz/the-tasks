package ru.devazz.server;

import ru.siencesquad.hqtasks.ui.utils.Utils;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Подписчик на системные события
 */
public class Subscriber {

	/** Наименование фабрики коннектов */
	private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";

	/** Наименование учереди по умолчанию */
	private static final String DEFAULT_DESTINATION = "jms/topic/hqtasksTopic";

	/** Пользователь по умолчанию */
	private static final String DEFAULT_USERNAME = "userApp";

	/** Пароль по умолчанию */
	private static final String DEFAULT_PASSWORD = "password";

	/** Наименование фабрики контекстов */
	private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";

	/** URL */
	private static final String PROVIDER_URL = Utils.getInstance().getConnectionURL();

	/** Консъюмер JMS сообщений */
	private JMSConsumer consumer;

	/**
	 * Конструктор
	 */
	public Subscriber() {
		super();
	}

	/**
	 * Создание подписчика и подключение к серверу
	 *
	 * @throws Exception в случае ошибки
	 */
	public void createSubscriber() throws Exception {
		Context namingContext = null;
		JMSContext context = null;

		try {
			String userName = System.getProperty("user", DEFAULT_USERNAME);
			String password = System.getProperty("password", DEFAULT_PASSWORD);

			// Set up the namingContext for the JNDI lookup
			final Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
			env.put(Context.SECURITY_PRINCIPAL, userName);
			env.put(Context.SECURITY_CREDENTIALS, password);
			namingContext = new InitialContext(env);

			// Perform the JNDI lookups
			String connectionFactoryString = System.getProperty("connection.factory",
					DEFAULT_CONNECTION_FACTORY);
			ConnectionFactory connectionFactory = (ConnectionFactory) namingContext
					.lookup(connectionFactoryString);

			String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);

			Destination destination = (Destination) namingContext.lookup(destinationString);

			// Create the JMS context
			context = connectionFactory.createContext(userName, password);

			// Create the JMS consumer
			consumer = context.createConsumer(destination);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (namingContext != null) {
				try {
					namingContext.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Добавляет слушателя JMS сообщений
	 *
	 * @param aListener слушатель JMS сообщений
	 */
	public void addMessageListener(MessageListener aListener) {
		if (null != consumer) {
			consumer.setMessageListener(aListener);
		}
	}

}
