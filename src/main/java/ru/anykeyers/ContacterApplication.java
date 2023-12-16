package ru.anykeyers;

import ru.anykeyers.factory.BotFactory;

/**
 * Класс, позволяющий запустить приложение
 */
public class ContacterApplication {

    /**
     * Запуск приложения
     */
    public void start() {
        BotFactory botFactory = new BotFactory();
        botFactory.startBots();
    }

}
