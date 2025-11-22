package com.doitteam.foodstore.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");
    private static final Random random = new Random();

    private OrderNumberGenerator() {
        // Private constructor
    }

    public static String generate() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);
        int randomNum = random.nextInt(1000);

        return String.format("ORD-%s-%s-%03d", date, time, randomNum);
    }

    public static String generateWithPrefix(String prefix) {
        return prefix + "-" + generate();
    }
}