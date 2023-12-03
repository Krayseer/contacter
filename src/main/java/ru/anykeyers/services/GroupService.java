package ru.anykeyers.services;

import ru.anykeyers.domain.User;

/**
 * Интерфейс сервиса для работы с группами
 */
public interface GroupService {

    /**
     * Существует ли группа у пользователя
     * @param user пользователь
     * @param groupName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsGroup(User user, String groupName);

    /**
     * Добавляет группу в список групп пользователя
     * @param user пользователь
     * @param groupName название группы
     * @return результат добавления группы
     */
    String addGroup(User user, String groupName);

    /**
     * Изменить состояние группы пользователя
     * <ol>
     *     <li>Изменить название группы</li>
     *     <li>Добавить контакт в группу</li>
     *     <li>Удалить контакт из группы</li>
     * </ol>
     * @param user пользователь
     * @param newValue новое значение
     * @return результат изменения состояния группы
     */
    String editGroup(User user, String newValue);

    /**
     * Удалить группу пользователя
     * @param user пользователь
     * @param groupName название группы
     * @return результат удаления группы
     */
    String deleteGroup(User user, String groupName);

}
