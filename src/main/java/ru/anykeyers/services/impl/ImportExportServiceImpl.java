package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.factories.ParserFactory;
import ru.anykeyers.parsers.Parser;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.ImportExportService;

/**
 * Реализация сервиса импорта/экспорта контактов
 */
public class ImportExportServiceImpl implements ImportExportService {

    private final ParserFactory parserFactory;

    private final Messages messages;

    public ImportExportServiceImpl(ContactRepository contactRepository) {
        this.parserFactory = new ParserFactory(contactRepository);
        messages = new Messages();
    }

    @Override
    public String importData(User user, String importPath) {
        ParserFactory.Format format = getFormatFromPath(importPath);
        if (format == null) {
            return messages.getMessageByKey("import.error");
        }
        Parser parser = parserFactory.createParser(format);
        boolean resultParseImport = parser.parseImport(user.getUsername(), importPath);
        return resultParseImport
                ? messages.getMessageByKey("import.success", importPath)
                : messages.getMessageByKey("import.error");
    }

    @Override
    public String exportData(User user, String exportPath) {
        ParserFactory.Format format = getFormatFromPath(exportPath);
        if (format == null) {
            return messages.getMessageByKey("export.error");
        }
        Parser parser = parserFactory.createParser(format);
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
    private ParserFactory.Format getFormatFromPath(String path) {
        String[] parts = path.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        return ParserFactory.Format.valueOf(parts[1].toUpperCase());
    }

}
