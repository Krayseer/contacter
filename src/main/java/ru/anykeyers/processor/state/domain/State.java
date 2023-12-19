package ru.anykeyers.processor.state.domain;

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
     * Изменение возраста контакта
     */
    EDIT_CONTACT_AGE,
    /**
     * Изменение пола контакта
     */
    EDIT_CONTACT_GENDER,
    /**
     * Изменение блокировки контакта
     */
    EDIT_CONTACT_BLOCK,
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
     * Получение информации
     */
    GET_KIND,
    /**
     * Получение контактов группы
     */
    GET_GROUP_CONTACTS,

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
     * Поиск по контактам группы
     */
    SEARCH_GROUP_CONTACTS,
    /**
     * Поиск по контактам группы по имени контакта
     */
    SEARCH_GROUP_CONTACTS_BY_NAME,

    /**
     * Фильтрация
     */
    FILTER_KIND,
    /**
     * Фильтрация по возрасту
     */
    FILTER_AGE,
    /**
     * Фильтрация по возрасту с обработкой принимаемого значения(действия фильтрации)
     */
    FILTER_AGE_KIND,
    /**
     * Фильтрация по полу
     */
    FILTER_GENDER,
    /**
     * Фильтрация по блокировке
     */
    FILTER_BLOCK,

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
    SORT_AGE,

    /**
     * Импорт
     */
    IMPORT,
    /**
     * Экспорт
     */
    EXPORT,

}
