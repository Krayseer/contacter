package ru.anykeyers.factories;

import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.receivers.Receiver;
import ru.anykeyers.receivers.impl.ConsoleReceiver;

public class ReceiverFactory {

    private final AppType APP_TYPE;

    public ReceiverFactory() {
        Messages messages = new Messages();
        ApplicationProperties applicationProperties = new ApplicationProperties();
        String appSetting = applicationProperties.getSetting("app.type");
        try {
            APP_TYPE = AppType.valueOf(appSetting);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(messages.getMessageByKey("application.invalid-type", appSetting));
        }
    }

    public Receiver createReceiver() {
        switch (APP_TYPE) {
            case CONSOLE:
                return new ConsoleReceiver();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Тип приложения
     */
    private enum AppType {
        /**
         * Консольное приложение
         */
        CONSOLE,
        /**
         * Телеграмм бот
         */
        TELEGRAM_BOT,
        /**
         * Дискорд бот
         */
        DISCORD_BOT
    }

}
