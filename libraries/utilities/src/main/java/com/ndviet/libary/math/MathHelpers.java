package com.ndviet.libary.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static com.ndviet.libary.configuration.ConfigurationHelpers.getSystemLocale;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class MathHelpers {

    private static final Logger LOGGER = LogManager.getLogger(MathHelpers.class);

    public static String numberDecimalFormat(String input, String decimal, String roundingMode) {
        Double number;
        if (isCreatable(input)) {
            number = Double.valueOf(input);
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
                case "HALF_UP":
                    rm = RoundingMode.HALF_UP;
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
}
