package ru.anykeyers;

import java.io.IOException;
import java.util.Scanner;

/**
 * Исполнитель, запускающий приложение
 */
public class Executor {

    public static void main(String[] args) {
        ContacterApplication application = new ContacterApplication();
        application.start();
    }

}
