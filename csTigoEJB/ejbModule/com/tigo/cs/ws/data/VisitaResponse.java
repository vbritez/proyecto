package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class VisitaResponse {
    private int code;
    private String description;
    private VisitaData[] results;

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

    public VisitaData[] getResults() {
        return results;
    }

    public void setResults(VisitaData[] results) {
        this.results = results;
    }

}
