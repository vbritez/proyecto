package com.tigo.cs.android.util;

import java.io.Serializable;
import java.util.Map;

import com.tigo.cs.android.helper.domain.OperationEntity;

/**
 * Esta clase se utiliza para encapsular todos los parametros a tener en cuenta
 * para un request via POST a un metodo de nuestro WS REST
 * 
 * @author lthmii
 * 
 */
public class PostRESTRequestData implements Serializable {

    private static final long serialVersionUID = -1127064761108851636L;
    /**
     * la URL a verificar antes de enviar nuestro request al servidor. Si la URL
     * a verificar esta disponible, tambien deberia estarlo el resto de los
     * servicios
     */
    private String verifyURL;

    /**
     * los datos a enviar a el webmethod
     */
    private String data;
    /**
     * nombre del webmethod a invocar en el request
     */
    private String webMethodName;
    /**
     * los datos a enviar en el encabezado del request http
     */
    private Map<String, String> headerData;
    /**
     * URL del WS a invocar
     */
    private String webServiceURL;

    private OperationEntity operationEntity;

    public String getVerifyURL() {
        return verifyURL;
    }

    public void setVerifyURL(String verifyURL) {
        this.verifyURL = verifyURL;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getWebMethodName() {
        return webMethodName;
    }

    public void setWebMethodName(String webMethodName) {
        this.webMethodName = webMethodName;
    }

    public Map<String, String> getHeaderData() {
        return headerData;
    }

    public void setHeaderData(Map<String, String> headerData) {
        this.headerData = headerData;
    }

    public String getWebServiceURL() {
        return webServiceURL;
    }

    public void setWebServiceURL(String webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public OperationEntity getOperationEntity() {
        return operationEntity;
    }

    public void setOperationEntity(OperationEntity operationEntity) {
        this.operationEntity = operationEntity;
    }

}
