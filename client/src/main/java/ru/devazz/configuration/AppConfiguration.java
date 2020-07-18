package ru.devazz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.*;
import ru.devazz.utils.Utils;

@Configuration
public class AppConfiguration {

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
