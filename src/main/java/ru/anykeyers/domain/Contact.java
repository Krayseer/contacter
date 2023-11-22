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

    public Contact(String username, String id, String name, String phoneNumber) {
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public Contact(String username, String name) {
        this.username = username;
        this.name = name;
        this.phoneNumber = "";
        this.id = UUID.randomUUID().toString();
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

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }

    @Override
    public String toString() {
        return String.format("%s:%s,%s,%s", username, id.toString(), name, phoneNumber);
    }

    /**
     * Перечисление, использующееся для уточнения, какое поле у класса нужно изменить
     */
    public enum Field {

        /**
         * Имя контакта
         */
        CONTACT_NAME,
        /**
         * Номер контакта
         */
        CONTACT_PHONE_NUMBER;

    }

}
