package ru.anykeyers.processors.states.impl;

import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.services.ImportExportService;

/**
 * Обработчик состояний для импорта/экспорта данных пользователя
 */
public class ImportExportStateProcessor extends BaseStateProcessor {

    private final ImportExportService importExportService;


    public ImportExportStateProcessor(ImportExportService importExportService) {
        this.importExportService = importExportService;
        registerStateHandlers();
    }

    /**
     * Регистрация состояний и их обработчиков для импорта/экспорта
     */
    private void registerStateHandlers() {
        registerHandler(State.IMPORT, (importExportService::importData));
        registerHandler(State.EXPORT, (importExportService::exportData));
    }

}
