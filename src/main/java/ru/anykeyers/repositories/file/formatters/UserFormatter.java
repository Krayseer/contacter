package ru.anykeyers.repositories.file.formatters;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.bots.telegram.TelegramConfig;
import ru.anykeyers.domain.User;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;
import ru.anykeyers.repositories.file.data.ObjectData;
import ru.anykeyers.repositories.file.data.UserData;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Форматтер для таблицы пользователей
 */
public class UserFormatter implements ObjectFormatter<User> {

    @Override
    public String format(User object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":StateType=").append(object.getStateType())
                .append(";State=").append(object.getState())
                .append(";AppType=").append(object.getBotType())
                .append(";ContactNameToEdit=").append(object.getContactNameToEdit())
                .append(";GroupNameToEdit=").append(object.getGroupNameToEdit())
                .append(";TelegramConfig=").append(object.getTelegramConfig() == null ? "" : object.getTelegramConfig().getChatId());
        return builder.toString();
    }

    @Override
    public Map<String, ObjectData<User>> initFromFile(File file) {
        Map<String, ObjectData<User>> collectionMap = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            lines.forEach(line -> {
                User user = parseUserFromFormattedString(line);
                String username = user.getUsername();
                collectionMap.computeIfAbsent(username, k -> new UserData()).addData(user);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return collectionMap;
    }

    /**
     * Парсинг пользователя из строки в объект
     */
    private User parseUserFromFormattedString(String formattedString) {
        String[] usernameAndConfigs = formattedString.split(":");
        String username = usernameAndConfigs[0];
        String[] configs = usernameAndConfigs[1].split(";");

        User user = new User(username);

        for (String config : configs) {
            String[] keyValue = config.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];

            switch (key) {
                case "StateType":
                    user.setStateType(StateType.valueOf(value));
                    break;
                case "State":
                    user.setState(State.valueOf(value));
                    break;
                case "AppType":
                    user.setBotType(BotType.valueOf(value));
                    break;
                case "ContactNameToEdit":
                    user.setContactNameToEdit(value);
                    break;
                case "GroupNameToEdit":
                    user.setGroupNameToEdit(value);
                    break;
                case "TelegramConfig":
                    if (value == null) {
                        break;
                    }
                    Long chatId = Long.valueOf(value);
                    TelegramConfig telegramConfig = new TelegramConfig(chatId);
                    user.setTelegramConfig(telegramConfig);
                    break;
            }
        }

        return user;
    }

}
