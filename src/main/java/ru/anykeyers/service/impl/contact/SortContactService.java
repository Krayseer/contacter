package ru.anykeyers.service.impl.contact;

import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.kinds.sort.SortDirectionKind;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис по сортировке контактов
 */
class SortContactService {

    /**
     * Отсортировать контакты по состоянию и типу действия
     *
     * @param contacts контакты
     * @param state    состояние пользователя
     * @param kind     тип сортировки
     * @return список контактов
     */
    Set<Contact> sortContacts(Set<Contact> contacts, State state, Enum<SortDirectionKind> kind) {
        Comparator<Contact> comparator = getSortingComparator(state, getSortingOrder(kind));
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
    private boolean getSortingOrder(Enum<SortDirectionKind> value) {
        if (value.equals(SortDirectionKind.ASCENDING)) {
            return false;
        } else if (value.equals(SortDirectionKind.DESCENDING)) {
            return true;
        }
        throw new BadArgumentException();
    }

    /**
     * Получить компаратор для сравнения контактов
     *
     * @param state    состояние пользователя
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
