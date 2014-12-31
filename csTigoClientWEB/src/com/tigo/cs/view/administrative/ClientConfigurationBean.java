package com.tigo.cs.view.administrative;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;

import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.ClientParameter;
import com.tigo.cs.domain.ClientParameterValue;
import com.tigo.cs.domain.ClientParameterValuePK;
import com.tigo.cs.facade.ClientFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.ClientParameterFacade;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Tania Nuñez
 * @version $Id: ClientConfigurationBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "clientConfigurationBean")
@ViewScoped
public class ClientConfigurationBean extends AbstractCrudBeanClient<Client, ClientFacade> implements Serializable {

    private static final long serialVersionUID = -2423526178458053550L;
    @EJB
    private ClientFileFacade clientFileFacade;
    @EJB
    private ClientParameterValueFacade clientParameterValueFacade;
    @EJB
    private ClientParameterFacade clientParameterFacade;
    private Client client;
    private ClientFile clientFile;
    private String filenameChr;
    private String selectedDurationFormat;

    public ClientConfigurationBean() {
        super(Client.class, ClientFacade.class);
    }

    @Override
    public String saveEditing() {

        /*
         * agregamos el ClientFile a la base de datos
         */

        if (clientFile != null && clientFile.getClientFileCod() == null) {

            ClientParameterValuePK pk = new ClientParameterValuePK();
            pk.setCodClient(getClient().getClientCod());
            pk.setCodClientParameter("client.image.logo");

            ClientParameterValue clientParamValue = clientParameterValueFacade.find(pk);

            if (clientParamValue == null) {
                ClientParameter clientParam = clientParameterFacade.find("client.image.logo");

                clientParamValue = new ClientParameterValue();
                clientParamValue.setClientParameterValuePK(pk);
                clientParamValue.setClientParameter(clientParam);
                clientParamValue.setClient(getClient());

                clientParamValue.setValueChr(clientFile.getFilenameChr());
                clientParameterValueFacade.create(clientParamValue);
            } else {
                ClientFile oldLogo = clientFileFacade.getClientLogo(getClient());
                clientFileFacade.remove(oldLogo);

                clientParamValue.setValueChr(clientFile.getFilenameChr());
                clientParameterValueFacade.edit(clientParamValue);
            }

            clientFile = clientFileFacade.create(clientFile);

//            this.reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
//            setClientFile(null);
//            setFilenameChr(null);
//            return null;
        }

        /*
         * Se guarda el parametro para el formato de la duracion de Visitas
         */
        ClientParameterValuePK pk = new ClientParameterValuePK();
        pk.setCodClient(getClient().getClientCod());
        pk.setCodClientParameter("client.duration.format");

        ClientParameterValue clientParamValue = clientParameterValueFacade.find(pk);

        if (clientParamValue == null) {
            ClientParameter clientParam = clientParameterFacade.find("client.duration.format");

            clientParamValue = new ClientParameterValue();
            clientParamValue.setClientParameterValuePK(pk);
            clientParamValue.setClientParameter(clientParam);
            clientParamValue.setClient(getClient());

            clientParamValue.setValueChr(selectedDurationFormat);
            clientParameterValueFacade.create(clientParamValue);
        } else {
            clientParamValue.setValueChr(selectedDurationFormat);
            clientParameterValueFacade.edit(clientParamValue);
        }
        
        this.reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
        setClientFile(null);
        setFilenameChr(null);
        return null;

        // this.reset("web.client.backingBean.abstractCrudBean.message.ClientDoeNotHaveLogo");

    }

    @Override
    public String deleteEditing() {

        if (getFilenameChr() == null || getFilenameChr().equals("")) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.ClientDoeNotHaveLogo"));
            return null;
        }

        getFacade().deleteLogo(getClient());
//        this.reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
        setFilenameChr(null);
        setClientFile(null);
        return null;
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (event.getFile() != null) {
            clientFile = new ClientFile();
            clientFile.setFileByt(event.getFile().getContents());
            clientFile.setFilenameChr(event.getFile().getFileName().replaceAll(
                    " ", "_"));
            clientFile.setFiletypeChr(event.getFile().getContentType());
            clientFile.setDescriptionChr(event.getFile().getFileName().replaceAll(
                    " ", "_"));
            clientFile.setClient(getClient());
            clientFile.setUserweb(SecurityBean.getInstance().getLoggedInUserClient());
            setFilenameChr(event.getFile().getFileName().replaceAll(" ", "_"));
        }
    }

    public Client getClient() {
        if (client == null) {
            client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
        }
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientFile getClientFile() {
        if (clientFile == null) {
            clientFile = clientFileFacade.getClientLogo(getClient());
        }
        return clientFile;
    }

    public void setClientFile(ClientFile clientFile) {
        this.clientFile = clientFile;
    }

    public String getFilenameChr() {
        if (filenameChr == null) {
            ClientFile clientFile = getClientFile();
            if (clientFile != null) {
                filenameChr = clientFile.getFilenameChr();
            }
        }
        return filenameChr;
    }

    public void setFilenameChr(String filenameChr) {
        this.filenameChr = filenameChr;
    }

    public String getSelectedDurationFormat() {
        if (this.selectedDurationFormat == null) {
            this.selectedDurationFormat = clientParameterValueFacade.findByCode(
                    getClient().getClientCod(), "client.duration.format");

        }
        return selectedDurationFormat;
    }

    public void setSelectedDurationFormat(String selectedDurationFormat) {
        this.selectedDurationFormat = selectedDurationFormat;
    }

    @Override
    public String getReportWhereCriteria() {
        return "";
    }
}
