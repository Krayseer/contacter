package ru.anykeyers.processor.state.impl;

import ru.anykeyers.common.Messages;
import ru.anykeyers.domain.StateInfo;
import ru.anykeyers.domain.entity.User;
import ru.anykeyers.exception.group.GroupNotExistsException;
import ru.anykeyers.processor.state.BaseStateProcessor;
import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.kinds.GroupEditActionKind;
import ru.anykeyers.service.GroupService;
import ru.anykeyers.service.UserStateService;

import java.util.List;

/**
 * Обработчик состояний для групп
 */
public class GroupStateProcessor extends BaseStateProcessor {

    private final Messages messages = Messages.getInstance();

    private final GroupService groupService;

    public GroupStateProcessor(UserStateService userStateService,
                               GroupService groupService) {
        super(userStateService);
        this.groupService = groupService;
        registerStatesHandlers();
    }

    /**
     * Регистрация состояний и их обработчиков
     */
    private void registerStatesHandlers() {
        registerHandler(State.ADD_GROUP, (user, groupName) -> {
            try {
                groupService.addGroup(user, groupName);
            } catch (Exception ex) {
                return ex.getMessage();
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return messages.getMessageByKey("group.successful-save", groupName);
        });
        registerHandler(State.EDIT_GROUP, (user, groupNameToEdit) -> {
            if (!groupService.existsGroup(user, groupNameToEdit)) {
                return messages.getMessageByKey("group.exception.name.not-exists", groupNameToEdit);
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.setEditInfo(groupNameToEdit);
            userStateInfo.setState(State.EDIT_GROUP_FIELD);
            return messages.getMessageByKey("group.state.edit.todo");
        });
        registerHandler(State.EDIT_GROUP_FIELD, (user, field) -> {
            switch (field.toLowerCase()) {
                case GroupEditActionKind.NAME -> {
                    return processEditGroup(user, State.EDIT_GROUP_NAME, "group.state.edit.name");
                }
                case GroupEditActionKind.ADD_CONTACT -> {
                    return processEditGroup(user, State.EDIT_GROUP_ADD_CONTACT, "group.state.edit.contact.add");
                }
                case GroupEditActionKind.DELETE_CONTACT -> {
                    return processEditGroup(user, State.EDIT_GROUP_DELETE_CONTACT, "group.state.edit.contact.delete");
                }
            }
            return messages.getMessageByKey("exception.argument.invalid");
        });
        List<State> editGroupFieldsStates = List.of(
                State.EDIT_GROUP_NAME, State.EDIT_GROUP_ADD_CONTACT, State.EDIT_GROUP_DELETE_CONTACT
        );
        editGroupFieldsStates.forEach(state -> registerHandler(state, (user, newValue) -> {
            StateInfo userStateInfo = userStateService.getUserState(user);
            String groupName = userStateInfo.getEditInfo();
            try {
                groupService.editGroup(user, userStateInfo, newValue);
            } catch (Exception ex){
                return ex.getMessage();
            }
            userStateInfo.clear();
            return messages.getMessageByKey("group.successful-edit", groupName);
        }));
        registerHandler(State.DELETE_GROUP, (user, groupName) -> {
            try {
                groupService.deleteGroup(user, groupName);
            } catch (GroupNotExistsException ex) {
                return ex.getMessage();
            }
            StateInfo userStateInfo = userStateService.getUserState(user);
            userStateInfo.clear();
            return messages.getMessageByKey("group.successful-delete", groupName);
        });
    }

    /**
     * Изменение состояния группы
     *
     * @param user пользователь
     * @param state состояние, сигнализирующее, что нужно изменить у группы
     * @param messageKey ключ сообщения успешного выполнения операции
     * @return результат изменения группы
     */
    private String processEditGroup(User user, State state, String messageKey) {
        StateInfo userStateInfo = userStateService.getUserState(user);
        userStateInfo.setState(state);
        return messages.getMessageByKey(messageKey);
    }

}
