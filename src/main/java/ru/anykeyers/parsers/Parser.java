package ru.anykeyers.parsers;

/**
 * Интерфейс, описывающий функционал парсеров
 */
public interface Parser {

    /**
     * Распарсить контакты в файл импорта
     * @param username имя пользователя
     * @return импорт был выполнен успешно или нет
     */
    boolean parseImport(String username, String importPath);

    /**
     * Распарсить контакты в файл экспорта
     * @param username имя пользователя
     * @return экспорт был выполнен успешно или нет
     */
    boolean parseExport(String username, String exportPath);

}
