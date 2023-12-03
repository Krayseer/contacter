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
 * Типы репозиториев устанавливаются в application.properties в ключе "storage.type"
 */
public class RepositoryFactory {

    private final ApplicationProperties applicationProperties;

    private final Messages messages;

    private final StorageType storageType;

    public RepositoryFactory() {
        messages = new Messages();
        applicationProperties = new ApplicationProperties();
        String storageTypeFromSettings = applicationProperties.getSetting("storage.type");
        try {
           storageType = StorageType.valueOf(storageTypeFromSettings);
        } catch (IllegalArgumentException ex) {
            String errorMessage = messages.getMessageByKey("storage.invalid-type", storageTypeFromSettings);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Создать экземпляр {@link UserRepository}
     */
    public UserRepository createUserRepository() {
        switch (storageType) {
            case FILE:
                String pathToUserTableFile = applicationProperties.getSetting("file-users-table");
                return new FileUserRepository(pathToUserTableFile);
        }
        String errorMessage = messages.getMessageByKey("storage.invalid-type", storageType);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Создать экземпляр {@link ContactRepository}
     */
    public ContactRepository createContactRepository() {
        switch (storageType) {
            case FILE:
                String pathToContactTableFile = applicationProperties.getSetting("file-contacts-table");
                return new FileContactRepository(pathToContactTableFile);
        }
        String errorMessage = messages.getMessageByKey("storage.invalid-type", storageType);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Создать экземпляр {@link GroupRepository}
     */
    public GroupRepository createGroupRepository() {
        switch (storageType) {
            case FILE:
                String pathToGroupTableFile = applicationProperties.getSetting("file-groups-table");
                String pathToContactTableFile = applicationProperties.getSetting("file-contacts-table");
                return new FileGroupRepository(pathToGroupTableFile, new FileContactRepository(pathToContactTableFile));
        }
        String errorMessage = messages.getMessageByKey("storage.invalid-type", storageType);
        throw new IllegalArgumentException(errorMessage);
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
