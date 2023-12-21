package ru.anykeyers.service.impl.contact.import_export.txt;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.common.Messages;
import ru.anykeyers.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация {@link FileService сервиса} для TXT файлов
 */
public class TXTFileService<T> implements FileService<T> {

    private final Messages messages = Messages.getInstance();

    private final Mapper<T> mapper;

    public TXTFileService(Mapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Collection<T> initDataFromFile(File file) {
        Collection<T> data = new ArrayList<>();
        try {
            List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
            lines.forEach(line -> {
                T object = mapper.parse(line);
                data.add(object);
            });
        } catch (IOException e) {
            String errorMessage = messages.getMessageByKey("import_export.txt.error.import");
            throw new RuntimeException(errorMessage);
        }
        return data;
    }

    @Override
    public void saveOrUpdateFile(File exportFile, Collection<T> contacts) {
        try {
            List<String> linesToSave = contacts.stream()
                    .map(mapper::format)
                    .collect(Collectors.toList());
            FileUtils.writeLines(exportFile, StandardCharsets.UTF_8.name(), linesToSave, false);
        } catch (IOException e) {
            String errorMessage = messages.getMessageByKey("import_export.txt.error.export");
            throw new RuntimeException(errorMessage);
        }
    }

}
