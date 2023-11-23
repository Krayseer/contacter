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

    /**
     * Возраст
     */
    private int age;

    /**
     * Пол контакта
     */
    private String gender;

    /**
     * Блокировка контакта
     */
    private String block;

    public Contact() { }

    public Contact(String username, String id, String name, int age, String gender, String block, String phoneNumber) {
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.block = block;
        this.id = id;
    }

    public Contact(String username, String name) {
        this.username = username;
        this.name = name;
        this.phoneNumber = "";
        this.age = -1;
        this.gender = "UNKNOWN";
        this.block = "UNBLOCK";
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
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
        return String.format("%s:%s,%s,%d,%s,%s,%s", username, id, name, age, gender, block, phoneNumber);
    }

    public String getContactString(Contact contact) {
        String contactAge = contact.getAge() < 0
                ? "Возраст не определен"
                : Integer.toString(contact.getAge());
        String contactGender = (!contact.getGender().equals("Мужчина") && !contact.getGender().equals("Женщина"))
                ? "Пол не определен"
                : contact.getGender();
        String contactNumber = contact.getPhoneNumber().equals("")
                ? "Номер телефона не определен"
                : contact.getPhoneNumber();
        return String.format("Имя: %s, Возраст: %s, Пол: %s, Номер телефона: %s", contact.getName(),
                contactAge, contactGender, contactNumber);
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
        CONTACT_PHONE_NUMBER,
        /**
         * Возраст контакта
         */
        CONTACT_AGE,
        /**
         * Пол контакта
         */
        CONTACT_GENDER,
        /**
         * Блокировка контакта
         */
        CONTACT_BLOCK;

    }

}
