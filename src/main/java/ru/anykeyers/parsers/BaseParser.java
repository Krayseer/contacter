package ru.anykeyers.parsers;

import ru.anykeyers.repositories.ContactRepository;

/**
 * Базовый класс для парсеров
 */
public abstract class BaseParser implements Parseable {

    protected final ContactRepository contactRepository;

    public BaseParser(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

}
