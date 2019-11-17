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
	private String destinationName;

	public Subscriber(String activeMqBrokerUri, String destinationName, String username,
								String password) {
		super();
		this.activeMqBrokerUri = activeMqBrokerUri;
		this.username = username;
		this.password = password;
		this.destinationName = destinationName;
	}

	public void run(MessageListener listener) throws JMSException {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, activeMqBrokerUri);
		Connection connection = factory.createConnection();
		connection.setClientID(getClientId());
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = session.createQueue(destinationName);

		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(listener);

		System.out.println(String.format("QueueMessageConsumer Waiting for messages at queue='%s' broker='%s'",
										 destinationName, this.activeMqBrokerUri));
	}

	private String getClientId() {
		return "TaskClient_" + destinationName + "_" + activeMqBrokerUri.replace("tcp://localhost:",
																			  "");
	}
}
