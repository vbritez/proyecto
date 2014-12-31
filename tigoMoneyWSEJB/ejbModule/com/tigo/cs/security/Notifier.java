package com.tigo.cs.security;



import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;


import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ExceptionUtility;

@Singleton
public class Notifier {
        
    public Notifier() {  
    }  
    
    public void error(String consumer, Class<?> origin, String msisdn, String message, Exception e) {
    	prepareAndSignal(consumer, msisdn, origin, Action.ERROR, message, e);
    }
    
    public void info(String consumer, Class<?> origin, String msisdn, String message) {
    	prepareAndSignal(consumer, msisdn, origin, Action.ERROR, message, null);
    }

    
    public void signal(String consumer,Class ownerClass, String msisdn, Action action, String message) {
        try {
           
          
            // PERFORM LOG
            log(consumer,ownerClass, msisdn, action, message, null, null);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(String consumer,Class ownerClass, String msisdn, Action action, String message, Exception e) {
        try {
            message += "\n".concat(ExceptionUtility.getStackTrace(e));

            // PERFORM LOG
            log(consumer,ownerClass, msisdn, action, message, null, null);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(String consumer,Class ownerClass, String msisdn, Action action, String message, String sessionId, String ipAddress) {
        try {

            // PERFORM LOG
            log(consumer,ownerClass, msisdn, action, message, sessionId, ipAddress);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }


    public void signal(String consumer,Class ownerClass, String msisdn, Action action, String message, String sessionId, String ipAddress, Exception e) {
        message += "\n".concat(ExceptionUtility.getStackTrace(e));
        log(consumer,ownerClass, msisdn, action, message, sessionId, ipAddress);
    }


    private void log(String consumer, Class ownerClass, String msisdn, Action action, String message, String sessionId, String ipAddress) {
    	String userName = "";
    	userName = consumer != null ? consumer : "";
//        screenPage = (screenPage == null) ? "" : "{page: ".concat(screenPage).concat("}");
        sessionId = (sessionId == null) ? "" : "{session id: ".concat(sessionId).concat("}");
        ipAddress = (ipAddress == null) ? "" : "{ip address: ".concat(ipAddress).concat("}");
            switch (action) {
            case CREATE:
            case READ:
            case UPDATE:
            case DELETE:
            case REPORT:
            case ACCESS:
                Logger.getLogger(ownerClass.getName()).log(Level.INFO, message.concat(msisdn));
                break;
            case DEBUG:
                Logger.getLogger(ownerClass.getName()).log(Level.WARNING, message.concat(userName));
                break;
            case INFO:
                Logger.getLogger(ownerClass.getName()).log(Level.INFO, message.concat(userName));
                break;
            case WARNING:
                Logger.getLogger(ownerClass.getName()).log(Level.WARNING, message.concat(userName));
                break;
            case ERROR:
                Logger.getLogger(ownerClass.getName()).log(Level.SEVERE, message.concat(userName));
                break;
            }
    }
    
    private void prepareAndSignal(String consumer, String msisdn, Class ownerClass, Action action, String message, Exception e) {
        String except = "";
        String method = "";
        String cause = "";
        String stackTrace = "";

        if (e != null) {
            except = "\nException: " + e.getClass().getCanonicalName();
            if (e.getStackTrace().length > 0) {
                StackTraceElement ste = e.getStackTrace()[0];
                method = "\nAt: " + ste.getClassName() + "."
                    + ste.getMethodName() + "(" + ste.getFileName() + ":"
                    + ste.getLineNumber() + ")";

                String pattern = "[{0}] {1}\n";
                for (StackTraceElement s : e.getStackTrace()) {
                    stackTrace = stackTrace.concat(MessageFormat.format(
                            pattern, msisdn, s.toString()));
                }

            }
            try {
                cause = "\nCaused by: "
                    + e.getCause().getClass().getCanonicalName()
                    + ", with message: " + e.getCause().getMessage();
            } catch (Exception ex) {
                cause = ""; // e.getCause() is null or there is not any message
                // for the cause.
            }
        }

        String owner = (ownerClass == null) ? "" : " (".concat(ownerClass.getSimpleName().concat(
                ")"));
        if (action == Action.ERROR || action == Action.FATAL) {
            owner = (ownerClass == null) ? "" : "\nFrom Class: ".concat(ownerClass.getCanonicalName());
        }
        if (message == null) {
            if (e != null && e.getMessage() != null) {
                message = e.getMessage();
            } else {
                message = "";
            }
        } else {
            if (e != null && e.getMessage() != null) {
                message += ". With Overwritten message: " + e.getMessage();
            }
        }

        String msg = message + except + method + cause + owner + "\n"
            + stackTrace;
        try {
            if (message == null || (message != null && message.length() > 0)) {
                    log(consumer, ownerClass, msisdn, action, msg, null, null);
            }
        } catch (Exception ex) {
        	log(consumer, ownerClass, msisdn, action, msg, null, null);
        }


    }

    

    public void logWarn(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass.getName()).log(Level.WARNING, "[".concat(method).concat("] ").concat(message));
    }

    public void logInfo(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass.getName()).log(Level.INFO, "[".concat(method).concat("] ").concat(message));
    }

    public void logError(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass.getName()).log(Level.SEVERE, "[".concat(method).concat("] ").concat(message));
    }
}