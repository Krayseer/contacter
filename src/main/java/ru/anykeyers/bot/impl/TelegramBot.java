package ru.anykeyers.bot.impl;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.anykeyers.bot.Bot;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.factory.RepositoryFactory;
import ru.anykeyers.processor.MessageProcessor;
import ru.anykeyers.processor.command.Command;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.service.AuthenticationService;
import ru.anykeyers.service.UserStateService;
import ru.anykeyers.service.impl.AuthenticationServiceImpl;
import ru.anykeyers.service.impl.UserStateServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Телеграм бот
 */
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private final Messages messages = Messages.getInstance();

    private final MessageProcessor messageProcessor;

    private final AuthenticationService authenticationService;

    public TelegramBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        authenticationService = new AuthenticationServiceImpl(userRepository);
        UserStateService userStateService = new UserStateServiceImpl();
        messageProcessor = new MessageProcessor(this, authenticationService, userStateService, repositoryFactory);
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
        return applicationProperties.getSettingByKey("telegram.token");
    }

    @Override
    public String getBotUsername() {
        return applicationProperties.getSettingByKey("telegram.username");
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        SendMessage msg = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.send-error");
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Отправить сообщение на обработку
     *
     * @param username имя пользователя
     * @param message сообщение
     */
    private void handleMessage(String username, String message) {
        messageProcessor.processMessage(username, message, BotType.TELEGRAM_BOT);
    }

    /**
     * Регистрация пользователя
     *
     * @param username имя пользователя
     * @param chatId идентификатор чата
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
            BotCommand botCommand = new BotCommand(
                    command.getCommandValue(),
                    messages.getMessageByKey(command.getDescriptionKey())
            );
            commands.add(botCommand);
        }
        setMyCommands.setCommands(commands);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            String errorMessage = messages.getMessageByKey("bot.telegram.exception.set-commands-error");
            throw new RuntimeException(errorMessage);
        }
    }

}