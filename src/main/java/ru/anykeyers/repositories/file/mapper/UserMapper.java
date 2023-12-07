package ru.anykeyers.repositories.file.mapper;

import ru.anykeyers.domain.User;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;

/**
 * Парсер для пользователя
 */
public class UserMapper implements Mapper<User> {

    @Override
    public String format(User object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":state_type=").append(object.getStateType())
                .append(";state=").append(object.getState())
                .append(";bot_type=").append(object.getBotType())
                .append(";edit_info=").append(object.getEditInfo())
                .append(";chat_id=").append(object.getChatId());
        return builder.toString();
    }

    @Override
    public User parse(String line) {
        String[] usernameAndConfigs = line.split(":");
        String username = usernameAndConfigs[0];
        String[] configs = usernameAndConfigs[1].split(";");
        User user = new User(username);

        for (String config : configs) {
            String[] keyValue = config.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            switch (key) {
                case "state_type":
                    user.setStateType(StateType.valueOf(value));
                    break;
                case "state":
                    user.setState(State.valueOf(value));
                    break;
                case "bot_type":
                    user.setBotType(BotType.valueOf(value));
                    break;
                case "edit_info":
                    if (value == null || value.equals("null")) {
                        break;
                    }
                    user.setEditInfo(value);
                    break;
                case "chat_id":
                    if (value == null || value.equals("null")) {
                        break;
                    }
                    user.setChatId(Long.valueOf(value));
                    break;
            }
        }
        return user;
    }

}
