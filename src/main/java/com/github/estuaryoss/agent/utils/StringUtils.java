package com.github.estuaryoss.agent.utils;

public class StringUtils {
    private static final int FIELD_MAX_SIZE = 255;


    public static String trimString(String input) {
        if (input == null) return "";
        if (input.length() == 0) return input;

        return input.substring(0, Math.min(input.length(), FIELD_MAX_SIZE));
    }
}
