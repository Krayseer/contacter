package ru.anykeyers.factory;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.anykeyers.bot.impl.TelegramBot;
import ru.anykeyers.bot.impl.ConsoleBot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.common.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Фабрика по созданию и запуску ботов
 */
public class BotFactory {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private final Messages messages = Messages.getInstance();

    /**
     * Запуск ботов
     */
    public void startBots() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
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
                            TelegramBot bot = new TelegramBot();
                            telegramBotsApi.registerBot(bot);
                        } catch (TelegramApiException e) {
                            String errorMessage = messages.getMessageByKey("bot.telegram.exception.register-error");
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
        List<BotType> botTypes = new ArrayList<>();
        String[] types = applicationProperties.getSettingByKey("app.type").split(",");
        for (String type : types) {
            try {
                BotType appType = BotType.valueOf(type);
                botTypes.add(appType);
            } catch (IllegalArgumentException ex) {
                String errorMessage = messages.getMessageByKey("bot.telegram.exception.invalid-type", type);
                throw new IllegalArgumentException(errorMessage);
            }
        }
        return botTypes;
    }

}
