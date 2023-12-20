package ru.anykeyers.service.impl.contact;

import ru.anykeyers.common.Utils;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterAgeKind;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterBlockKind;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterGenderKind;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис по фильтрации контактов
 */
class FilterContactService {

    private final Utils utils = new Utils();

    /**
     * Отфильтровать контакты пользователя по возрасту
     *
     * @param kind тип фильтра по возрасту
     */
    Set<Contact> filterByAgeKind(Set<Contact> contacts, String userEditInfo, String kind) {
        int contactAgeFilter = Integer.parseInt(userEditInfo);
        Predicate<Contact> agePredicate = getAgePredicate(contactAgeFilter, kind);
        return applyFilter(contacts, agePredicate, Comparator.comparing(Contact::getAge));
    }

    /**
     * Получить фильтр по состоянию пользователя и выбранному типу фильтрации по возрасту
     *
     * @param contactAge возраст, по которому производится фильтрация
     * @param kind       тип фильтрации
     */
    private Predicate<Contact> getAgePredicate(int contactAge, String kind) {
        switch ((FilterAgeKind) utils.getEnumKindByField(FilterAgeKind.values(), kind)) {
            case GREATER_THAN -> {
                return contact -> contact.getAge() != null && contact.getAge() > contactAge;
            }
            case LESS_THAN -> {
                return contact -> contact.getAge() != null && contact.getAge() < contactAge;
            }
            case EQUALS -> {
                return contact -> contact.getAge() != null && contact.getAge() == contactAge;
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Отфильтровать контакты пользователя по полу
     */
    Set<Contact> filterByGender(Set<Contact> contacts, String kind) {
        Predicate<Contact> genderPredicate = getGenderPredicate(kind);
        return applyFilter(contacts, genderPredicate, Comparator.comparing(Contact::getGender));
    }

    /**
     * Получить фильтр по полу
     *
     * @param kind тип фильтра по полу
     */
    private Predicate<Contact> getGenderPredicate(String kind) {
        switch ((FilterGenderKind) utils.getEnumKindByField(FilterGenderKind.values(), kind)) {
            case MALE -> {
                return contact -> contact.getGender() != null
                        && contact.getGender().equals(Gender.MAN);
            }
            case FEMALE -> {
                return contact -> contact.getGender() != null
                        && contact.getGender().equals(Gender.WOMAN);
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Отфильтровать контакты пользователя по блокировке
     *
     * @param kind тип фильтра по блокировке
     */
    Set<Contact> filterByBlock(Set<Contact> contacts, String kind) {
        Predicate<Contact> blockPredicate = getBlockPredicate(kind);
        return applyFilter(contacts, blockPredicate, Comparator.comparing(Contact::isBlocked));
    }

    /**
     * Получить фильтр по блокировке
     *
     * @param kind тип фильтра по блокировке
     */
    private Predicate<Contact> getBlockPredicate(String kind) {
        switch ((FilterBlockKind) utils.getEnumKindByField(FilterBlockKind.values(), kind)) {
            case BLOCKED -> {
                return Contact::isBlocked;
            }
            case NON_BLOCKED -> {
                return contact -> !contact.isBlocked();
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Применить фильтр для поиска по контактам
     *
     * @param filterPredicate фильтр
     * @param contacts        контакты
     */
    private Set<Contact> applyFilter(Set<Contact> contacts,
                                     Predicate<Contact> filterPredicate,
                                     Comparator<Contact> comparator) {
        return Optional.ofNullable(contacts)
                .orElse(Set.of())
                .stream()
                .filter(filterPredicate)
                .sorted(Comparator.nullsLast(comparator))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
