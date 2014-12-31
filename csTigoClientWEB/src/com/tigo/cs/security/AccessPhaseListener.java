package com.tigo.cs.security;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: AccessPhaseListener.java 91 2011-11-28 20:16:12Z javier.kovacs
 *          $
 */
public class AccessPhaseListener implements PhaseListener {

    private static final long serialVersionUID = -6838984525188562323L;

    @Override
    public void afterPhase(PhaseEvent phaseEvent) {
        PhaseId currPhaseId = phaseEvent.getPhaseId();
        if (currPhaseId.equals(PhaseId.RESTORE_VIEW)) {
            verify(phaseEvent);
        }
    }

    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
        PhaseId currPhaseId = phaseEvent.getPhaseId();
        if (currPhaseId.equals(PhaseId.RENDER_RESPONSE)) {
            verify(phaseEvent);
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    protected void verify(PhaseEvent phaseEvent) {
        FacesContext facesContext = phaseEvent.getFacesContext();
        String viewId = facesContext.getViewRoot().getViewId();
        SecurityBean securityBean = SecurityBean.getInstance();
        
        /*
         * Primero se valida que la sesión no haya caducado
         * */
        if (SecurityBean.isSessionExpired(facesContext)) {
            try {
                doRedirect(facesContext, "/resources/errorpages/errorSessionExpired.jsp");
                facesContext.responseComplete();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
         
        }else if (!securityBean.verifyPageAccess(viewId)) {
            /*
             * Luego se valida que no se esté accediendo a un recurso sin autenticación
             * */
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            try {
                response.sendError(403); // 403: Forbidden error
                facesContext.responseComplete();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        

    }

    public void doRedirect(FacesContext fc, String redirectPage) {
        ExternalContext ec = fc.getExternalContext();        
        try {           
            ec.redirect(ec.getRequestContextPath() + (redirectPage != null ? redirectPage : ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
