package com.tigo.cs.listener;

import com.tigo.cs.facade.TmpWsresultFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
@WebListener
public class SessionControlListener implements HttpSessionListener {

    public static final String ATTR_NAME = "session_control";
    private static Map<String, List<String>> users = new HashMap<String, List<String>>();
    private static Map<String, String> sessions = new HashMap<String, String>();
    @EJB
    TmpWsresultFacade tmpWsresultFacade;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setAttribute(ATTR_NAME, this);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        removeTmpSessionCacheData(se.getSession().getId());
        removeSession(se.getSession().getId());
    }

    private void removeTmpSessionCacheData(String sessionId) {
        try {
            tmpWsresultFacade.removeSessionTmpCacheData(sessionId);
        } catch (Exception ex) {
        }
    }

    private void removeSession(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            String username = sessions.get(sessionId);

            users.get(username).remove(sessionId);
            sessions.remove(sessionId);
        }
    }

    public void addLoggedInUser(String username, String sessionId) {
        if (username != null && sessionId != null) {
            sessions.put(sessionId, username);

            if (!users.containsKey(username)) {
                users.put(username, new ArrayList<String>());
            }
            users.get(username).add(sessionId);
        }
    }

    public int openedSessionsCount(String username) {
        if (username == null || !users.containsKey(username)) {
            return 0;
        }
        return users.get(username).size();
    }

    public boolean hasOpenedSession(String username) {
        return openedSessionsCount(username) > 0;
    }
}
