package ru.anykeyers.domain;

import java.util.Objects;

/**
 * Класс пользователя
 */
public class User {

    /**
     * Имя пользователя
     */
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return String.format(username);
    }

}
