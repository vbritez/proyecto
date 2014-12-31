package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class VisitaPedidoResponse {
    private int code;
    private String description;
    private VisitaVPData[] results;

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

    public VisitaVPData[] getResults() {
        return results;
    }

    public void setResults(VisitaVPData[] results) {
        this.results = results;
    }

}
