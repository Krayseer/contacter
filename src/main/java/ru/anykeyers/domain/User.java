package ru.anykeyers.domain;

import ru.anykeyers.bots.BotType;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;

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
     * Тип бота пользователя
     */
    private BotType botType;

    /**
     * Состояние<br/>
     * По дефолту значение {@link State#NONE}
     */
    private State state = State.NONE;

    /**
     * Тип состояния<br/>
     * По дефолту значение {@link StateType#NONE}
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
     * Идентификатор чата пользователя
     */
    private Long chatId;


    public User(String username) {
        this.username = username;
    }

    public User(String username, BotType botType) {
        this.username = String.format("%s-%s", username, botType);
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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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
        return Objects.equals(username, user.getUsername())
                && botType == user.getBotType()
                && state == user.getState()
                && stateType == user.getStateType()
                && Objects.equals(contactNameToEdit, user.getContactNameToEdit())
                && Objects.equals(groupNameToEdit, user.getGroupNameToEdit())
                && Objects.equals(chatId, user.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, botType, state, stateType, contactNameToEdit, groupNameToEdit, chatId);
    }
}
