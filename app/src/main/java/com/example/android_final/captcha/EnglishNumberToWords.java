package com.example.android_final.captcha;

import java.text.DecimalFormat;

public class EnglishNumberToWords {
    private static final String[] TEN_NAMES = {"", " ten", " twenty", " thirty", " forty", " fifty"
            , " sixty", " seventy", " eighty", " ninety"};
    private static final String[] NUMBER_NAMES = {"", " one", " two", " three", " four", " five"
            , " six", " seven", " eight", " nine", " ten", " eleven", " twelves", " thirteen", " fourteen"
            , " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"};
    private static String convertLessThanOneThousand (int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = NUMBER_NAMES[number % 100];
            number /= 100;
        } else {
            soFar = NUMBER_NAMES[number % 10];
            number /= 10;
            soFar = TEN_NAMES[number % 10] + soFar;
            number /= 10;
        }

        if (number == 0) {
            return soFar;
        }

        return NUMBER_NAMES[number] + "hundred" + soFar;
    }

    public static String convert (long number) {
        if (number == 0) {
            return "zero";
        }

        String snumber = Long.toString(number);

        String mask = "000000000000";

        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        int billions = Integer.parseInt(snumber.substring(0, 3));

        int millions = Integer.parseInt(snumber.substring(3, 6));

        int hundredThousand = Integer.parseInt(snumber.substring(6, 9));

        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;

        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions) + " billion ";
        }
        String result = tradBillions;

        String tradMillions;

        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions) + " million ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions) + " million ";
        }

        result += tradMillions;

        String tradHunredThousands;

        switch (hundredThousand) {
            case 0:
                tradHunredThousands = "";
                break;
            case 1:
                tradHunredThousands = " one thousand ";
                break;
            default:
                tradHunredThousands = convertLessThanOneThousand(hundredThousand) + " thousand ";
        }

        result += tradHunredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result += tradThousand;

        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }
}