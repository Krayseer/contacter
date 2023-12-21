package ru.anykeyers.domain;

import java.io.File;

/**
 * Сообщение для отправки<br/>
 * Для отправки данных могут использоваться
 * <ol>
 *     <li>Текст</li>
 *     <li>Файл</li>
 * </ol>
 */
public class Message {

    /**
     * Текст
     */
    private String text;

    /**
     * Файл
     */
    private File file;

    public Message(String text) {
        this.text = text;
    }

    public Message(File file) {
        this.file = file;
    }

    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }

}
