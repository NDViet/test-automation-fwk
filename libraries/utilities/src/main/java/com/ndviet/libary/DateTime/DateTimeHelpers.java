package com.ndviet.libary.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.ndviet.libary.configuration.ConfigurationHelpers.getSystemLocale;

public class DateTimeHelpers {
    public static Map getCurrentQuarterYear() {
        LocalDate localDate = LocalDate.now();
        int quarter = localDate.get(IsoFields.QUARTER_OF_YEAR);
        int year = localDate.getYear();
        Map<String, String> returnDate = new LinkedHashMap<>();
        returnDate.put("Year", year + "");
        returnDate.put("Quarter", quarter + "");
        return returnDate;
    }

    public static String getCurrentDateInFormat(String format, int increase) {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, increase);
        Date date = calendar.getTime();
        return new SimpleDateFormat(format, getSystemLocale()).format(date);
    }
}
