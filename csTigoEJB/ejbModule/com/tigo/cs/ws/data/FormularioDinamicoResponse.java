package com.tigo.cs.ws.data;

public class FormularioDinamicoResponse {
	private int code;
	private String description;
	private FormularioDinamicoCab[] results;

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

	public FormularioDinamicoCab[] getResults() {
		return results;
	}

	public void setResults(FormularioDinamicoCab[] results) {
		this.results = results;
	}
}
