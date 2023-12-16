package ru.anykeyers.service.impl;

import ru.anykeyers.bot.BotType;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.service.AuthenticationService;

/**
 * Реализация сервиса {@link AuthenticationService}
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
