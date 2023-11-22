package ru.anykeyers.receivers.impl;

import ru.anykeyers.receivers.Receiver;

import java.util.Scanner;

/**
 * Консольный обработчик
 */
public class ConsoleReceiver implements Receiver {

    @Override
    public String readCommand() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

}
