package ru.anykeyers.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * Возраст
     */
    private int age;

    /**
     * Пол контакта
     */
    private Gender gender;

    /**
     * Блокировка контакта
     */
    private boolean block;

    public Contact() { }

    public Contact(String username) {
        this.username = username;
    }

    public Contact(String username, String name) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.name = name;
    }

    public Contact(String id, String username, String name, String phoneNumber, int age, Gender gender, boolean block) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.block = block;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    /**
     * @return человекочитаемая информация о контакте
     */
    @JsonIgnore
    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        return builder
                .append("Имя: ").append(name)
                .append("\nНомер телефона: ").append(phoneNumber != null ? phoneNumber : StringUtils.EMPTY)
                .append("\nВозраст: ").append(age != 0 ? age : StringUtils.EMPTY)
                .append("\nПол: ").append(gender != null ? gender.getName() : StringUtils.EMPTY)
                .append("\nЗаблокирован: ").append(block ? "да" : "нет")
                .toString();
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
