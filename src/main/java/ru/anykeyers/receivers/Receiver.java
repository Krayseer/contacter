package ru.anykeyers.receivers;

/**
 * Интерфейс, описывающий обработчик команд
 */
public interface Receiver {

    /**
     * Считывает команду, введенную пользователем
     * @return Строка, представляющая введенную команду
     */
    String readCommand();

    /**
     * Отправить результат обработки
     * @param message результат обработки
     */
    void sendMessage(String message);

}
