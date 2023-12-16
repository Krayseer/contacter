package ru.anykeyers.processor.state.domain.kinds;

/**
 * Типы критериев поиска контактов
 */
public final class ContactSearchKind {

    /**
     * Поиск контакта по имени
     */
    public static final String BY_NAME = "1";

    /**
     * Поиск контакта по номеру телефона
     */
    public static final String BY_PHONE = "2";

    /**
     * Получить все контакты группы по имени
     */
    public static final String GROUP_CONTACTS_BY_NAME = "3";

}
