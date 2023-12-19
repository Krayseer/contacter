package ru.anykeyers.processor.state.domain;

/**
 * Тип состояния
 */
public enum StateType {

    /**
     * Нет состояния
     */
    NONE,
    /**
     * Контакт
     */
    CONTACT,
    /**
     * Группа
     */
    GROUP,
    /**
     * Операция (получение/поиск/фильтр/сортировка)
     */
    OPERATION,
    /**
     * Импорт/экспорт
     */
    IMPORT_EXPORT,

}
