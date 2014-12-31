package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class AsistenciaResponse {

    private int code;
    private String description;
    private AsistenciaCabData[] results;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AsistenciaCabData[] getResults() {
        return results;
    }

    public void setResults(AsistenciaCabData[] results) {
        this.results = results;
    }

}
