package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class DeliveryResponse {

    private int code;
    private String description;
    private DeliveryCabData[] results;

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

    public DeliveryCabData[] getResults() {
        return results;
    }

    public void setResults(DeliveryCabData[] results) {
        this.results = results;
    }

}
