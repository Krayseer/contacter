package ru.anykeyers.processor.state.domain.kinds;

/**
 * Виды действий для изменения группы
 */
public final class GroupEditActionKind {

    /**
     * Изменение названия группы
     */
    public static final String NAME = "1";

    /**
     * Добавление контакта в группу
     */
    public static final String ADD_CONTACT = "2";

    /**
     * Удаление контакта из группы
     */
    public static final String DELETE_CONTACT = "3";

}
