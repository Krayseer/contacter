package ru.anykeyers.processor.state.domain.kinds;

/**
 * Виды действий для изменения группы
 */
public enum GroupEditActionKind {

    /**
     * Изменение названия группы
     */
    NAME,

    /**
     * Добавление контакта в группу
     */
    ADD_CONTACT,

    /**
     * Удаление контакта из группы
     */
    DELETE_CONTACT

}
