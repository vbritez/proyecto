package com.tigo.cs.view;

import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.tigo.cs.commons.web.view.SMBaseBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "downloadAppsBean")
@RequestScoped
public class DownloadAppsBean extends SMBaseBean {

    private static final long serialVersionUID = -4845556363187375053L;
    
    private StreamedContent file; 

    /**
     * Creates a new instance of LoginBean
     */
    public DownloadAppsBean() {         
    }

    public StreamedContent getFile() {  
        if (file == null){
            InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/images/csTigoAndroid.apk");  
            file = new DefaultStreamedContent(stream, "application/vnd.android.package-archive", "csTigoAndroid.apk"); 
        }
        return file;  
    }

    
    
}
