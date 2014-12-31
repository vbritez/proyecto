/**
 * Menu.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package py.gov.ips.consultas.servicios;

public class Menu  implements java.io.Serializable {
    private java.lang.String codCategoria;

    private java.lang.String codRetorno;

    private java.lang.String desCategoria;

    private java.lang.String desRetorno;

    private py.gov.ips.consultas.servicios.Menu[] listaRetorno;

    public Menu() {
    }

    public Menu(
           java.lang.String codCategoria,
           java.lang.String codRetorno,
           java.lang.String desCategoria,
           java.lang.String desRetorno,
           py.gov.ips.consultas.servicios.Menu[] listaRetorno) {
           this.codCategoria = codCategoria;
           this.codRetorno = codRetorno;
           this.desCategoria = desCategoria;
           this.desRetorno = desRetorno;
           this.listaRetorno = listaRetorno;
    }


    /**
     * Gets the codCategoria value for this Menu.
     * 
     * @return codCategoria
     */
    public java.lang.String getCodCategoria() {
        return codCategoria;
    }


    /**
     * Sets the codCategoria value for this Menu.
     * 
     * @param codCategoria
     */
    public void setCodCategoria(java.lang.String codCategoria) {
        this.codCategoria = codCategoria;
    }


    /**
     * Gets the codRetorno value for this Menu.
     * 
     * @return codRetorno
     */
    public java.lang.String getCodRetorno() {
        return codRetorno;
    }


    /**
     * Sets the codRetorno value for this Menu.
     * 
     * @param codRetorno
     */
    public void setCodRetorno(java.lang.String codRetorno) {
        this.codRetorno = codRetorno;
    }


    /**
     * Gets the desCategoria value for this Menu.
     * 
     * @return desCategoria
     */
    public java.lang.String getDesCategoria() {
        return desCategoria;
    }


    /**
     * Sets the desCategoria value for this Menu.
     * 
     * @param desCategoria
     */
    public void setDesCategoria(java.lang.String desCategoria) {
        this.desCategoria = desCategoria;
    }


    /**
     * Gets the desRetorno value for this Menu.
     * 
     * @return desRetorno
     */
    public java.lang.String getDesRetorno() {
        return desRetorno;
    }


    /**
     * Sets the desRetorno value for this Menu.
     * 
     * @param desRetorno
     */
    public void setDesRetorno(java.lang.String desRetorno) {
        this.desRetorno = desRetorno;
    }


    /**
     * Gets the listaRetorno value for this Menu.
     * 
     * @return listaRetorno
     */
    public py.gov.ips.consultas.servicios.Menu[] getListaRetorno() {
        return listaRetorno;
    }


    /**
     * Sets the listaRetorno value for this Menu.
     * 
     * @param listaRetorno
     */
    public void setListaRetorno(py.gov.ips.consultas.servicios.Menu[] listaRetorno) {
        this.listaRetorno = listaRetorno;
    }

    public py.gov.ips.consultas.servicios.Menu getListaRetorno(int i) {
        return this.listaRetorno[i];
    }

    public void setListaRetorno(int i, py.gov.ips.consultas.servicios.Menu _value) {
        this.listaRetorno[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Menu)) return false;
        Menu other = (Menu) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codCategoria==null && other.getCodCategoria()==null) || 
             (this.codCategoria!=null &&
              this.codCategoria.equals(other.getCodCategoria()))) &&
            ((this.codRetorno==null && other.getCodRetorno()==null) || 
             (this.codRetorno!=null &&
              this.codRetorno.equals(other.getCodRetorno()))) &&
            ((this.desCategoria==null && other.getDesCategoria()==null) || 
             (this.desCategoria!=null &&
              this.desCategoria.equals(other.getDesCategoria()))) &&
            ((this.desRetorno==null && other.getDesRetorno()==null) || 
             (this.desRetorno!=null &&
              this.desRetorno.equals(other.getDesRetorno()))) &&
            ((this.listaRetorno==null && other.getListaRetorno()==null) || 
             (this.listaRetorno!=null &&
              java.util.Arrays.equals(this.listaRetorno, other.getListaRetorno())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCodCategoria() != null) {
            _hashCode += getCodCategoria().hashCode();
        }
        if (getCodRetorno() != null) {
            _hashCode += getCodRetorno().hashCode();
        }
        if (getDesCategoria() != null) {
            _hashCode += getDesCategoria().hashCode();
        }
        if (getDesRetorno() != null) {
            _hashCode += getDesRetorno().hashCode();
        }
        if (getListaRetorno() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaRetorno());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaRetorno(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Menu.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "menu"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codCategoria");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codCategoria"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codRetorno");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codRetorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desCategoria");
        elemField.setXmlName(new javax.xml.namespace.QName("", "desCategoria"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desRetorno");
        elemField.setXmlName(new javax.xml.namespace.QName("", "desRetorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaRetorno");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listaRetorno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "menu"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
