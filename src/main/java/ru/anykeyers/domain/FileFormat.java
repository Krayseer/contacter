package ru.anykeyers.domain;

/**
 * Формат файла
 */
public enum FileFormat {

    /**
     * Формат .txt
     */
    TXT(".txt"),
    /**
     * Формат .xml
     */
    XML(".xml"),
    /**
     * Формат .csv
     */
    CSV(".csv"),
    /**
     * Формат .json
     */
    JSON(".json");

    private final String name;

    FileFormat(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
