package com.tigo.cs.security;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CacheControlPhaseListener.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
public class CacheControlPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 6468088967065046066L;

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        
//        response.addHeader("Pragma", "no-cache");
//        response.addHeader("Cache-Control", "no-cache");
//        response.addHeader("Cache-Control", "no-store");
//        response.addHeader("Cache-Control", "must-revalidate");
//        response.addHeader("Expires", "Sat, 1 Jan 2000 00:00:00 GMT");
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }
}
