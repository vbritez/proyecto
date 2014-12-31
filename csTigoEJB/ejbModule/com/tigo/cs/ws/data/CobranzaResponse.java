package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class CobranzaResponse {

    private int code;
    private String description;
    private CobranzaCabData[] results;

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

    public CobranzaCabData[] getResults() {
        return results;
    }

    public void setResults(CobranzaCabData[] results) {
        this.results = results;
    }

}
