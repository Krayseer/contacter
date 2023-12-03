package ru.anykeyers.bots.impl;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.processors.MessageProcessor;
import ru.anykeyers.processors.commands.Command;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.impl.AuthenticationServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Телеграм бот
 */
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private final MessageProcessor messageProcessor;

    private final AuthenticationService authenticationService;

    private final ApplicationProperties applicationProperties;

    private final Messages messages;

    public TelegramBot() {
        applicationProperties = new ApplicationProperties();
        messages = new Messages();
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        authenticationService = new AuthenticationServiceImpl(userRepository);
        messageProcessor = new MessageProcessor(this, authenticationService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            org.telegram.telegrambots.meta.api.objects.User telegramUser = message.getFrom();
            String username = telegramUser.getUserName() != null
                    ? telegramUser.getUserName()
                    : telegramUser.getId().toString();
            String text = message.getText();
            Long chatId = message.getChatId();
            if(text.equals("/start")) {
                initUser(username, chatId);
                String welcomeMessage = messages.getMessageByKey("bot.welcome");
                sendMessage(chatId, welcomeMessage);
                return;
            }
            handleMessage(username, text);
        }
    }

    @Override
    public String getBotToken() {
        return applicationProperties.getSetting("telegram.token");
    }

    @Override
    public String getBotUsername() {
        return applicationProperties.getSetting("telegram.username");
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        SendMessage msg = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.send-error");
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Обработать сообщение
     * @param username имя пользователя
     * @param message сообщение
     */
    private void handleMessage(String username, String message) {
        messageProcessor.processMessage(username, message, BotType.TELEGRAM_BOT);
    }

    /**
     * Проинициализировать пользователя
     * @param username имя пользователя
     * @param chatId идентификатор чат-бота
     */
    private void initUser(String username, Long chatId) {
        User user = authenticationService.existsUserByUsernameAndBotType(username, BotType.TELEGRAM_BOT)
                ? authenticationService.getUserByUsernameAndBotType(username, BotType.TELEGRAM_BOT)
                : new User(username, BotType.TELEGRAM_BOT);
        user.setChatId(chatId);
        authenticationService.saveOrUpdateUser(user);
        setCommands();
    }

    /**
     * Установить в меню возможные команды в приложении
     */
    private void setCommands() {
        SetMyCommands setMyCommands = new SetMyCommands();
        List<BotCommand> commands = new ArrayList<>();
        for (Command command : Command.values()) {
            BotCommand botCommand = new BotCommand(command.getCommandValue(), command.getDescription());
            commands.add(botCommand);
        }
        setMyCommands.setCommands(commands);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.set-commands-error");
            throw new RuntimeException(errorMessage);
        }
    }

}