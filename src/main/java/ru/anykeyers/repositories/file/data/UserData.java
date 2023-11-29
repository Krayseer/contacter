package ru.anykeyers.repositories.file.data;

import ru.anykeyers.domain.User;

/**
 * Информация о пользователе
 */
public class UserData implements ObjectData<User> {

    private User user;

    @Override
    public Object getData() {
        return user;
    }

    @Override
    public void addData(User user) {
        this.user = user;
    }

    @Override
    public void removeData(User user) {
        this.user = null;
    }

}
