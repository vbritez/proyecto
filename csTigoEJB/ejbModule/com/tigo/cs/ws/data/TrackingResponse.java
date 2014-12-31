package com.tigo.cs.ws.data;

public class TrackingResponse {
	private int code;
    private String description;
    private TrackingData[] results;

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

    public TrackingData[] getResults() {
        return results;
    }

    public void setResults(TrackingData[] results) {
        this.results = results;
    }
	
}
