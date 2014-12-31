package com.tigo.cs.api.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class NCenterJmsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String resultCode;
    private String resultDescription;
    private String title;
    private HashMap<JMSProperties, String> additionalResult;
    private List<String> entries;

    public NCenterJmsResponse() {

    }

    public NCenterJmsResponse(boolean success, String resultCode, String resultDescription, HashMap<JMSProperties, String> additionalResult) {

        this.success = success;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.additionalResult = additionalResult;
    }

    /**
     * Ejecucion exitosa o no
     * 
     * @return
     */
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * Retorna el codigo de respuesta del validador
     * 
     * @return
     */
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Retorna una descripcion de la ejecucion del validador
     * 
     * @return
     */
    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    /**
     * Retorna un hashmap con parametros adicionales en caso de ser necesario
     * 
     * @return
     */
    public HashMap<JMSProperties, String> getAdditionalResult() {
        return additionalResult;
    }

    public void setAdditionalResult(HashMap<JMSProperties, String> additionalResult) {
        this.additionalResult = additionalResult;
    }

    /**
     * Titulo a mostrar en caso de ser un proceso de menu dinamico
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Lista de menus en case de ser un proceso de menu dinamico
     * 
     * @return
     */
    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
}
