/**
 * RetornoAgendamientoMedico.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package py.gov.ips.consultas.servicios;

public class RetornoAgendamientoMedico  implements java.io.Serializable {
    private java.lang.Integer codError;

    private java.lang.String desError;

    private py.gov.ips.consultas.servicios.Menu[] listaMenu;

    private java.lang.String nomApe;

    private java.lang.String nroCi;

    private java.lang.Long token;

    public RetornoAgendamientoMedico() {
    }

    public RetornoAgendamientoMedico(
           java.lang.Integer codError,
           java.lang.String desError,
           py.gov.ips.consultas.servicios.Menu[] listaMenu,
           java.lang.String nomApe,
           java.lang.String nroCi,
           java.lang.Long token) {
           this.codError = codError;
           this.desError = desError;
           this.listaMenu = listaMenu;
           this.nomApe = nomApe;
           this.nroCi = nroCi;
           this.token = token;
    }


    /**
     * Gets the codError value for this RetornoAgendamientoMedico.
     * 
     * @return codError
     */
    public java.lang.Integer getCodError() {
        return codError;
    }


    /**
     * Sets the codError value for this RetornoAgendamientoMedico.
     * 
     * @param codError
     */
    public void setCodError(java.lang.Integer codError) {
        this.codError = codError;
    }


    /**
     * Gets the desError value for this RetornoAgendamientoMedico.
     * 
     * @return desError
     */
    public java.lang.String getDesError() {
        return desError;
    }


    /**
     * Sets the desError value for this RetornoAgendamientoMedico.
     * 
     * @param desError
     */
    public void setDesError(java.lang.String desError) {
        this.desError = desError;
    }


    /**
     * Gets the listaMenu value for this RetornoAgendamientoMedico.
     * 
     * @return listaMenu
     */
    public py.gov.ips.consultas.servicios.Menu[] getListaMenu() {
        return listaMenu;
    }


    /**
     * Sets the listaMenu value for this RetornoAgendamientoMedico.
     * 
     * @param listaMenu
     */
    public void setListaMenu(py.gov.ips.consultas.servicios.Menu[] listaMenu) {
        this.listaMenu = listaMenu;
    }

    public py.gov.ips.consultas.servicios.Menu getListaMenu(int i) {
        return this.listaMenu[i];
    }

    public void setListaMenu(int i, py.gov.ips.consultas.servicios.Menu _value) {
        this.listaMenu[i] = _value;
    }


    /**
     * Gets the nomApe value for this RetornoAgendamientoMedico.
     * 
     * @return nomApe
     */
    public java.lang.String getNomApe() {
        return nomApe;
    }


    /**
     * Sets the nomApe value for this RetornoAgendamientoMedico.
     * 
     * @param nomApe
     */
    public void setNomApe(java.lang.String nomApe) {
        this.nomApe = nomApe;
    }


    /**
     * Gets the nroCi value for this RetornoAgendamientoMedico.
     * 
     * @return nroCi
     */
    public java.lang.String getNroCi() {
        return nroCi;
    }


    /**
     * Sets the nroCi value for this RetornoAgendamientoMedico.
     * 
     * @param nroCi
     */
    public void setNroCi(java.lang.String nroCi) {
        this.nroCi = nroCi;
    }


    /**
     * Gets the token value for this RetornoAgendamientoMedico.
     * 
     * @return token
     */
    public java.lang.Long getToken() {
        return token;
    }


    /**
     * Sets the token value for this RetornoAgendamientoMedico.
     * 
     * @param token
     */
    public void setToken(java.lang.Long token) {
        this.token = token;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RetornoAgendamientoMedico)) return false;
        RetornoAgendamientoMedico other = (RetornoAgendamientoMedico) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codError==null && other.getCodError()==null) || 
             (this.codError!=null &&
              this.codError.equals(other.getCodError()))) &&
            ((this.desError==null && other.getDesError()==null) || 
             (this.desError!=null &&
              this.desError.equals(other.getDesError()))) &&
            ((this.listaMenu==null && other.getListaMenu()==null) || 
             (this.listaMenu!=null &&
              java.util.Arrays.equals(this.listaMenu, other.getListaMenu()))) &&
            ((this.nomApe==null && other.getNomApe()==null) || 
             (this.nomApe!=null &&
              this.nomApe.equals(other.getNomApe()))) &&
            ((this.nroCi==null && other.getNroCi()==null) || 
             (this.nroCi!=null &&
              this.nroCi.equals(other.getNroCi()))) &&
            ((this.token==null && other.getToken()==null) || 
             (this.token!=null &&
              this.token.equals(other.getToken())));
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
        if (getCodError() != null) {
            _hashCode += getCodError().hashCode();
        }
        if (getDesError() != null) {
            _hashCode += getDesError().hashCode();
        }
        if (getListaMenu() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaMenu());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaMenu(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNomApe() != null) {
            _hashCode += getNomApe().hashCode();
        }
        if (getNroCi() != null) {
            _hashCode += getNroCi().hashCode();
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RetornoAgendamientoMedico.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "retornoAgendamientoMedico"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codError");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("desError");
        elemField.setXmlName(new javax.xml.namespace.QName("", "desError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaMenu");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listaMenu"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "menu"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nomApe");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nomApe"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroCi");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nroCi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("", "token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
