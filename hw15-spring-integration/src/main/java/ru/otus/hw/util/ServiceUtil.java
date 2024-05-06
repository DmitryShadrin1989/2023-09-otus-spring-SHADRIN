package ru.otus.hw.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUtil {

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
