package ru.anykeyers.repository.file.mapper;

import org.junit.Assert;
import org.junit.Test;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.bot.BotType;
import ru.anykeyers.domain.entity.User;

/**
 * Тестирование класса {@link FileUserMapper}
 */
public class FileUserMapperTest {

    private final Mapper<User> mapper = new FileUserMapper();

    /**
     * Тестирование форматирования пользователя в строку
     */
    @Test
    public void parseUserToStringTest() {
        // Подготовка
        User user = new User("testUser", BotType.TELEGRAM_BOT);

        // Действие
        String formattedUser = mapper.format(user);

        // Проверка
        String expectedResult = "testUser-TELEGRAM_BOT:bot_type=TELEGRAM_BOT;chat_id=null";
        Assert.assertEquals(expectedResult, formattedUser);
    }

    /**
     * Тестирование форматирования строки в пользователя
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