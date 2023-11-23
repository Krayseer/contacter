package ru.anykeyers.factories;

import ru.anykeyers.parsers.Parseable;
import ru.anykeyers.repositories.ContactRepository;

/**
 * Фабрика создания необходимого парсера
 */
public class ParserFactory {

    private final ContactRepository contactRepository;

    public ParserFactory(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Создать парсер в зависимости от формата файла
     * @param format формат файла
     * @return экземпляр необходимого парсера
     */
    public Parseable createParser(Format format) {
        return switch (format) {
            // TODO: 23.11.2023 Реализация парсеров
            case TXT -> null;
            case XML -> null;
            case CSV -> null;
            case JSON -> null;
        };
    }

    /**
     * Формат файла
     */
    public enum Format {
        /**
         * Формат .txt
         */
        TXT,
        /**
         * Формат .xml
         */
        XML,
        /**
         * Формат .csv
         */
        CSV,
        /**
         * Формат .json
         */
        JSON
    }

}
