package com.tigo.cs.ws.data;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 
 * @author Miguel Zorrilla
 */
public class MedicoCabData {

    long id;
    String codMedico;
    String latitud;
    String longitud;
    @XStreamImplicit(itemFieldName = "medico_detalle")
    List<MedicoDetData> detalles;
    
    @XStreamImplicit(itemFieldName = "producto")
    List<ProductoMedData> productos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodMedico() {
        return codMedico;
    }

    public void setCodMedico(String codMedico) {
        this.codMedico = codMedico;
    }

    public List<MedicoDetData> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<MedicoDetData> detalles) {
        this.detalles = detalles;
    }

    public List<ProductoMedData> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoMedData> productos) {
        this.productos = productos;
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

}
