package org.aub.payzenapi.utils;

import java.util.Random;

public class RandomOtp {
    public String generateOtp() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
