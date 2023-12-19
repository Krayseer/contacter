package ru.anykeyers.repository.file.service.impl;

import ru.anykeyers.repository.file.service.FileRepositoryService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link FileRepositoryService}
 */
public class FileRepositoryServiceImpl<T> implements FileRepositoryService<T> {

    @Override
    public Collection<T> getCollectionFromMap(Map<String, ? extends Collection<T>> dataByUsername) {
        List<T> collection = new ArrayList<>();
        dataByUsername.values().forEach(collection::addAll);
        return collection;
    }

    @Override
    public Map<String, Set<T>> getMapFromCollection(Collection<T> data,
                                                                     Function<T, String> generateKeyFunction) {
        return data.stream()
                .collect(Collectors.groupingBy(generateKeyFunction, Collectors.toSet()));
    }

}
