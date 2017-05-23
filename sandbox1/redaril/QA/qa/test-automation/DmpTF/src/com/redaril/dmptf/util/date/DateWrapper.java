package com.redaril.dmptf.util.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateWrapper {

    public static String getPreviousDateDDMMYY(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("dd.MM.yy");
        return format.format(date);
    }

    public static String getPreviousDateDDMMYYYY(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);
    }

    public static String getPreviousDateyyyyMMddHmmss(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("yyyyMMddHmmss");
        return format.format(date);
    }

    public static String getPreviousDateEEEMMMddHHzyyyy(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        return format.format(date);
    }

    public static long getLongTime() {
        return new Date().getTime();
    }

    public static String getDateInStandardFormat() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -2);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("dd.MM.yy");
        return format.format(date);
    }

    public static String getPreviousDATEYYMMDD(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getRandom() {
        Date date = new Date();
        return String.valueOf(date.getTime());
    }

    public static Date getDate(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        return calendar.getTime();
    }

    public static String getDateWithTime(Integer i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, i);
        Date date = calendar.getTime();
        DateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return format.format(date);
    }
}