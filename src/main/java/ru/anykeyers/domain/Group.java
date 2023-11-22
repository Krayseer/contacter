package ru.anykeyers.domain;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сущность группы
 */
public class Group {

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

    public Group(String username, String id, String name, Set<Contact> contacts) {
        this.username = username;
        this.name = name;
        this.contacts = contacts;
        this.id = id;
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

    public void addContactInGroup(Contact contact) {
        contacts.add(contact);
    }

    public void deleteContactFromGroup(Contact contact) {
        contacts.remove(contact);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return Objects.equals(username, group.getUsername()) && Objects.equals(name, group.getName()) && Objects.equals(contacts, group.getContacts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, contacts);
    }

    @Override
    public String toString() {
        String contactsToString = contacts.stream()
                .map(Contact::toString)
                .collect(Collectors.joining(";"));
        return String.format("%s:%s=%s", username, name, contactsToString);
    }

}
