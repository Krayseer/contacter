package ru.anykeyers.factory;

import ru.anykeyers.common.ApplicationProperties;
import ru.anykeyers.exception.InvalidStorageTypeException;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.repository.GroupRepository;
import ru.anykeyers.repository.UserRepository;
import ru.anykeyers.repository.file.FileContactRepository;
import ru.anykeyers.repository.file.FileGroupRepository;
import ru.anykeyers.repository.file.FileUserRepository;

/**
 * Фабрика для создания репозиториев<br/>
 * Типы репозиториев устанавливаются в application.properties в ключе "storage.type"
 */
public class RepositoryFactory {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private final StorageType storageType;

    public RepositoryFactory() {
        String typeFromSettings = applicationProperties.getSettingByKey("storage.type");
        try {
           storageType = StorageType.valueOf(typeFromSettings);
        } catch (IllegalArgumentException ex) {
            throw new InvalidStorageTypeException(typeFromSettings);
        }
    }

    /**
     * Создать экземпляр {@link UserRepository}
     */
    public UserRepository createUserRepository() {
        return switch (storageType) {
            case FILE -> {
                String pathToUserTableFile = applicationProperties.getSettingByKey("file-users-table");
                yield new FileUserRepository(pathToUserTableFile);
            }
        };
    }

    /**
     * Создать экземпляр {@link ContactRepository}
     */
    public ContactRepository createContactRepository() {
        return switch (storageType) {
            case FILE -> {
                String pathToContactTableFile = applicationProperties.getSettingByKey("file-contacts-table");
                yield new FileContactRepository(pathToContactTableFile);
            }
        };
    }

    /**
     * Создать экземпляр {@link GroupRepository}
     */
    public GroupRepository createGroupRepository() {
        return switch (storageType) {
            case FILE -> {
                String pathToGroupTableFile = applicationProperties.getSettingByKey("file-groups-table");
                String pathToContactTableFile = applicationProperties.getSettingByKey("file-contacts-table");
                yield new FileGroupRepository(pathToGroupTableFile, new FileContactRepository(pathToContactTableFile));
            }
        };
    }

    /**
     * Тип хранилища, где будут храниться данные
     */
    private enum StorageType {
        /**
         * Файловая БД
         */
        FILE,
    }

}
