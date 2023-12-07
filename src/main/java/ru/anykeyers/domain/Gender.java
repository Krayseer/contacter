package ru.anykeyers.domain;

/**
 * Пол
 */
public enum Gender {

    MAN("Мужской"),

    WOMAN("Женский");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
