package ru.anykeyers.repository.file.service.impl;

import org.apache.commons.io.FileUtils;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.exception.FileReadException;
import ru.anykeyers.repository.file.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с файловой БД
 */
public class FileServiceImpl<T> implements FileService<T> {

    private final Mapper<T> mapper;

    public FileServiceImpl(Mapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Collection<T> initDataFromFile(File dbFile) {
        Collection<T> data = new ArrayList<>();
        try {
            List<String> lines = FileUtils.readLines(dbFile, StandardCharsets.UTF_8);
            lines.forEach(line -> {
                T object = mapper.parse(line);
                data.add(object);
            });
        } catch (IOException e) {
            throw new FileReadException(dbFile.getPath());
        }
        return data;
    }

    @Override
    public void saveOrUpdateFile(File dbFile, Collection<T> data) {
        try {
            List<String> linesToSave = data.stream()
                    .map(mapper::format)
                    .collect(Collectors.toList());
            FileUtils.writeLines(dbFile, StandardCharsets.UTF_8.name(), linesToSave, false);
        } catch (IOException e) {
            throw new FileReadException(dbFile.getPath());
        }
    }

}
