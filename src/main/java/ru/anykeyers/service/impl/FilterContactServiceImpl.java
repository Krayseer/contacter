package ru.anykeyers.service.impl;

import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.domain.Gender;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterBlockKind;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterAgeKind;
import ru.anykeyers.processor.state.domain.kinds.filter.FilterGenderKind;
import ru.anykeyers.exception.BadArgumentException;
import ru.anykeyers.exception.InvalidUserStateException;
import ru.anykeyers.repository.ContactRepository;
import ru.anykeyers.service.FilterContactService;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link FilterContactService}
 */
public class FilterContactServiceImpl implements FilterContactService {

    private final ContactRepository contactRepository;

    public FilterContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Set<Contact> filterByUserStateAndKind(User user, StateInfo userStateInfo, String value) {
        switch (userStateInfo.getState()) {
            case FILTER_AGE_KIND -> {
                return filterByAgeKind(user, userStateInfo.getEditInfo(), value);
            }
            case FILTER_GENDER -> {
                return filterByGender(user, value);
            }
            case FILTER_BLOCK -> {
                return filterByBlock(user, value);
            }
            default -> throw new InvalidUserStateException();
        }
    }

    /**
     * Отфильтровать контакты пользователя по возрасту
     *
     * @param user пользователь
     * @param value тип фильтра по возрасту
     */
    private Set<Contact> filterByAgeKind(User user, String userEditInfo, String value) {
        int contactAgeFilter = Integer.parseInt(userEditInfo);
        Predicate<Contact> agePredicate = getAgePredicate(contactAgeFilter, value);
        return applyFilter(user, agePredicate, Comparator.comparing(Contact::getAge));
    }

    /**
     * Получить фильтр по состоянию пользователя и выбранному типу фильтрации по возрасту
     *
     * @param contactAge возраст, по которому производится фильтрация
     * @param value тип фильтрации
     */
    private Predicate<Contact> getAgePredicate(int contactAge, String value) {
        switch (value) {
            case FilterAgeKind.GREATER_THAN -> {
                return contact -> contact.getAge() != null && contact.getAge() > contactAge;
            }
            case FilterAgeKind.LESS_THAN -> {
                return contact -> contact.getAge() != null && contact.getAge() < contactAge;
            }
            case FilterAgeKind.EQUALS -> {
                return contact -> contact.getAge() != null && contact.getAge() == contactAge;
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Отфильтровать контакты пользователя по полу
     *
     * @param user пользователь
     * @param value тип фильтра по полу
     */
    private Set<Contact> filterByGender(User user, String value) {
        Predicate<Contact> genderPredicate = getGenderPredicate(value);
        return applyFilter(user, genderPredicate, Comparator.comparing(Contact::getGender));
    }

    /**
     * Получить фильтр по полу
     *
     * @param value тип фильтра по полу
     */
    private Predicate<Contact> getGenderPredicate(String value) {
        switch (value) {
            case FilterGenderKind.MALE -> {
                return contact -> contact.getGender() != null
                        && contact.getGender().equals(Gender.MAN);
            }
            case FilterGenderKind.FEMALE -> {
                return contact -> contact.getGender() != null
                        && contact.getGender().equals(Gender.WOMAN);
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Отфильтровать контакты пользователя по блокировке
     *
     * @param user пользователь
     * @param value тип фильтра по блокировке
     */
    private Set<Contact> filterByBlock(User user, String value) {
        Predicate<Contact> blockPredicate = getBlockPredicate(value);
        return applyFilter(user, blockPredicate, Comparator.comparing(Contact::isBlocked));
    }

    /**
     * Получить фильтр по блокировке
     *
     * @param value тип фильтра по блокировке
     */
    private Predicate<Contact> getBlockPredicate(String value) {
        switch (value) {
            case FilterBlockKind.BLOCKED -> {
                return Contact::isBlocked;
            }
            case FilterBlockKind.NON_BLOCKED -> {
                return contact -> !contact.isBlocked();
            }
            default -> throw new BadArgumentException();
        }
    }

    /**
     * Применить фильтр для поиска по контактам
     *
     * @param filterPredicate фильтр
     * @param user пользователь
     */
    private Set<Contact> applyFilter(User user, Predicate<Contact> filterPredicate, Comparator<Contact> comparator) {
        return Optional.ofNullable(contactRepository.findByUsername(user.getUsername()))
                .orElse(Set.of())
                .stream()
                .filter(filterPredicate)
                .sorted(Comparator.nullsLast(comparator))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
