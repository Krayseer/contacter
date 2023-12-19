package ru.anykeyers.repository.file;

import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.repository.file.service.FileService;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.file.mapper.FileGroupMapper;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.common.Mapper;
import ru.anykeyers.repository.file.service.impl.FileServiceImpl;
import ru.anykeyers.repository.file.service.FileRepositoryService;
import ru.anykeyers.repository.file.service.impl.FileRepositoryServiceImpl;

import java.io.File;
import java.util.*;

/**
 * Реализация файловой базы данных для групп
 */
public class FileGroupRepository implements GroupRepository {

    private final Map<String, Set<Group>> groupsByUsername;

    private final File dbFile;

    private final FileService<Group> fileService;

    private final FileRepositoryService<Group> repositoryService;

    public FileGroupRepository(String groupFilePath,
                               ContactRepository contactRepository) {
        dbFile = new File(groupFilePath);
        Mapper<Group> groupMapper = new FileGroupMapper(contactRepository);
        fileService = new FileServiceImpl<>(groupMapper);
        repositoryService = new FileRepositoryServiceImpl<>();
        Collection<Group> groups = fileService.initDataFromFile(dbFile);
        groupsByUsername = repositoryService.getMapFromCollection(groups, Group::getUsername);
    }

    @Override
    public boolean existsByUsernameAndName(String username, String name) {
        return Optional.ofNullable(groupsByUsername.get(username))
                .map(groups -> groups.stream()
                        .map(Group::getName)
                        .anyMatch(groupName -> Objects.equals(groupName, name)))
                .orElse(false);
    }

    @Override
    public Set<Group> findByUsername(String username) {
        return groupsByUsername.getOrDefault(username, Collections.emptySet());
    }

    @Override
    public Optional<Group> getByUsernameAndName(String username, String groupName) {
        return Optional.ofNullable(groupsByUsername.get(username))
                .flatMap(groups -> groups.stream()
                        .filter(group -> Objects.equals(group.getName(), groupName))
                        .findFirst());
    }

    @Override
    public void saveOrUpdate(Group group) {
        groupsByUsername.computeIfAbsent(group.getUsername(), k -> new HashSet<>()).add(group);
        fileService.saveOrUpdateFile(dbFile, repositoryService.getCollectionFromMap(groupsByUsername));
    }

    @Override
    public void delete(Group group) {
        Optional.ofNullable(groupsByUsername.get(group.getUsername()))
                .ifPresent(groups -> groups.remove(group));
    }

}
