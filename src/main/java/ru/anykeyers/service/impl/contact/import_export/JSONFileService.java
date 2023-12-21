package ru.anykeyers.service.impl.contact.import_export;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.service.FileService;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Реализация {@link FileService сервиса} для JSON файлов
 */
public class JSONFileService implements FileService<Contact> {

    private final Messages messages = Messages.getInstance();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Collection<Contact> initDataFromFile(File file) {
        try {
            return objectMapper.readValue(file, new TypeReference<>() {});
        } catch (IOException exception) {
            String errorMessage = messages.getMessageByKey("import_export.json.error.import");
            throw new RuntimeException(errorMessage);
        }
    }

    @Override
    public void saveOrUpdateFile(File exportFile, Collection<Contact> contacts) {
        try {
            objectMapper.writeValue(exportFile, contacts);
        } catch (Exception exception) {
            String errorMessage = messages.getMessageByKey("import_export.json.error.export");
            throw new RuntimeException(errorMessage);
        }
    }

}
