package ru.anykeyers.factories;

import ru.anykeyers.contexts.ApplicationProperties;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.repositories.file.FileUserRepository;

public class RepositoryFactory {

    private final ApplicationProperties applicationProperties;

    private final StorageType STORAGE_TYPE;

    public RepositoryFactory() {
        Messages messages = new Messages();
        applicationProperties = new ApplicationProperties();
        String storageSetting = applicationProperties.getSetting("storage.type");
        try {
           STORAGE_TYPE = StorageType.valueOf(storageSetting);
        } catch (RuntimeException e) {
           throw new IllegalArgumentException(messages.getMessageByKey("storage.invalid-type", storageSetting));
        }
    }

    public UserRepository createUserRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new FileUserRepository(applicationProperties.getSetting("users-table"));
            default:
                return null;
        }
    }

    public ContactRepository createContactRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new ContactRepository(applicationProperties.getSetting("contacts-table"));
            default:
                return null;
        }
    }

    public GroupRepository createGroupRepository() {
        switch (STORAGE_TYPE) {
            case FILE:
                return new GroupRepository(applicationProperties.getSetting("groups-table"));
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
