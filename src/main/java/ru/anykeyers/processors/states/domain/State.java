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
     * Изменить возраст контакта
     */
    EDIT_CONTACT_AGE,
    /**
     * Изменить пол контакта
     */
    EDIT_CONTACT_GENDER,
    /**
     * Заблокировать контакт
     */
    EDIT_CONTACT_BLOCK,
    /**
     * Разблокировать контакт
     */
    EDIT_CONTACT_UNBLOCK,
    /**
     * Удаление контакта
     */
    DELETE_CONTACT,

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

    /**
     * Поиск
     */
    SEARCH_KIND,
    /**
     * Поиск по имени
     */
    SEARCH_NAME,
    /**
     * Поиск по номеру
     */
    SEARCH_PHONE,
    /**
     * Поиск по контактам групп
     */
    SEARCH_GROUP_CONTACTS,

    /**
     * Фильтрация
     */
    FILTER_KIND,
    /**
     * Фильтрация по возрасту
     */
    FILTER_AGE,
    /**
     * Фильтрация по возрасту с обработкой принимаемого значения
     */
    FILTER_AGE_KIND,
    /**
     * Фильтрация по полу
     */
    FILTER_GENDER,
    /**
     * Фильтрация по полу с обработкой принимаемого значения
     */
    FILTER_GENDER_KIND,
    /**
     * Фильтрация по блокировке
     */
    FILTER_BLOCK,
    /**
     * Фильтрация по блокировке с обработкой принимаемого значения
     */
    FILTER_BLOCK_KIND,

    /**
     * Сортировка
     */
    SORT_KIND,
    /**
     * Сортировка по имени
     */
    SORT_NAME,
    /**
     * Сортировка по возрасту
     */
    SORT_AGE

}
