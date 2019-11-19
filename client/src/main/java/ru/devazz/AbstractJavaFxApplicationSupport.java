package ru.devazz;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.devazz.utils.Utils;

public abstract class AbstractJavaFxApplicationSupport extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(getClass());
        builder.headless(false);
        context = builder.run(savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    protected static void launchApp(Class<? extends AbstractJavaFxApplicationSupport> appClass, String[] args)
            throws Exception {
        if (4 <= args.length) {
            AbstractJavaFxApplicationSupport.savedArgs = args;

            StringBuilder builder = new StringBuilder(args[0]);
            String subscriberIp = builder.substring(builder.lastIndexOf("=") + 1);
            Utils.getInstance().setConnectionURL(subscriberIp);

            builder = new StringBuilder(args[1]);
            String user = builder.substring(builder.lastIndexOf("=") + 1);
            Utils.getInstance().setServerConnectionUser(user);

            builder = new StringBuilder(args[2]);
            String password = builder.substring(builder.lastIndexOf("=") + 1);
            Utils.getInstance().setServerConnectionPassword(password);

            builder = new StringBuilder(args[3]);
            String registration = builder.substring(builder.lastIndexOf("=") + 1);
            if (Boolean.parseBoolean(registration)) {
                RegistryApp.main(null);
            } else {
                Application.launch(appClass, args);
            }
        } else {
            throw new Exception("Некорректные параметры запуска приложения");
        }
    }
}
