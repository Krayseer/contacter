package ru.anykeyers.repositories.file.services.impl;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.repositories.file.parsers.FileObjectParser;
import ru.anykeyers.repositories.file.services.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с файловой БД
 */
public class FileServiceImpl<T> implements FileService<T> {

    private final FileObjectParser<T> formatter;

    private final Messages messages;

    public FileServiceImpl(FileObjectParser<T> formatter) {
        this.formatter = formatter;
        messages = new Messages();
    }

    @Override
    public Collection<T> initDataFromFile(File dbFile) {
        Collection<T> data = new ArrayList<>();
        try {
            List<String> lines = FileUtils.readLines(dbFile, StandardCharsets.UTF_8);
            lines.forEach(line -> {
                T object = formatter.parseFrom(line);
                data.add(object);
            });
        } catch (IOException e) {
            String errorMessage = messages.getMessageByKey("file.read-error", dbFile.getPath());
            throw new RuntimeException(errorMessage);
        }
        return data;
    }

    @Override
    public void saveOrUpdateFile(File dbFile, Map<String, ? extends Collection<T>> data) {
        try {
            List<String> linesToSave = data.values().stream()
                    .flatMap(Collection::stream)
                    .map(formatter::parseTo)
                    .collect(Collectors.toList());
            FileUtils.writeLines(dbFile, StandardCharsets.UTF_8.name(), linesToSave, false);
        } catch (IOException e) {
            String errorMessage = messages.getMessageByKey("file.read-error", dbFile.getPath());
            throw new RuntimeException(errorMessage);
        }
    }

}
