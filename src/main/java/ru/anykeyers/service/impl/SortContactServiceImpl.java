package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.SortContactService;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link SortContactService}
 */
public class SortContactServiceImpl implements SortContactService {

    private final ContactRepository contactRepository;

    public SortContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Set<Contact> sortByUserStateAndKind(User user, StateInfo userStateInfo, String value) {
        Comparator<Contact> comparator = getSortingComparator(userStateInfo.getState(), getSortingOrder(value));
        Set<Contact> contacts = contactRepository.findByUsername(user.getUsername());
        return contacts.stream()
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Получить тип сортировки
     *
     * @param value тип аргумента
     * @return {@code true}, если нужно по убыванию и {@code false} если по возрастанию
     */
    private boolean getSortingOrder(String value) {
        return switch (value) {
            case SortDirectionKind.ASCENDING -> false;
            case SortDirectionKind.DESCENDING -> true;
            default -> throw new BadArgumentException();
        };
    }

    /**
     * Получить компаратор для сравнения контактов
     *
     * @param state состояние пользователя
     * @param reversed тип сортировки
     */
    private Comparator<Contact> getSortingComparator(State state, boolean reversed) {
        return switch (state) {
            case SORT_NAME -> reversed
                        ? Comparator.comparing(contact -> contact.getName().toLowerCase(), Comparator.nullsLast(Comparator.reverseOrder()))
                        : Comparator.comparing(contact -> contact.getName().toLowerCase());
            case SORT_AGE -> reversed
                        ? Comparator.comparing(Contact::getAge, Comparator.nullsLast(Comparator.reverseOrder()))
                        : Comparator.comparing(Contact::getAge, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> throw new InvalidUserStateException();
        };
    }

}
