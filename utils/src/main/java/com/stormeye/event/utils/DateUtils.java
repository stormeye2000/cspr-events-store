package com.stormeye.event.utils;

import org.joda.time.DateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

/**
 * Utility class to help with conversions of date formats
 *
 * @author ian@meywood.com
 */
public class DateUtils {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

    /***
     * Converts an ISO 8601 formatted date time to a java Data
     * @param isoDate the ISO 8601 date time to convert to a date
     * @return a java date
     */
    public static Date fromIso8601(final String isoDate) {
        return new DateTime(isoDate).toDate();
    }

    /***
     * Converts  a java date to an ISO 8601 formatted date time
     * @param date the date  to convert
     * @return an ISO 8601 date time string
     */
    public static String toIso8601(final Date date) {
        return formatter.format(date.toInstant());
    }
}
