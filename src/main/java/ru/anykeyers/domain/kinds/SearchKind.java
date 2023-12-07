package ru.anykeyers.domain.kinds;

/**
 * Аргументы для поиска контактов
 */
public final class SearchKind {

    /**
     * Поиск всех контактов
     */
    public static final String SEARCH_ALL_CONTACTS = "1";

    /**
     * Поиск контакта по имени
     */
    public static final String SEARCH_BY_NAME = "2";

    /**
     * Поиск контакта по номеру телефона
     */
    public static final String SEARCH_BY_PHONE = "3";

    /**
     * Получить все группы
     */
    public static final String SEARCH_ALL_GROUPS = "4";

    /**
     * Получить все контакты группы
     */
    public static final String SEARCH_GROUP_CONTACTS = "5";

}
