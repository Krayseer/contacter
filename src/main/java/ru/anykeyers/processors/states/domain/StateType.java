package ru.anykeyers.processors.states.domain;

/**
 * Тип состояния
 */
public enum StateType {

    /**
     * Нет состояния
     */
    NONE,
    /**
     * Контакты
     */
    CONTACT,
    /**
     * Группы
     */
    GROUP,
    /**
     * Обработка функции (поиск, фильтр, сортировка)
     */
    FUNCTION

}
