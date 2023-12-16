package ru.anykeyers.processor.state.domain.mapper;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.Group;
import ru.anykeyers.common.Mapper;

/**
 * Маппер группы, для получения информации в человеко читаемом формате
 */
public class UiGroupMapper implements Mapper<Group> {

    private final Messages messages = Messages.getInstance();

    @Override
    public String format(Group group) {
        return messages.getMessageByKey("group.info.ui")
                .formatted(
                        group.getName(),
                        group.getContacts().size()
                );
    }

    @Override
    public Group parse(String str) {
        throw new UnsupportedOperationException();
    }

}
