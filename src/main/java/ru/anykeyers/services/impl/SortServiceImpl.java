package ru.anykeyers.services.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.Contact;
import ru.anykeyers.domain.User;
import ru.anykeyers.domain.kinds.sort.SortKindType;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.services.SortService;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса по сортировке контактов
 */
public class SortServiceImpl implements SortService {

    private final ContactRepository contactRepository;

    private final Messages messages;

    public SortServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
        messages = new Messages();
    }

    @Override
    public String sortByUserStateAndKind(User user, String value) {
        boolean isReversed;
        switch (value) {
            case SortKindType.ASC -> isReversed = false;
            case SortKindType.DESC -> isReversed = true;
            default -> {
                return messages.getMessageByKey("commons.bad-argument");
            }
        }

        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        if (contacts == null) {
            return messages.getMessageByKey("commons.empty");
        }
        Comparator<Contact> comparator;
        switch (user.getState()) {
            case SORT_NAME -> comparator = Comparator.comparing(contact -> contact.getName().toLowerCase());
            case SORT_AGE -> comparator = Comparator.comparing(Contact::getAge);
            default -> throw new RuntimeException(messages.getMessageByKey("state.type.not-exists"));
        }
        return contacts.stream()
                .sorted(isReversed ? comparator.reversed() : comparator)
                .map(Contact::getInfo)
                .collect(Collectors.joining("\n\n"));
    }

}

