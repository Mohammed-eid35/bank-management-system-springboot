package com.alien.bank.management.system.utils;

import com.alien.bank.management.system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class Utils {

    private static final Random RANDOM = new Random();

    public static String generateCardNumber() {
        return generateRandomNumber(16);
    }

    public static String generateCVV() {
        return generateRandomNumber(3);
    }

    private static String generateRandomNumber(int len) {
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < len; ++i) {
            int digit = RANDOM.nextInt(10);
            number.append(digit);
        }

        return number.toString();
    }
}
