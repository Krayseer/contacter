package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.Message;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.domain.FileFormat;
import ru.anykeyers.service.ContactService;
import ru.anykeyers.processor.state.BaseStateProcessor;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.kinds.ExportKind;
import ru.anykeyers.service.UserStateService;

import java.io.File;
import java.nio.file.Files;

/**
 * Обработчик состояний для импорта/экспорта контактов
 */
public class ImportExportStateProcessor extends BaseStateProcessor {

    private final Messages messages = Messages.getInstance();

    private final ContactService contactService;

    public ImportExportStateProcessor(UserStateService userStateService,
                                      ContactService contactService) {
        super(userStateService);
        this.contactService = contactService;
        registerStateHandlers();
    }

    private void registerStateHandlers() {
        registerHandler(State.IMPORT, ((user, path) -> {
            File importFile;
            try {
                importFile = new File(path);
                contactService.importContacts(user, importFile);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return new Message(messages.getMessageByKey("import_export.import.successful"));
        }));
        registerHandler(State.EXPORT, ((user, field) -> {
            FileFormat format = switch (field) {
                case ExportKind.TXT -> FileFormat.TXT;
                case ExportKind.JSON -> FileFormat.JSON;
                case ExportKind.XML -> FileFormat.XML;
                case ExportKind.CSV -> FileFormat.CSV;
                default -> throw new BadArgumentException();
            };
            File exportFile;
            try {
                exportFile = Files.createTempFile("contacts", format.getName()).toFile();
                contactService.exportContacts(user, exportFile);
            } catch (Exception ex) {
                return new Message(ex.getMessage());
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return new Message(exportFile);
        }));
    }

}
