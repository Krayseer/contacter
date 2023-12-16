package ru.anykeyers.domain.entity;

import ru.anykeyers.bot.BotType;

import java.util.Objects;

/**
 * Пользователь
 */
public class User {

    /**
     * Имя пользователя
     */
    private final String username;

    /**
     * Тип бота
     */
    private BotType botType;

    /**
     * Идентификатор чата
     */
    private Long chatId;

    public User(String username) {
        this.username = username;
    }

    public User(String username, BotType botType) {
        this(String.format("%s-%s", username, botType));
        this.botType = botType;
    }

    public String getUsername() {
        return username;
    }

    public BotType getBotType() {
        return botType;
    }

    public void setBotType(BotType botType) {
        this.botType = botType;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
