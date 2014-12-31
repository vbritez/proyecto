package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ExceptionUtility;
import com.tigo.cs.domain.Auditory;
import com.tigo.cs.domain.AuditoryAction;
import com.tigo.cs.domain.Screen;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Useradmin;
import com.tigo.cs.domain.Userweb;

public abstract class NotifierAPI extends AbstractAPI<String> {

    // private static Map<String, Boolean> appenderCreatedMap = new
    // HashMap<String, Boolean>();

    public NotifierAPI() {
        super(String.class);
    }

    public void signal(Class ownerClass, String msisdn, Action action, String message) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            // PERFORM AUDIT
            audit(auditoryAction, null, null, null, message, null, null);
            // PERFORM LOG
            log(auditoryAction, msisdn, ownerClass, action, null, null, null,
                    message, null, null);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, String message) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            // PERFORM AUDIT
            audit(auditoryAction, null, null, null, message, null, null);
            // PERFORM LOG
            log(auditoryAction, null, ownerClass, action, null, null, null,
                    message, null, null);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, String message, String sessionId, String ipAddress) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            // PERFORM AUDIT
            audit(auditoryAction, null, null, null, message, sessionId,
                    ipAddress);
            // PERFORM LOG
            log(auditoryAction, null, ownerClass, action, null, null, null,
                    message, sessionId, ipAddress);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, String message, Exception e) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            message += "\n".concat(ExceptionUtility.getStackTrace(e));
            // PERFORM AUDIT
            audit(auditoryAction, null, null, null, message, null, null);
            // PERFORM LOG
            log(auditoryAction, null, ownerClass, action, null, null, null,
                    message, null, null);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, String screenPage, String message, String sessionId, String ipAddress) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            // PERFORM AUDIT
            audit(auditoryAction, null, null, screenPage, message, sessionId,
                    ipAddress);
            // PERFORM LOG
            log(auditoryAction, null, ownerClass, action, null, null,
                    screenPage, message, sessionId, ipAddress);
            // trap();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, Userweb userweb, Useradmin adminUser, String screenPage, String message, String sessionId, String ipAddress) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);

            // PERFORM AUDIT
            // if ((adminUser == null && !screenPage.equals("/login.xhtml"))
            // || (userweb == null && !screenPage.equals("/loginclient.xhtml")))
            // {
            // System.out.println(i18nBean.iValue("ejb.admin.notifier.message.NullUser"));
            // }
            audit(auditoryAction, userweb, adminUser, screenPage, message,
                    sessionId, ipAddress);
            // PERFORM LOG
            log(auditoryAction, null, ownerClass, action, userweb, adminUser,
                    screenPage, message, sessionId, ipAddress);
            // trap();

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void signal(Class ownerClass, Action action, Userweb userweb, Useradmin adminUser, String screenPage, String message, String sessionId, String ipAddress, Exception e) {
        message += "\n".concat(ExceptionUtility.getStackTrace(e));
        signal(ownerClass, action, null, adminUser, screenPage, message,
                sessionId, ipAddress);
    }

    private void audit(AuditoryAction auditoryAction, Userweb userweb, Useradmin adminUser, String screenPage, String message, String sessionId, String ipAddress) {

        if (auditoryAction.getAuditable()) {
            if (message.length() >= 2000) {
                message = message.substring(0, 1999);
            }
            Auditory auditory = new Auditory();
            auditory.setActionChr(auditoryAction.getMessage());
            auditory.setDescriptionChr(message);
            auditory.setIpAddress(ipAddress);
            auditory.setSessionId(sessionId);
            auditory.setUseradmin(adminUser);
            auditory.setUserweb(userweb);
            Screen screen = null;
            try {
                screen = getFacadeContainer().getScreenAPI().findScreenByPage(
                        screenPage);
            } catch (MoreThanOneResultException e) {
                e.printStackTrace();
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }

            Screenclient screenClient = null;
            try {
                screenClient = getFacadeContainer().getScreenClientAPI().findByPage(
                        screenPage);
            } catch (MoreThanOneResultException e) {
                e.printStackTrace();
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }
            if (screen != null) {
                auditory.setScreen(screen);
            }

            if (screenClient != null) {
                auditory.setScreenClient(screenClient);
            }

            getFacadeContainer().getAuditoryAPI().create(auditory);
        }
    }

    private void log(AuditoryAction auditoryAction, String msisdn, Class ownerClass, Action action, Userweb userweb, Useradmin adminUser, String screenPage, String message, String sessionId, String ipAddress) {

        if (message == null) {
            return;
        }

        if (msisdn != null && msisdn.compareToIgnoreCase("null") != 0) {
            msisdn = MessageFormat.format("[{0}] ", msisdn);
        } else {
            msisdn = "";
        }

        // if (msisdn != null && msisdn.trim().length() > 0) {
        // Boolean appenderCreated = appenderCreatedMap.get(msisdn);
        // if (appenderCreated == null) {
        // FileAppender fa = new FileAppender();
        // fa.setName(msisdn);
        // fa.setFile(msisdn + ".log");
        // fa.setLayout(new PatternLayout("%d %p: %m%n"));
        // fa.setThreshold(Level.INFO);
        // fa.setAppend(true);
        // fa.activateOptions();
        // // add appender to any Logger (here is root)
        // Logger.getLogger("LOGFILE").addAppender(fa);
        // appenderCreatedMap.put(msisdn, true);
        // }
        // }

        if (auditoryAction.getLogable()) {
            switch (action) {
            case CREATE:
            case READ:
            case UPDATE:
            case DELETE:
            case REPORT:
            case ACCESS:
                String userName = (adminUser == null ? (userweb == null ? "" : "{userweb: ".concat(userweb.getNameChr())).concat("}") : "{adminUser: ".concat(adminUser.getNameChr())).concat("}");
                screenPage = (screenPage == null) ? "" : "{page: ".concat(
                        screenPage).concat("}");
                sessionId = (sessionId == null) ? "" : "{session id: ".concat(
                        sessionId).concat("}");
                ipAddress = (ipAddress == null) ? "" : "{ip address: ".concat(
                        ipAddress).concat("}");
                Logger.getLogger(ownerClass).log(
                        Level.INFO,
                        auditoryAction.getMessage().concat(
                                " -> ".concat(message)).concat(userName).concat(
                                screenPage).concat(sessionId).concat(ipAddress));
                break;
            case DEBUG:
                Logger.getLogger(ownerClass).log(Level.DEBUG,
                        msisdn.concat(message));
                // if (msisdn != null && msisdn.trim().length() > 0) {
                // Logger.getLogger(msisdn).log(Level.DEBUG,
                // msisdn.concat(message));
                // }
                break;
            case INFO:
                Logger.getLogger(ownerClass).log(Level.INFO,
                        msisdn.concat(message));
                // if (msisdn != null && msisdn.trim().length() > 0) {
                // Logger.getLogger(msisdn).log(Level.INFO,
                // msisdn.concat(message));
                // }
                break;
            case WARNING:
                Logger.getLogger(ownerClass).log(Level.WARN,
                        msisdn.concat(message));
                // if (msisdn != null && msisdn.trim().length() > 0) {
                // Logger.getLogger(msisdn).log(Level.WARN,
                // msisdn.concat(message));
                // }
                break;
            case ERROR:
                Logger.getLogger(ownerClass).log(Level.ERROR,
                        msisdn.concat(message));
                // if (msisdn != null && msisdn.trim().length() > 0) {
                // Logger.getLogger(msisdn).log(Level.ERROR,
                // msisdn.concat(message));
                // }
                break;
            }
        }
    }

    private void trap() {
        throw new UnsupportedOperationException("ej.admin.commons.exception.UnsupportedOperation");
    }

    public void logWarn(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass).log(Level.WARN,
                "[".concat(method).concat("] ").concat(message));
    }

    public void logInfo(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass).log(Level.INFO,
                "[".concat(method).concat("] ").concat(message));
    }

    public void logError(Class<?> ownerClass, String method, String message) {
        Logger.getLogger(ownerClass).log(Level.ERROR,
                "[".concat(method).concat("] ").concat(message));
    }

    // METHODS FOR LOGGING
    public void debug(Class<?> origin, String msisdn, String message) {
        signalExceptionWithoutAudit(origin, msisdn, Action.DEBUG, message,
                null, false);
    }

    public void info(Class<?> origin, String msisdn, String message) {
        signalExceptionWithoutAudit(origin, msisdn, Action.INFO, message, null,
                false);
    }

    public void info(Class<?> origin, String msisdn, String message, boolean alertReport) {
        signalExceptionWithoutAudit(origin, msisdn, Action.INFO, message, null,
                alertReport);
    }

    public void warn(Class<?> origin, String msisdn, String message) {
        signalExceptionWithoutAudit(origin, msisdn, Action.WARNING, message,
                null, false);
    }

    public void warn(Class<?> origin, String msisdn, String message, boolean alertReport) {
        signalExceptionWithoutAudit(origin, msisdn, Action.WARNING, message,
                null, alertReport);
    }

    public void error(Class<?> origin, String msisdn, String message, Exception e) {
        signalExceptionWithoutAudit(origin, msisdn, Action.ERROR, message, e,
                false);
    }

    public void error(Class<?> origin, String msisdn, String message, Exception e, boolean reportAlert) {
        signalExceptionWithoutAudit(origin, msisdn, Action.ERROR, message, e,
                reportAlert);
    }

    public void fatal(Class<?> origin, String msisdn, String message, Exception e) {
        signalExceptionWithoutAudit(origin, msisdn, Action.FATAL, message, e,
                false);
    }

    public void log(Class<?> origin, String msisdn, Action action, String message) {
        signalExceptionWithoutAudit(origin, msisdn, action, message, null,
                false);
    }

    public void log(Class<?> origin, String msisdn, Action action, String message, Exception e) {
        signalExceptionWithoutAudit(origin, msisdn, action, message, e, false);
    }

    public void signalExceptionWithoutAudit(Class<?> origin, String msisdn, Action action, String message, Exception e, boolean reportAlert) {
        AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                action);
        auditoryAction.setAuditable(false);
        prepareAndSignal(auditoryAction, msisdn, origin, action, message, e,
                reportAlert);
    }

    private void prepareAndSignal(AuditoryAction auditoryAction, String msisdn, Class ownerClass, Action action, String message, Exception e, boolean reportAlert) {
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

                if (auditoryAction.getLogable()) {
                    log(auditoryAction, msisdn, ownerClass, action, null, null,
                            null, msg, null, null);
                }
            } else {
                log(auditoryAction, msisdn, ownerClass, action, null, null,
                        null, msg, null, null);
            }
        } catch (Exception ex) {
            log(auditoryAction, msisdn, ownerClass, action, null, null, null,
                    msg, null, null);
        }

        if (reportAlert) {

        }

    }

    public Auditory signalMenuMovil(Class ownerClass, Action action, String message) {
        try {
            AuditoryAction auditoryAction = getFacadeContainer().getAuditoryActionAPI().findByAction(
                    action);
            // PERFORM AUDIT
            if (message.length() >= 2000) {
                message = message.substring(0, 1999);
            }
            Auditory auditory = new Auditory();
            auditory.setActionChr(auditoryAction.getMessage());
            auditory.setDescriptionChr(message);
            Auditory au = getFacadeContainer().getAuditoryAPI().create(auditory);
            return au;
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    public String verifyException(String user, Exception e) {

        if (e.getCause() != null
            && e.getCause().getClass().equals(RollbackException.class)) {

            RollbackException rbe = (RollbackException) e.getCause();

            if (rbe.getCause() != null
                && rbe.getCause().getClass().equals(PersistenceException.class)) {

                PersistenceException persistenceException = (PersistenceException) rbe.getCause();

                if (persistenceException.getCause() != null
                    && persistenceException.getCause().getClass().equals(
                            ConstraintViolationException.class)) {

                    ConstraintViolationException violationException = (ConstraintViolationException) persistenceException.getCause();

                    Iterator<ConstraintViolation<?>> iterator = violationException.getConstraintViolations().iterator();
                    String message = "";
                    while (iterator.hasNext()) {
                        ConstraintViolation<?> constraintViolation = iterator.next();

                        warn(getClass(),
                                null,
                                MessageFormat.format(
                                        "El mensaje de restriccion es: {0}. El valor con error es :{1}",
                                        constraintViolation.getMessage(),
                                        constraintViolation.getInvalidValue()));

                        message = constraintViolation.getMessage();
                        break;
                    }

                    warn(getClass(), null, message + " User: " + user);
                    return message;
                }
            }
        }
        signal(getClass(), Action.ERROR, e.getMessage() + " User: " + user);
        return null;

    }

}
