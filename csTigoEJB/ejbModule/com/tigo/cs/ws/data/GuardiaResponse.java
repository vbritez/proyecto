package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class GuardiaResponse {
    private int code;
    private String description;
    private GuardiaCabData[] results;

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

    public GuardiaCabData[] getResults() {
        return results;
    }

    public void setResults(GuardiaCabData[] results) {
        this.results = results;
    }
}
