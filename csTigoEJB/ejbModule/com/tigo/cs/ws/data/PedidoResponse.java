package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class PedidoResponse {
    private int code;
    private String description;
    private PedidoCab[] results;

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

    public PedidoCab[] getResults() {
        return results;
    }

    public void setResults(PedidoCab[] results) {
        this.results = results;
    }

}
