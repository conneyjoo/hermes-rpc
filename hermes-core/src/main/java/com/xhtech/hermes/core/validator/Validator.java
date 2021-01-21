package com.xhtech.hermes.core.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Jack on 2017/1/18 0018.
 */
public class Validator {
    public static void assertNotNull(Object o, String msg){
        if(null == o){
            throw new IllegalArgumentException(msg);
        }
    }

    public static void assertNotEmptyStr(String str, String msg){
        if(null == str || str.trim().length()<=0){
            throw new IllegalArgumentException(msg);
        }
    }

    public static void assertLong(String str, String msg){
        try{
            Long.valueOf(str);
        }catch (NumberFormatException e){
            throw new IllegalArgumentException(msg, e);
        }
    }

    public static void assertDateTimeType(String strDate, String msg){
        if (!strDate.matches("\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}")) {
            throw new IllegalArgumentException(msg);
        }

        try {
            new SimpleDateFormat("yyyyMMddHHmmss").parse(strDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(msg, e);
        }
    }

    public static void main(String[] args){
        assertDateTimeType("20170118132121", "invalid datetime str.");
        assertDateTimeType("201701181321211", "invalid datetime str.");
    }
}
