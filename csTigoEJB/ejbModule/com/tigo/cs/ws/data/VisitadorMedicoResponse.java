package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class VisitadorMedicoResponse {
    private int code;
    private String description;
    private VisitadorMedicoCabData[] results;

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

    public VisitadorMedicoCabData[] getResults() {
        return results;
    }

    public void setResults(VisitadorMedicoCabData[] results) {
        this.results = results;
    }
}
