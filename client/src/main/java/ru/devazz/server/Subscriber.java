package ru.devazz.server;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

/**
 * A simple message consumer which consumes the message from ActiveMQ Broker
 *
 * @author Mary.Zheng
 *
 */
public class Subscriber {

	private String activeMqBrokerUri;
	private String username;
	private String password;

	Subscriber(String activeMqBrokerUri, String username, String password) {
		super();
		this.activeMqBrokerUri = activeMqBrokerUri;
		this.username = username;
		this.password = password;
	}

	void run(String clientName, String queueName, MessageListener listener) throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, activeMqBrokerUri);
		Connection connection = factory.createConnection();
		connection.setClientID(getClientId(clientName, queueName));
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = session.createQueue(queueName);

		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(listener);

		//TODO Логгер
		System.out.println(String.format("QueueMessageConsumer Waiting for messages at queue='%s' broker='%s'",
										 queueName, this.activeMqBrokerUri));
	}

	private String getClientId(String clientName, String queueName) {
		return clientName + queueName + "_" + activeMqBrokerUri.replace("tcp://127.0.0.1:61616","");
	}
}
