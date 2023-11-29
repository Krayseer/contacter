package ru.anykeyers.factories;

import ru.anykeyers.bots.Bot;
import ru.anykeyers.bots.telegram.TelegramBot;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.bots.console.ConsoleBot;
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
        getBotTypesFromProperties().forEach(botType -> {
            switch (botType) {
                case CONSOLE:
                    executorService.submit(() -> {
                        Bot consoleBot = new ConsoleBot();
                        consoleBot.start();
                    });
                    break;
                case TELEGRAM_BOT:
                    executorService.submit(() -> {
                        Bot telegramBot = new TelegramBot();
                        telegramBot.start();
                    });
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });
    }

    /**
     * Получить типы ботов, которые нужно запустить
     */
    private List<BotType> getBotTypesFromProperties() {
        Messages messages = new Messages();
        ApplicationProperties applicationProperties = new ApplicationProperties();
        List<BotType> botTypes = new ArrayList<>();

        for (String type : applicationProperties.getSetting("app.type").split(",")) {
            try {
                BotType appType = BotType.valueOf(type);
                botTypes.add(appType);
            } catch (RuntimeException e) {
                String errorMessage = messages.getMessageByKey("application.invalid-type", type);
                throw new IllegalArgumentException(errorMessage);
            }
        }

        return botTypes;
    }

}
