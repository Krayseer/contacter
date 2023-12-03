package ru.anykeyers.factories;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.anykeyers.bots.impl.TelegramBot;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.bots.impl.ConsoleBot;
import ru.anykeyers.bots.BotType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Фабрика по созданию и запуску ботов
 */
public class BotFactory {

    /**
     * Запуск ботов
     */
    public void startBots() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Messages messages = new Messages();
        getBotTypesFromProperties().forEach(botType -> {
            switch (botType) {
                case CONSOLE:
                    executorService.submit(() -> {
                        new ConsoleBot();
                    });
                    break;
                case TELEGRAM_BOT:
                    executorService.submit(() -> {
                        try {
                            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                            telegramBotsApi.registerBot(new TelegramBot());
                        } catch (TelegramApiException e) {
                            String errorMessage = messages.getMessageByKey("bot.telegram.register-error");
                            throw new RuntimeException(errorMessage);
                        }
                    });
                    break;
            }
        });
    }

    /**
     * Получить из настроек типы ботов, которые нужно запустить
     */
    private List<BotType> getBotTypesFromProperties() {
        Messages messages = new Messages();
        ApplicationProperties applicationProperties = new ApplicationProperties();
        List<BotType> botTypes = new ArrayList<>();

        for (String type : applicationProperties.getSetting("app.type").split(",")) {
            try {
                BotType appType = BotType.valueOf(type);
                botTypes.add(appType);
            } catch (IllegalArgumentException ex) {
                String errorMessage = messages.getMessageByKey("bot.invalid-type", type);
                throw new IllegalArgumentException(errorMessage);
            }
        }

        return botTypes;
    }

}
