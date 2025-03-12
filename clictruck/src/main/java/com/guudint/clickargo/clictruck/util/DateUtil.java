package com.guudint.clickargo.clictruck.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.guudint.clickargo.clictruck.constant.DateFormat;

public class DateUtil {

    private static Logger LOG = Logger.getLogger(DateUtil.class);

    private Date date;
    private SimpleDateFormat sdf;

    public DateUtil() {
    }

    public DateUtil(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date add(int param, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(param, value);
        date = calendar.getTime();
        return date;
    }

    public LocalDate getDateOnly() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date toDate(String date, String format) {
        try {
            sdf = new SimpleDateFormat(format);
            return sdf.parse(date);
        } catch (ParseException e) {
            LOG.error(e);
        }
        return null;
    }

    public String toStringFormat(String format) {
        sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public Date getDefaultEndDate() {
        return toDate("2040/12/31", DateFormat.Java.YYYY_MM_DD);
    }

    public String getMonth() {
        if (date == null) {
            return "00";
        }
        sdf = new SimpleDateFormat("MM");
        String month = sdf.format(date);
        return month.startsWith("0") ? month.replaceFirst("0", "") : month;
    }

    public String getYear() {
        if (date == null) {
            return "0000";
        }
        sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }
}
