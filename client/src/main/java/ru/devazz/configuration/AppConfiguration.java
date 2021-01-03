package ru.devazz.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.*;
import ru.devazz.utils.Utils;

import javax.jms.ConnectionFactory;

@EnableJms
@EnableConfigurationProperties({ActiveMQProperties.class})
@Configuration
public class AppConfiguration {

    @Bean
    public ActiveMQConnectionFactory jmsConnectionFactory(ActiveMQProperties activeMQProperties) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(activeMQProperties.getBrokerUrl());
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper responseMapper = new ObjectMapper();
        responseMapper.registerModule(new JavaTimeModule());
        return responseMapper;
    }

    @Bean
    @Primary
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                      DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    ProxyFactory taskProxyFactory() {
        return ProxyFactory.getInstance();
    }

    @Bean
    RmiProxyFactoryBean userService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(
                Utils.getInstance().getConnectionURL() + "/" + IUserService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(IUserService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean eventService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(
                Utils.getInstance().getConnectionURL() + "/" + IEventService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(IEventService.class);
        return rmiProxyFactory;
    }


    @Bean
    RmiProxyFactoryBean helpService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(
                Utils.getInstance().getConnectionURL() + "/" + IHelpService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(IHelpService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean reportService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(Utils.getInstance().getConnectionURL() + "/" +
                                      IReportService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(IReportService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean roleService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(
                Utils.getInstance().getConnectionURL() + "/" + IRoleService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(IRoleService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean searchService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(Utils.getInstance().getConnectionURL() + "/" +
                                      ISearchService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(ISearchService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean subElService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(Utils.getInstance().getConnectionURL() + "/" +
                                      ISubordinationElementService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(ISubordinationElementService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean taskHistoryService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(Utils.getInstance().getConnectionURL() + "/" +
                                      ITaskHistoryService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(ITaskHistoryService.class);
        return rmiProxyFactory;
    }

    @Bean
    RmiProxyFactoryBean taskService() {
        RmiProxyFactoryBean rmiProxyFactory = new RmiProxyFactoryBean();
        rmiProxyFactory.setServiceUrl(
                Utils.getInstance().getConnectionURL() + "/" + ITaskService.class.getSimpleName());
        rmiProxyFactory.setServiceInterface(ITaskService.class);
        return rmiProxyFactory;
    }

}
