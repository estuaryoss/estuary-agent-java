package com.github.estuaryoss.agent.utils;

public class StringUtils {

    public static String trimString(String input, int maxSize) {
        if (input == null) return "";
        if (input.length() == 0) return "";

        return input.substring(0, Math.min(input.length(), maxSize));
    }
}
