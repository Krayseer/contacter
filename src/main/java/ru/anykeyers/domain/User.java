package ru.anykeyers.domain;

import ru.anykeyers.bots.telegram.TelegramConfig;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;

import java.util.Objects;

/**
 * Класс пользователя
 */
public class User {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Из какого последнего приложения была обработка пользователя
     */
    private BotType botType = BotType.CONSOLE;

    /**
     * Состояние
     */
    private State state = State.NONE;

    /**
     * Тип состояния
     */
    private StateType stateType = StateType.NONE;

    /**
     * Имя контакта, выбранное для редактирования
     */
    private String contactNameToEdit;

    /**
     * Название группы, выбранное для редактирования
     */
    private String groupNameToEdit;

    /**
     * Конфигурация телеграмма пользователя
     */
    private TelegramConfig telegramConfig;


    public User(String username) {
        this.username = username;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public String getContactNameToEdit() {
        return contactNameToEdit;
    }

    public void setContactNameToEdit(String contactNameToEdit) {
        this.contactNameToEdit = contactNameToEdit;
    }

    public String getGroupNameToEdit() {
        return groupNameToEdit;
    }

    public void setGroupNameToEdit(String groupNameToEdit) {
        this.groupNameToEdit = groupNameToEdit;
    }

    public TelegramConfig getTelegramConfig() {
        return telegramConfig;
    }

    public void setTelegramConfig(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

    /**
     * Получить идентификатор чата, куда нужно отправить сообщение
     */
    public Long getChatIdByAppType() {
        return switch (botType) {
            case CONSOLE -> 0L;
            case TELEGRAM_BOT -> telegramConfig.getChatId();
        };
    }

    /**
     * Очистить состояние пользователя
     */
    public void clearState() {
        state = State.NONE;
        stateType = StateType.NONE;
        contactNameToEdit = null;
        groupNameToEdit = null;
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
