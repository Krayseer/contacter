package ru.anykeyers.service.impl.contact;

import ru.anykeyers.utils.EnumUtils;
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

    private final EnumUtils enumUtils = new EnumUtils();

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
        Enum<FilterAgeKind> enumKindByField = enumUtils.getEnumKindByField(FilterAgeKind.values(), kind);
        if (enumKindByField.equals(FilterAgeKind.GREATER_THAN)) {
            return contact -> contact.getAge() != null && contact.getAge() > contactAge;
        } else if (enumKindByField.equals(FilterAgeKind.LESS_THAN)) {
            return contact -> contact.getAge() != null && contact.getAge() < contactAge;
        } else if (enumKindByField.equals(FilterAgeKind.EQUALS)) {
            return contact -> contact.getAge() != null && contact.getAge() == contactAge;
        }
        throw new BadArgumentException();
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
        Enum<FilterGenderKind> enumKindByField = enumUtils.getEnumKindByField(FilterGenderKind.values(), kind);
        if (enumKindByField.equals(FilterGenderKind.MALE)) {
            return contact -> contact.getGender() != null
                    && contact.getGender().equals(Gender.MAN);
        } else if (enumKindByField.equals(FilterGenderKind.FEMALE)) {
            return contact -> contact.getGender() != null
                    && contact.getGender().equals(Gender.WOMAN);
        }
        throw new BadArgumentException();
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
        Enum<FilterBlockKind> enumKindByField = enumUtils.getEnumKindByField(FilterBlockKind.values(), kind);
        if (enumKindByField.equals(FilterBlockKind.BLOCKED)) {
            return Contact::isBlocked;
        } else if (enumKindByField.equals(FilterBlockKind.NON_BLOCKED)) {
            return contact -> !contact.isBlocked();
        }
        throw new BadArgumentException();
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
