package ru.anykeyers.processor.state.domain.mapper;

import org.apache.commons.lang3.StringUtils;
import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.entity.Contact;
import ru.anykeyers.common.Mapper;

/**
 * Маппер контакта, для получения информации в человеко читаемом формате
 */
public class UiContactMapper implements Mapper<Contact> {

    private final Messages messages = Messages.getInstance();

    @Override
    public String format(Contact contact) {
        return messages.getMessageByKey("contact.info.ui")
                .formatted(
                    contact.getName(),
                    contact.getPhoneNumber() != null
                            ? contact.getPhoneNumber()
                            : StringUtils.EMPTY,
                    contact.getAge() != null
                            ? String.valueOf(contact.getAge())
                            : StringUtils.EMPTY,
                    contact.getGender() != null
                            ? contact.getGender().getName()
                            : StringUtils.EMPTY,
                    contact.isBlocked()
                            ? messages.getMessageByKey("commons.yes")
                            : messages.getMessageByKey("commons.no")
                );
    }

    @Override
    public Contact parse(String str) {
        throw new UnsupportedOperationException();
    }

}
