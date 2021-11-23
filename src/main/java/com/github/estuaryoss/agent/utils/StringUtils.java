package com.github.estuaryoss.agent.utils;

public class StringUtils {

    public static String trimString(String input, int maxSize) {
        if (input == null) return "";
        if (input.length() == 0) return "";

        if (input.length() < maxSize) return input;

        return input.substring(input.length() - maxSize);
    }
}
