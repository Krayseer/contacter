package ru.anykeyers.processor.state.domain.kinds;

/**
 * Типы критериев поиска контактов
 */
public enum ContactSearchKind {

    /**
     * Поиск контакта по имени
     */
    BY_NAME,

    /**
     * Поиск контакта по номеру телефона
     */
    BY_PHONE,

    /**
     * Получить все контакты группы по имени
     */
    GROUP_CONTACTS_BY_NAME

}
