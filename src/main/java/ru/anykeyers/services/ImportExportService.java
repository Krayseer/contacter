package ru.anykeyers.services;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.ParserFactory;
import ru.anykeyers.parsers.Parseable;
import ru.anykeyers.repositories.ContactRepository;

/**
 * Сервис импорта/экспорта контактов
 */
public class ImportExportService {

    private final ParserFactory parserFactory;

    private final Messages messages;

    public ImportExportService(ContactRepository contactRepository) {
        this.parserFactory = new ParserFactory(contactRepository);
        messages = new Messages();
    }

    /**
     * Импортировать данные
     * @param user имя пользователя
     * @param importPath путь до файла импорта
     * @return сообщение выполнения обработки
     */
    public String importData(User user, String importPath) {
        ParserFactory.Format format;
        try {
            format = ParserFactory.Format.valueOf(getFormatFromPath(importPath));
        } catch (RuntimeException ex) {
            return messages.getMessageByKey("import.error");
        }
        Parseable parser = parserFactory.createParser(format);
        boolean resultParseImport = parser.parseImport(user.getUsername(), importPath);
        return resultParseImport
                ? messages.getMessageByKey("import.success", importPath)
                : messages.getMessageByKey("import.error");
    }

    /**
     * Экспортировать данные
     * @param user имя пользователя
     * @param exportPath путь до файла экспорта
     * @return сообщения выполнения обработки
     */
    public String exportData(User user, String exportPath) {
        ParserFactory.Format format;
        try {
            format = ParserFactory.Format.valueOf(getFormatFromPath(exportPath));
        } catch (RuntimeException ex) {
            return messages.getMessageByKey("export.error");
        }
        Parseable parser = parserFactory.createParser(format);
        boolean resultParseExport = parser.parseExport(user.getUsername(), exportPath);
        return resultParseExport
                ? messages.getMessageByKey("export.success", exportPath)
                : messages.getMessageByKey("export.error");
    }

    /**
     * Получить формат файла
     * @param path путь до файла
     * @return формат файла
     */
    private String getFormatFromPath(String path) {
        return path.split("\\.")[1].toUpperCase();
    }

}
