package ru.anykeyers.bots;

/**
 * Интерфейс, описывающий поведение ботов
 */
public interface Bot {

    /**
     * Отправить результат обработки в чат
     * @param chatId идентификатор чата
     * @param message результат обработки
     */
    void sendMessage(Long chatId, String message);

    /**
     * Обработать сообщение
     * @param username имя пользователя, которому нужно обработать сообщение
     * @param message сообщение
     */
    void receiveMessage(String username, String message);

    /**
     * Запуск бота
     */
    void start();
}
