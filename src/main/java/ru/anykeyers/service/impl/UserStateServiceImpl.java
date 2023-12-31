package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.service.UserStateService;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация сервиса {@link UserStateService}
 */
public class UserStateServiceImpl implements UserStateService {

    /**
     * Карта вида [имя пользователя -> информация о его состоянии]
     */
    private final Map<String, StateInfo> stateInfoByUsername = new HashMap<>();

    @Override
    public StateInfo getUserState(User user) {
        return stateInfoByUsername.computeIfAbsent(user.getUsername(), s -> new StateInfo());
    }

}
