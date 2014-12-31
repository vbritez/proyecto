package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class RondaGuardiaResponse {
    private int code;
    private String description;
    private RondaGuardiaCabData[] results;

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

    public RondaGuardiaCabData[] getResults() {
        return results;
    }

    public void setResults(RondaGuardiaCabData[] results) {
        this.results = results;
    }
}
