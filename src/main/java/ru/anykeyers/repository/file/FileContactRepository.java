package ru.anykeyers.repository.file;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.repository.file.mapper.FileContactMapper;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.file.service.FileService;
import ru.anykeyers.repository.file.service.impl.FileServiceImpl;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Реализация файловой базы данных для контактов
 */
public class FileContactRepository implements ContactRepository {

    private final Map<String, Set<Contact>> contactsByUsername;

    private final File dbFile;

    private final FileService<Contact> fileService;

    public FileContactRepository(String contactFilePath) {
        dbFile = new File(contactFilePath);
        this.fileService = new FileServiceImpl<>(new FileContactMapper());
        Collection<Contact> contacts = fileService.initDataFromFile(dbFile);
        contactsByUsername = contacts.stream()
                .collect(Collectors.groupingBy(Contact::getUsername, Collectors.toSet()));
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return findByUsername(username).stream()
                .map(Contact::getName)
                .anyMatch(contactName -> contactName.equals(name));
    }

    @Override
    public Set<Contact> findByUsername(String username) {
        return contactsByUsername.getOrDefault(username, Collections.emptySet());
    }

    @Override
    public Optional<Contact> getByUsernameAndId(String username, String contactId) {
        return getContactByCriteria(username, contact -> Objects.equals(contact.getId(), contactId));
    }

    @Override
    public Optional<Contact> getByUsernameAndName(String username, String name) {
        return getContactByCriteria(username, contact -> Objects.equals(contact.getName(), name));
    }

    @Override
    public void saveOrUpdate(Contact contact) {
        contactsByUsername.computeIfAbsent(contact.getUsername(), k -> new HashSet<>()).add(contact);
        fileService.saveOrUpdateFile(dbFile, contactsByUsername);
    }

    @Override
    public void delete(Contact contact) {
        Set<Contact> contacts = findByUsername(contact.getUsername());
        contacts.remove(contact);
        fileService.saveOrUpdateFile(dbFile, contactsByUsername);
    }

    /**
     * Получить контакт по какому-то критерию
     *
     * @param username имя пользователя
     * @param criteria критерий
     */
    private Optional<Contact> getContactByCriteria(String username, Predicate<Contact> criteria) {
        return Optional.ofNullable(contactsByUsername.get(username))
                .flatMap(contacts -> contacts.stream()
                        .filter(criteria)
                        .findFirst());
    }

}
