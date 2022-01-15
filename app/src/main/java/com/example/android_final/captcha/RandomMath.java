package com.example.android_final.captcha;

import java.util.Random;

public class RandomMath {

    private static final Random RANDOM = new Random();

    public static String ShowEquation(int num1, int num2) {

        int chooseOperator = RANDOM.nextInt(3) + 1;
        switch(chooseOperator) {
            case 1:
                return "What is answer of " + num1 + " + " + num2;
            case 2:
                return "What is answer of " + num1 + " - " + num2;
            case 3:
                return "What is answer of " + num1 + " * " + num2;
        }
        return null;
    }

    public static int ShowResult(int num1, int num2, String equation) {
        if (equation.contains("+")) {
            return num1 + num2;
        } else if (equation.contains("-")) {
            return num1 - num2;
        } else if (equation.contains("*")) {
            return num1 * num2;
        }
        return 0;
    }
}