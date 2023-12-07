package ru.anykeyers.processors.states.impl;

import ru.anykeyers.contexts.Messages;
import ru.anykeyers.domain.User;
import ru.anykeyers.processors.states.BaseStateProcessor;
import ru.anykeyers.processors.states.domain.State;
import ru.anykeyers.domain.kinds.GroupEditKind;
import ru.anykeyers.services.AuthenticationService;
import ru.anykeyers.services.GroupService;

import java.util.List;

/**
 * Обработчик состояний для групп
 */
public class GroupStateProcessor extends BaseStateProcessor {

    private final GroupService groupService;

    private final AuthenticationService authenticationService;

    private final Messages messages;

    public GroupStateProcessor(AuthenticationService authenticationService,
                               GroupService groupService) {
        this.groupService = groupService;
        this.authenticationService = authenticationService;
        messages = new Messages();
        registerStatesHandlers();
    }

    /**
     * Регистрация состояний и их обработчиков
     */
    private void registerStatesHandlers() {
        registerHandler(State.ADD_GROUP, (user, groupName) -> {
            String result = groupService.addGroup(user, groupName);
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        });
        registerHandler(State.EDIT_GROUP, (user, groupNameToEdit) -> {
            if (!groupService.existsGroup(user, groupNameToEdit)) {
                return messages.getMessageByKey("group.not-exists", groupNameToEdit);
            }
            user.setEditInfo(groupNameToEdit);
            user.setState(State.EDIT_GROUP_FIELD);
            authenticationService.saveOrUpdateUser(user);
            return messages.getMessageByKey("group.state.edit.todo");
        });
        registerHandler(State.EDIT_GROUP_FIELD, (user, field) -> {
            switch (field.toLowerCase()) {
                case GroupEditKind.NAME -> {
                    return processEditGroup(user, State.EDIT_GROUP_NAME, "group.state.edit.name");
                }
                case GroupEditKind.ADD_CONTACT -> {
                    return processEditGroup(user, State.EDIT_GROUP_ADD_CONTACT, "group.state.edit.contact.add");
                }
                case GroupEditKind.DELETE_CONTACT -> {
                    return processEditGroup(user, State.EDIT_GROUP_DELETE_CONTACT, "group.state.edit.contact.delete");
                }
            }
            return messages.getMessageByKey("commons.bad-argument");
        });
        List<State> editGroupFieldsStates = List.of(
                State.EDIT_GROUP_NAME, State.EDIT_GROUP_ADD_CONTACT, State.EDIT_GROUP_DELETE_CONTACT
        );
        editGroupFieldsStates.forEach(state -> registerHandler(state, (user, newValue) -> {
            String result = groupService.editGroup(user, newValue);
            if (!result.equals(messages.getMessageByKey("group.successful-edit-name", user.getEditInfo()))) {
                return result;
            }
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        }));
        registerHandler(State.DELETE_GROUP, (user, groupName) -> {
            String result = groupService.deleteGroup(user, groupName);
            if (!result.equals(messages.getMessageByKey("group.successful-delete", groupName))) {
                return result;
            }
            user.clearState();
            authenticationService.saveOrUpdateUser(user);
            return result;
        });
    }

    /**
     * Изменение состояния группы
     * @param user пользователь
     * @param state состояние, сигнализирующее что нужно изменить у группы
     * @param messageKey ключ сообщения успешного выполнения операции
     * @return результат изменения группы
     */
    private String processEditGroup(User user, State state, String messageKey) {
        user.setState(state);
        authenticationService.saveOrUpdateUser(user);
        return messages.getMessageByKey(messageKey);
    }

}
