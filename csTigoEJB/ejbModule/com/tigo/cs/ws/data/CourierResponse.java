package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class CourierResponse {
    private int code;
    private String description;
    private CourierCab[] results;

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

    public CourierCab[] getResults() {
        return results;
    }

    public void setResults(CourierCab[] results) {
        this.results = results;
    }

}
