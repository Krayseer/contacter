package ru.anykeyers.bots.telegram;

/**
 * Конфигурация пользователя для телеграмма
 */
public class TelegramConfig {

    /**
     * Идентификатор чата для бота
     */
    private Long chatId;

    public TelegramConfig(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

}
