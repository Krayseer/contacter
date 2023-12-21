package ru.anykeyers.service;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;

import java.io.File;
import java.util.Set;

/**
 * Сервис для работы с контактами
 */
public interface ContactService {

    /**
     * Существует ли контакт у пользователя
     *
     * @param user        пользователь
     * @param contactName имя контакта
     * @return {@code true}, если существует, иначе {@code false}
     */
    boolean existsContact(User user, String contactName);

    /**
     * Добавляет контакт в список контактов пользователя
     *
     * @param user        пользователь
     * @param contactName имя контакта
     */
    void addContact(User user, String contactName);

    /**
     * Изменить состояние контакта
     * <ol>
     *     <li>Изменить имя</li>
     *     <li>Изменить телефонный номер</li>
     *     <li>Изменить возраст</li>
     *     <li>Изменить пол</li>
     *     <li>Изменить блокировку</li>
     * </ol>
     *
     * @param user     пользователь, которому нужно изменить контакт
     * @param newValue новое значение
     */
    void editContact(User user, StateInfo userStateInfo, String newValue);


    /**
     * Удаляет контакт из списка контактов пользователя
     *
     * @param user        пользователь
     * @param contactName имя контакта
     */
    void deleteContact(User user, String contactName);

    /**
     * Найти все контакты пользователя
     *
     * @param user пользователь
     * @return список контактов
     */
    Set<Contact> findAll(User user);

    /**
     * Найти все контакты по подстроке
     * <ol>
     *     <li>по имени</li>
     *     <li>по номеру телефона</li>
     * </ol>
     *
     * @param user          пользователь
     * @param userStateInfo информация о состоянии пользователя
     * @param substring     подстрока поиска
     * @return список контактов, удовлетворяющих критерию поиску
     */
    Set<Contact> searchByArgument(User user, StateInfo userStateInfo, String substring);

    /**
     * Отфильтровать контакты в зависимости от типа фильтрации
     *
     * @param user          пользователь
     * @param userStateInfo информация о состоянии пользователя
     * @param kind          тип фильтрации
     * @return список отфильтрованных контактов
     */
    Set<Contact> filterByKind(User user, StateInfo userStateInfo, String kind);

    /**
     * Отсортировать контакты в зависимости от типа сортировки
     *
     * @param user          пользователь
     * @param userStateInfo информация о состоянии пользователя
     * @param kind          тип сортировки
     * @return список отсортированных контактов
     */
    Set<Contact> sortByKind(User user, StateInfo userStateInfo, SortDirectionKind kind);

    /**
     * Импортировать контакты из файла
     *
     * @param user       пользователь
     * @param importFile файл с контактами для импорта
     */
    void importContacts(User user, File importFile);

    /**
     * Экспортировать контакты в файл
     *
     * @param user       пользователь
     * @param exportFile файл экспорта
     */
    void exportContacts(User user, File exportFile);

}
