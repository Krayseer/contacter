package ru.anykeyers.repositories.file.parsers;

import ru.anykeyers.domain.User;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.processors.states.domain.StateType;

/**
 * Парсер для пользователя
 */
public class FileUserParser implements FileObjectParser<User> {

    @Override
    public String parseTo(User object) {
        StringBuilder builder = new StringBuilder();
        builder.append(object.getUsername())
                .append(":state_type=").append(object.getStateType())
                .append(";state=").append(object.getState())
                .append(";bot_type=").append(object.getBotType())
                .append(";edit_contact_name=").append(object.getContactNameToEdit())
                .append(";edit_group_name=").append(object.getGroupNameToEdit())
                .append(";chat_id=").append(object.getChatId());
        return builder.toString();
    }

    @Override
    public User parseFrom(String line) {
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
                case "edit_contact_name":
                    if (value == null || value.equals("null")) {
                        break;
                    }
                    user.setContactNameToEdit(value);
                    break;
                case "edit_group_name":
                    if (value == null || value.equals("null")) {
                        break;
                    }
                    user.setGroupNameToEdit(value);
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
