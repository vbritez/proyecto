package com.tigo.cs.ws.data;

/**
 * 
 * @author Miguel Zorrilla
 */
public class PedidoDet {

    long id;
    String producto;
    String cantidad;
    String descuento;
    String precio;
    String total;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = DataConf.parse(cantidad);
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = DataConf.parse(descuento);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = DataConf.parse(precio);
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = DataConf.parse(producto);
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = DataConf.parse(total);
    }
}
