package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class FlotaResponse {

    private int code;
    private String description;
    private FlotaCabData[] results;

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

    public FlotaCabData[] getResults() {
        return results;
    }

    public void setResults(FlotaCabData[] results) {
        this.results = results;
    }

}
