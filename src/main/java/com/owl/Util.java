package com.owl;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    private static final SimpleDateFormat logFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static String uiFormat(Date date) {
        return logFormat.format(date);
    }

    public static String logPrefix() {
        return "[" + logFormat.format(new Date()) + "] ";
    }

}
