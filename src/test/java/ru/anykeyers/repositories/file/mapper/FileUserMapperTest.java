package ru.anykeyers.repositories.file.mapper;

import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.bots.BotType;
import ru.anykeyers.domain.User;

/**
 * Тестирование класса {@link UserMapper}
 */
public class FileUserMapperTest {

    private final UserMapper mapper;

    public FileUserMapperTest() {
        mapper = new UserMapper();
    }

    /**
     * Тестирование парсинга пользователя в строку
     */
    @Test
    public void parseUserToStringTest() {
        // Подготовка
        User user = new User("testUser", BotType.TELEGRAM_BOT);

        // Действие
        String formattedUser = mapper.format(user);

        // Проверка
        String expectedResult = "testUser-TELEGRAM_BOT:state_type=NONE;state=NONE;bot_type=TELEGRAM_BOT;" +
            "edit_info=null;chat_id=null";
        Assert.assertEquals(expectedResult, formattedUser);
    }

    /**
     * Тестирование пасринга пользователя из строки
     */
    @Test
    public void parseUserFromStringTest() {
        // Подготовка
        String userString = "testUser-TELEGRAM_BOT:state_type=NONE;state=NONE;bot_type=TELEGRAM_BOT;" +
                "edit_contact_name=null;edit_group_name=null;chat_id=null";

        // Действие
        User user = mapper.parse(userString);

        // Проверка
        User expectedUser = new User("testUser", BotType.TELEGRAM_BOT);
        Assert.assertEquals(expectedUser, user);
    }

}