package ru.anykeyers.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Утилитарный класс для работы с потоками ввода-вывода
 */
public final class StreamUtils {

    /**
     * Записать данные в поток ввода системы
     * @param data данные, которые нужно написать
     */
    public static void writeDataInSystemInputStream(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    /**
     * Установить системный OutputStream
     * @param outputStream поток вывода, который нужно установить
     */
    public static void setSystemOutputStream(OutputStream outputStream) {
        PrintStream ps = new PrintStream(outputStream);
        System.setOut(ps);
    }

}
