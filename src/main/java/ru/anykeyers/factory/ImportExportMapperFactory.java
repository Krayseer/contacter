package ru.anykeyers.factory;

import ru.anykeyers.repository.file.service.FileService;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.domain.entity.Contact;

import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика по созданию сервисов для импорта экспорта
 */
public class ImportExportMapperFactory {

    /**
     * Карта вида [формат -> сервис для этого формата]
     */
    private final Map<FileFormat, FileService<Contact>> servicesByFormat;

    public ImportExportMapperFactory() {
        servicesByFormat = initServicesByFormat();
    }

    /**
     * Получить сервис в зависимости от формата файла
     *
     * @param format формат файла
     */
    public FileService<Contact> getServiceByFormat(FileFormat format) {
        return servicesByFormat.get(format);
    }

    /**
     * Создание карты вида [формат -> маппер для формата]
     */
    private Map<FileFormat, FileService<Contact>> initServicesByFormat() {
        Map<FileFormat, FileService<Contact>> resultMap = new HashMap<>();
//        resultMap.put(FileFormat.TXT, ...); //todo Реализация
//        resultMap.put(FileFormat.XML, ...); //todo Реализация
//        resultMap.put(FileFormat.CSV, ...); //todo Реализация
//        resultMap.put(FileFormat.JSON, ...); //todo Реализация
        return resultMap;
    }

}
