package com.tigo.cs.ws.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Miguel Zorrilla
 */
public class DataConf {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd");
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(
            "yyyyMMdd HHmmss");
    public static final String ERROR_KEY_WORD = "ERROR";

    public static final SimpleDateFormat DATETIME_PATTERN1 = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm");

    public static String getDateAsString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String getDatetimeAsString(Date date) {
        return DATETIME_FORMAT.format(date);
    }

    public static String standarizeDateTime(SimpleDateFormat sdf, String date) {
        try {
            return DATETIME_FORMAT.format(sdf.parse(date));
        } catch (Exception e) {
            return null;
        }

    }

    public static String standarizeDateTime(String pattern, String date) {
        try {
            return DATETIME_FORMAT.format(new SimpleDateFormat(pattern).parse(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String parse(String toParse) {
        if (toParse == null) {
            return "";
        }
        return toParse;
    }

    public static <T> List<T> parse(List<T> list) {
        if (list == null) {
            return new ArrayList<T>();
        }
        return list;
    }

}
