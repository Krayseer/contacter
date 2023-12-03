package ru.anykeyers;

import ru.anykeyers.factories.BotFactory;

/**
 * Класс, позволяющий запустить приложение
 */
public class ContacterApplication {

    /**
     * Запуск приложения
     */
    public void start() {
        BotFactory botsFactory = new BotFactory();
        botsFactory.startBots();
    }

}
