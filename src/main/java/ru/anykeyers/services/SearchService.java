package ru.anykeyers.services;

import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.Group;
import ru.anykeyers.domain.User;

import java.util.Set;

/**
 * Интерфейс сервиса для поиска контактов и групп
 */
public interface SearchService {

    /**
     * Найти все контакты пользователя
     * @param user пользователь
     * @return список контактов
     */
    String findAllContacts(User user);

    /**
     * Найти все группы пользователя
     * @param user пользователь
     * @return список групп
     */
    String findAllGroups(User user);

    /**
     * Получить информацию, в зависимости от состояния пользователя и выбранного им действия
     * @param user пользователь
     * @param value значение, по которому нужно произвести поиск
     * @return данные об объекте, информацию которого пользователь хотел получить
     */
    String findInfoByUserStateAndKind(User user, String value);

}
