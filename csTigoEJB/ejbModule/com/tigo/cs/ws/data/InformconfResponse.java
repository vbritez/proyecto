package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class InformconfResponse {

    private int code;
    private String description;
    private InformconfData[] results;

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

    public InformconfData[] getResults() {
        return results;
    }

    public void setResults(InformconfData[] results) {
        this.results = results;
    }

}
