/**
 * 
 */
package com.tigo.cs.ws.facade;

/**
 * Enumerados para codificar las respuestas de las operaciones del WS Upload de
 * datos.
 * 
 * @author Miguel Zorrilla
 * @since CS Fase 7
 */
public enum UploadResponseOperations {
    SUCCESS(0, "Exito", "client.data.ws.upload.SuccessMessage"),
    PARTIAL_SUCCESS(10, "Exito parcial", "client.data.ws.upload.PartialSuccess"),
    ERROR(100, "Error", "client.data.ws.upload.ErrorMessage");

    private final int code;
    private final String description;
    private final String message;

    private UploadResponseOperations(int code, String description, String message) {
        this.code = code;
        this.description = description;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

}
