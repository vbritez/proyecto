package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class InventarioDAResponse {

    private int code;
    private String description;
    private InventarioDAData[] results;

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

    public InventarioDAData[] getResults() {
        return results;
    }

    public void setResults(InventarioDAData[] results) {
        this.results = results;
    }

}
