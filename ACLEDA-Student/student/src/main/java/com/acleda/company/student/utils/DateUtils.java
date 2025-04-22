package com.acleda.company.student.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtils {

    public static ZoneId getDateTimeZoneOfTenant() {
        ZoneId zone = ZoneId.systemDefault();
        String tenant = "CAMBODIA";
        if (tenant != null) {
            zone = ZoneId.of("Asia/Phnom_Penh");
        }
        return zone;
    }

    public static TimeZone getTimeZoneOfTenant() {
        TimeZone zone = null;
        String tenant = "CAMBODIA";
        if (tenant != null) {
            zone = TimeZone.getTimeZone(getDateTimeZoneOfTenant());
        }
        return zone;
    }

    public static Date getDateOfTenant() {
        return Date.from(getLocalDateOfTenant().atStartOfDay(getDateTimeZoneOfTenant()).toInstant());
    }

    public static LocalDate getLocalDateOfTenant() {
        final ZoneId zone = getDateTimeZoneOfTenant();
        LocalDate today = LocalDate.now(zone);
        return today;
    }

    public static LocalDateTime getLocalDateTimeOfTenant() {
        final ZoneId zone = getDateTimeZoneOfTenant();
        LocalDateTime today = LocalDateTime.now(zone).truncatedTo(ChronoUnit.SECONDS);
        return today;
    }

    public static String formatToSqlDate(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(getTimeZoneOfTenant());
        final String formattedSqlDate = df.format(date);
        return formattedSqlDate;
    }

    public static String formatToSqlDate(final Date date, String format) {
        final DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(getTimeZoneOfTenant());
        final String formattedSqlDate = df.format(date);
        return formattedSqlDate;
    }

    public static String formatToSqlDateTime(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
        df.setTimeZone(getTimeZoneOfTenant());
        final String formattedSqlDate = df.format(date);
        return formattedSqlDate;
    }

    public static boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(getLocalDateOfTenant());
    }

    public static LocalDateTime parseLocalDateTime(String strDateTime) {
//      String str = "2022-02-23 21:41";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime formatDateTime = LocalDateTime.parse(strDateTime, formatter);
        return formatDateTime;
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDateTime.format(formatter);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }
}

