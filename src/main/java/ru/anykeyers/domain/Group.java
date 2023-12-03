package ru.anykeyers.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Группа
 */
public class Group {

    /**
     * Идентификатор группы
     */
    private String id;

    /**
     * Имя пользователя, которому принадлежит группа
     */
    private String username;

    /**
     * Название группы
     */
    private String name;

    /**
     * Список контактов, добавленных в группу
     */
    private Set<Contact> contacts;

    public Group(String username) {
        this.username = username;
    }

    public Group(String username, String name) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    /**
     * Добавить контакт у группу
     * @param contact добавляемый контакт
     */
    public void addContactInGroup(Contact contact) {
        if (contacts == null) {
            contacts = new HashSet<>();
        }
        contacts.add(contact);
    }

    /**
     * Удалить контакт из группы
     * @param contact удаляемый контакт
     * @return {@code true}, если контакт удалось удалить, иначе {@code false}
     */
    public boolean deleteContactFromGroup(Contact contact) {
        if (contacts == null) {
            return false;
        }
        return contacts.remove(contact);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return Objects.equals(id, group.getId())
                && Objects.equals(username, group.getUsername())
                && Objects.equals(name, group.getName())
                && Objects.equals(contacts, group.getContacts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, contacts);
    }
}
