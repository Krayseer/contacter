package ru.anykeyers.domain;

import java.util.List;
import java.util.Objects;

/**
 * Сущность группы
 */
public class Group {

    /**
     * Название группы
     */
    private String name;

    /**
     * Список контактов, добавленных в группу
     */
    private List<Contact> members;

    public Group(String name, List<Contact> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<Contact> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        StringBuilder membersStr = new StringBuilder();
        for (Contact member : members) {
            membersStr.append(member.getFirstname()).append(" ").append(member.getLastname()).
                    append(" ").append(member.getPhoneNumber()).append(",");
        }
        return name + ": " + membersStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name) && Objects.equals(members, group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<Contact> members) {
        this.members = members;
    }
}
