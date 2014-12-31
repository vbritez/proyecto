/**
 * 
 */
package com.tigo.cs.ws.facade;


/**
 * @author Miguel Zorrilla
 *
 */
public class DataUploadResponse {

    Integer code;
    String description;
    String[] dataKeys;
    
    /**
     * 
     */
    public DataUploadResponse() {        
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getDataKeys() {
        return dataKeys;
    }

    public void setDataKeys(String[] dataKeys) {
        this.dataKeys = dataKeys;
    }    
    
}
