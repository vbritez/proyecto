package com.tigo.cs.android.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Notifier {

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
    private final static Logger logger = Logger.getLogger(Notifier.class.getName());

    static public void setup() throws IOException {
        logger.setLevel(Level.INFO);
    }

    public static void info(Class ownerClass, String message) {
        String pattern = "{0} - {1}";
        message = MessageFormat.format(pattern,
                simpleDateFormat.format(new Date()), message);
        Logger.getLogger(ownerClass.getName()).log(Level.INFO, message);
    }

    public static void warning(Class ownerClass, String message) {
        String pattern = "{0} - {1}";
        message = MessageFormat.format(pattern,
                simpleDateFormat.format(new Date()), message);
        Logger.getLogger(ownerClass.getName()).log(Level.WARNING, message);
    }

    public static void error(Class ownerClass, String message) {
        String pattern = "{0} - {1}";
        message = MessageFormat.format(pattern,
                simpleDateFormat.format(new Date()), message);
        Logger.getLogger(ownerClass.getName()).log(Level.SEVERE, message);
    }

    public static void debug(Class ownerClass, String message) {
        String pattern = "{0} - {1}";
        message = MessageFormat.format(pattern,
                simpleDateFormat.format(new Date()), message);
        Logger.getLogger(ownerClass.getName()).log(Level.FINE, message);
    }

}
