package com.sample.sample.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String TIME_FORMAT = "h:mm:ss a z";
    public static final String DATE_TIME_FORMAT = "MMMM dd, yyyy h:mm:ss a z";

    /*
     * Converts a date (double value) into the given format.
     */
    public static String getDateTimeFormat(double v, String format) {
        if (v == 0)
            return null;

        Date currenTimeZone = new Date((long)v * 1000);
        SimpleDateFormat dft = new SimpleDateFormat(format);

        try {
            String dateStr = dft.format(currenTimeZone);
            if (dateStr.startsWith("0") && dateStr.length() > 1)
                return dateStr.substring(1);

            return dateStr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
