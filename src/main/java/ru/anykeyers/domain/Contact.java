package ru.anykeyers.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Контакт
 */
public class Contact {

    /**
     * Идентификатор контакта
     */
    private String id;

    /**
     * Имя пользователя, которому принадлежит контакт
     */
    private String username;

    /**
     * Название контакта
     */
    private String name;

    /**
     * Номер телефона
     */
    private String phoneNumber;

    public Contact(String username) {
        this.username = username;
    }

    public Contact(String username, String name) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact contact)) return false;
        return Objects.equals(id, contact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
