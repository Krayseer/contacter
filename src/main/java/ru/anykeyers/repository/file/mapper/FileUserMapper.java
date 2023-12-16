package ru.anykeyers.repository.file.mapper;

import ru.anykeyers.common.Mapper;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.bot.BotType;

/**
 * Маппер для пользователя
 */
public class FileUserMapper implements Mapper<User> {

    @Override
    public String format(User object) {
        return """
                %s:bot_type=%s;chat_id=%s"""
                .formatted(
                        object.getUsername(),
                        object.getBotType(),
                        object.getChatId()
                );
    }

    @Override
    public User parse(String str) {
        String[] usernameAndConfigs = str.split(":");
        String username = usernameAndConfigs[0];
        String[] configs = usernameAndConfigs[1].split(";");
        User user = new User(username);
        for (String config : configs) {
            String[] keyValue = config.split("=");
            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            switch (key) {
                case "bot_type":
                    user.setBotType(BotType.valueOf(value));
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
