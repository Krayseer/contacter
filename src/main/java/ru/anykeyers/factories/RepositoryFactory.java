package ru.anykeyers.factories;

import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.repositories.file.FileContactRepository;
import ru.anykeyers.repositories.file.FileGroupRepository;
import ru.anykeyers.repositories.file.FileUserRepository;

/**
 * Фабрика для создания репозиториев<br/>
 * Типы репозиториев устанавливаются в application.properties в ключе STORAGE_TYPE
 */
public class RepositoryFactory {

    private final ApplicationProperties applicationProperties;

    private final StorageType STORAGE_TYPE;

    public RepositoryFactory() {
        Messages messages = new Messages();
        applicationProperties = new ApplicationProperties();
        String storageType = applicationProperties.getSetting("storage.type");
        try {
           STORAGE_TYPE = StorageType.valueOf(storageType);
        } catch (RuntimeException e) {
           throw new IllegalArgumentException(messages.getMessageByKey("storage.invalid-type", storageType));
        }
    }

    public UserRepository createUserRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new FileUserRepository(applicationProperties.getSetting("file-users-table"));
            default:
                return null;
        }
    }

    public ContactRepository createContactRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new FileContactRepository(applicationProperties.getSetting("file-contacts-table"));
            default:
                return null;
        }
    }

    public GroupRepository createGroupRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new FileGroupRepository(applicationProperties.getSetting("file-groups-table"));
            default:
                return null;
        }
    }

    /**
     * Тип хранилища, где будут храниться данные
     */
    private enum StorageType {
        FILE,
        POSTGRES,
        MY_SQL,
        CASSANDRA
    }

}
