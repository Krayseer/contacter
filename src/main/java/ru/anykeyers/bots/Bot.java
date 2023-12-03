package ru.anykeyers.bots;

/**
 * Интерфейс бота
 */
public interface Bot {

    /**
     * Отправить результат обработки в чат
     * @param chatId идентификатор чата
     * @param message результат обработки
     */
    void sendMessage(Long chatId, String message);

}
