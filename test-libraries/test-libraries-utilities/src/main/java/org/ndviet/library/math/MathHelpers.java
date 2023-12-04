package org.ndviet.library.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import static org.ndviet.library.configuration.ConfigurationHelpers.getSystemLocale;

public class MathHelpers {

    private static final Logger LOGGER = LogManager.getLogger(MathHelpers.class);

    public static String numberDecimalFormat(String input, String decimal, String roundingMode) {
        Double number;
        if (isCreatable(input)) {
            number = createNumber(input);
        } else {
            LOGGER.error(input + " is not a Number");
            return input;
        }
        roundingMode = (roundingMode == null) ? "" : roundingMode;
        if (decimal != null) {
            LOGGER.info("Decimal format is enabled");
            RoundingMode rm;
            switch (roundingMode) {
                case "HALF_DOWN":
                    rm = RoundingMode.HALF_DOWN;
                    break;
                default:
                    rm = RoundingMode.HALF_UP;
                    break;
            }
            DecimalFormat df = new DecimalFormat(decimal, DecimalFormatSymbols.getInstance(getSystemLocale()));
            df.setRoundingMode(rm);
            String returnString = df.format(number);
            LOGGER.info("Decimal format: " + decimal + " - Final result: " + returnString);
            return returnString;
        } else {
            LOGGER.info("Decimal format is not set");
            return input;
        }
    }

    public static boolean isCreatable(String text) {
        try {
            Double number = createNumber(text);
            return number != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static Double createNumber(String text) {
        try {
            NumberFormat format = NumberFormat.getInstance(getSystemLocale());
            return format.parse(text).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException("Could not create number");
        }
    }

    public static int compareNumber(String text1, String text2) {
        Double value1 = createNumber(text1);
        Double value2 = createNumber(text2);
        return value1.compareTo(value2);
    }
}
