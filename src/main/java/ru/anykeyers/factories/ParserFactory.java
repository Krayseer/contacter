package ru.anykeyers.factories;

import ru.anykeyers.parsers.Parser;
import ru.anykeyers.parsers.csv.CsvParser;
import ru.anykeyers.parsers.json.JsonParser;
import ru.anykeyers.parsers.txt.TxtParser;
import ru.anykeyers.parsers.xml.XmlParser;
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
    public Parser createParser(Format format) {
        return switch (format) {
            case TXT -> new TxtParser(contactRepository);
            case XML -> new XmlParser(contactRepository);
            case CSV -> new CsvParser(contactRepository);
            case JSON -> new JsonParser(contactRepository);
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
