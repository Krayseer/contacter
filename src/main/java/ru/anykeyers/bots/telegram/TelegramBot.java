package ru.anykeyers.bots.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.anykeyers.bots.Bot;
import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.processors.commands.Command;
import ru.anykeyers.processors.commands.CommandProcessor;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.AuthenticationService;

import java.util.ArrayList;
import java.util.List;

import static ru.anykeyers.bots.BotType.TELEGRAM_BOT;

/**
 * Телеграм бот
 */
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private final CommandProcessor commandProcessor;

    private final AuthenticationService authenticationService;

    private final ApplicationProperties applicationProperties;

    public TelegramBot() {
        RepositoryFactory repositoryFactory = new RepositoryFactory();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        authenticationService = new AuthenticationService(userRepository);
        commandProcessor = new CommandProcessor(this, authenticationService);
        applicationProperties = new ApplicationProperties();
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
                User user = authenticationService.existsUser(username) ?
                        authenticationService.getUserByUsername(username) :
                        new User(username);
                TelegramConfig telegramConfig = new TelegramConfig(chatId);
                user.setTelegramConfig(telegramConfig);
                authenticationService.saveOrUpdateUser(user);
                sendCommands(chatId);
            }

            receiveMessage(username, text);
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveMessage(String username, String message) {
        commandProcessor.processMessage(username, message, TELEGRAM_BOT);
    }

    @Override
    public void start() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCommands(Long chatId) {
        SetMyCommands setMyCommands = new SetMyCommands();
        List<BotCommand> commands = new ArrayList<>();
        for (Command command : Command.values()) {
            BotCommand botCommand = new BotCommand(command.getCommandValue(), command.getDescription());
            commands.add(botCommand);
        }
        setMyCommands.setCommands(commands);

        try {
            execute(setMyCommands);
            SendMessage message = SendMessage.builder().chatId(chatId).build();
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}