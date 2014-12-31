package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class ArpResponse {

    private int code;
    private String description;
    private ArpCabData[] results;

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

    public ArpCabData[] getResults() {
        return results;
    }

    public void setResults(ArpCabData[] results) {
        this.results = results;
    }

}
