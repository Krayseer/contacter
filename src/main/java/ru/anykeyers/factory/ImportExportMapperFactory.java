package ru.anykeyers.factory;

import ru.anykeyers.service.FileService;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.service.impl.import_export.CSVFileService;
import ru.anykeyers.service.impl.import_export.JSONFileService;
import ru.anykeyers.service.impl.import_export.txt.TXTFileService;
import ru.anykeyers.service.impl.import_export.txt.domain.TXTContactMapper;
import ru.anykeyers.service.impl.import_export.xml.XMLFileService;

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
        resultMap.put(FileFormat.TXT, new TXTFileService<>(new TXTContactMapper()));
        resultMap.put(FileFormat.XML, new XMLFileService());
        resultMap.put(FileFormat.CSV, new CSVFileService());
        resultMap.put(FileFormat.JSON, new JSONFileService());
        return resultMap;
    }

}
