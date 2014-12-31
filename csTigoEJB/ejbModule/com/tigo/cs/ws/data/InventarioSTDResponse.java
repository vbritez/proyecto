package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class InventarioSTDResponse {

    private int code;
    private String description;
    private InventarioStdData[] results;

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

    public InventarioStdData[] getResults() {
        return results;
    }

    public void setResults(InventarioStdData[] results) {
        this.results = results;
    }

}
