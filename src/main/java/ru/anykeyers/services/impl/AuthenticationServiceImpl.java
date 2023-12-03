package ru.anykeyers.services.impl;

import ru.anykeyers.bots.BotType;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.domain.User;
import ru.anykeyers.services.AuthenticationService;

/**
 * Реализация сервиса для аутентификации пользователей
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsUserByUsernameAndBotType(String username, BotType botType) {
        return userRepository.existsByUsernameAndBotType(username, botType);
    }

    @Override
    public User getUserByUsernameAndBotType(String username, BotType botType) {
        return userRepository.getUserByUsernameAndBotType(username, botType);
    }

    @Override
    public void saveOrUpdateUser(User user) {
        userRepository.saveOrUpdate(user);
    }

}
