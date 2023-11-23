package ru.anykeyers;

import ru.anykeyers.commands.CommandProcessor;
import ru.anykeyers.contexts.Messages;
import ru.anykeyers.factories.ReceiverFactory;
import ru.anykeyers.factories.RepositoryFactory;
import ru.anykeyers.receivers.Receiver;
import ru.anykeyers.repositories.ContactRepository;
import ru.anykeyers.repositories.GroupRepository;
import ru.anykeyers.repositories.UserRepository;
import ru.anykeyers.services.*;

/**
 * Класс, позволяющий запустить приложение
 */
public class ContacterApplication {

    private final Receiver receiver;

    private final CommandProcessor commandProcessor;

    private final Messages messages;

    public ContacterApplication() {
        messages = new Messages();

        ReceiverFactory receiverFactory = new ReceiverFactory();
        RepositoryFactory repositoryFactory = new RepositoryFactory();

        receiver = receiverFactory.createReceiver();
        UserRepository userRepository = repositoryFactory.createUserRepository();
        ContactRepository contactRepository = repositoryFactory.createContactRepository();
        GroupRepository groupRepository = repositoryFactory.createGroupRepository();

        AuthenticationService authenticationService = new AuthenticationService(userRepository);
        ContactService contactService = new ContactService(contactRepository);
        GroupService groupService = new GroupService(groupRepository, contactRepository);
        SearchService contactSearch = new SearchService(contactRepository);
        FilterService filterService = new FilterService(contactRepository);
        SortService sortService = new SortService(contactRepository);

        commandProcessor = new CommandProcessor(authenticationService, contactService,
                groupService, contactSearch, filterService, sortService);
    }

    /**
     * Запуск приложения
     */
    public void start() {
        receiver.sendMessage(messages.getMessageByKey("application.welcome"));
        while (true) {
            String command = receiver.readCommand();
            String resultMessage = commandProcessor.processCommand(command);
            receiver.sendMessage(resultMessage);
        }
    }

}
