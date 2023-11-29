package ru.anykeyers.repositories.file.data;

import ru.anykeyers.domain.Group;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Информация о группах
 */
public class GroupData implements ObjectData<Group> {

    private final Set<Group> groups;

    public GroupData() {
        groups = new HashSet<>();
    }

    @Override
    public Object getData() {
        return groups;
    }

    @Override
    public void addData(Group group) {
        groups.removeIf(currentContact -> Objects.equals(currentContact.getId(), group.getId()));
        groups.add(group);
    }

    @Override
    public void removeData(Group group) {
        groups.remove(group);
    }

}
