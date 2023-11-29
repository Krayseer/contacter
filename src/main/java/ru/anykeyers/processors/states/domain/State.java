package ru.anykeyers.processors.states.domain;

/**
 * Состояние пользователя
 */
public enum State {

    /**
     * Пустое состояние
     */
    NONE,

    /**
     * Добавление контакта
     */
    ADD_CONTACT,
    /**
     * Изменение контакта
     */
    EDIT_CONTACT,
    /**
     * Изменение поля контакта
     */
    EDIT_CONTACT_FIELD,
    /**
     * Изменение имени контакта
     */
    EDIT_CONTACT_NAME,
    /**
     * Изменение номера телефона контакта
     */
    EDIT_CONTACT_PHONE,
    /**
     * Удаление контакта
     */
    DELETE_CONTACT,

    /**
     * Получить контакты из группы
     */
    GET_GROUP_CONTACTS,
    /**
     * Добавление группы
     */
    ADD_GROUP,
    /**
     * Изменение группы
     */
    EDIT_GROUP,
    /**
     * Изменение поля группы
     */
    EDIT_GROUP_FIELD,
    /**
     * Изменение названия группы
     */
    EDIT_GROUP_NAME,
    /**
     * Добавление контакта в группу
     */
    EDIT_GROUP_ADD_CONTACT,
    /**
     * Удаление контакта из группы
     */
    EDIT_GROUP_DELETE_CONTACT,
    /**
     * Удаление группы
     */
    DELETE_GROUP,

}
