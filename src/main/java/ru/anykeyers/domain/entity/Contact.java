package ru.anykeyers.domain.entity;

import ru.anykeyers.domain.Gender;

import java.util.Objects;
import java.util.UUID;

/**
 * Контакт
 */
public class Contact {

    /**
     * Идентификатор
     */
    private String id;

    /**
     * Имя пользователя, которому принадлежит контакт
     */
    private String username;

    /**
     * Имя
     */
    private String name;

    /**
     * Номер телефона
     */
    private String phoneNumber;

    /**
     * Возраст
     */
    private Integer age;

    /**
     * Пол
     */
    private Gender gender;

    /**
     * Блокировка
     */
    private boolean blocked;

    public Contact() { }

    public Contact(String username) {
        this.username = username;
    }

    public Contact(String username, String name) {
        this(username);
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public Contact(String id, String username, String name, String phoneNumber, Integer age, Gender gender, boolean blocked) {
        this(username, name);
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.blocked = blocked;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
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
