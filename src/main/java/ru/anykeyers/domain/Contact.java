package ru.anykeyers.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность контакта
 */
public class Contact {

    private String id;

    private String username;

    /**
     * Название контакта
     */
    private String name;

    /**
     * Номер телефона
     */
    private String phoneNumber;

    public Contact(String username, String name) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.name = name;
        this.phoneNumber = "";
    }

    public Contact(String username, String id, String name, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
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
        return Objects.equals(name, contact.getName()) && Objects.equals(phoneNumber, contact.getPhoneNumber());
    }

    public String getInfo() {
        return String.format("Имя: %s. Номер телефона: %s.", name, phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }

}
