package com.tigo.cs.ws.data;

/**
 * 
 * @author Viviana Britez
 */
public class OTResponse {
    private int code;
    private String description;
    private OTCab[] results;

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

    public OTCab[] getResults() {
        return results;
    }

    public void setResults(OTCab[] results) {
        this.results = results;
    }

}
