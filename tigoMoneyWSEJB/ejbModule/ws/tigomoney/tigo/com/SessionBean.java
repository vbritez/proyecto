package ws.tigomoney.tigo.com;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.tigo.cs.commons.web.view.SMBaseBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: SessionBean.java 1858 2012-09-13 20:25:12Z miguel.maciel $
 */
@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean extends SMBaseBean {

	private boolean log4jConfigured = false;

	private static final long serialVersionUID = 9060250094658127837L;

	public SessionBean() {
	}

	public boolean isLog4jConfigured() {
		return log4jConfigured;
	}

	public void setLog4jConfigured(boolean log4jConfigured) {
		this.log4jConfigured = log4jConfigured;
	}

	public static SessionBean getInstance() {
		return (SessionBean) getBean("sessionBean");
	}

	public void signalPageAcces() {
		// notifier.signal(this.getClass(), Action.ACCESS,
		// SecurityBean.getInstance().getLoggedInUserClient(), null,
		// getCurrentViewId(), "Acceso a la pantalla.", getSessionId(),
		// getIpAddress());
	}

	/**
	 * M�todo utilizado en el componente
	 * <code>p:growl<code> para capturar mensajes desde el contexto de validaci�n
	 * (javax.validation.contraints) y evitar que el t�tulo y el detalle
	 * del mensaje sea el mismo, asignando el t�tulo el nivel de severidad (Error, Info, etc).
	 * */
	public boolean isSummaryEqualsToDetail() {
		List<FacesMessage> msgs = getFacesContext().getMessageList();
		for (FacesMessage facesMessage : msgs) {
			if (facesMessage.getSummary().equals(facesMessage.getDetail())) {
				facesMessage.setDetail(new String(facesMessage.getDetail()));
				String severityType = facesMessage.getSeverity().toString();
				facesMessage.setSummary(new String(severityType.substring(0,
						severityType.indexOf(" "))));
			}
		}
		return true;
	}

}
