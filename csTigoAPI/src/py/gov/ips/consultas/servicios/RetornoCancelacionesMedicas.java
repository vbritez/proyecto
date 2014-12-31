/**
 * RetornoCancelacionesMedicas.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package py.gov.ips.consultas.servicios;

public class RetornoCancelacionesMedicas  implements java.io.Serializable {
    private java.lang.Integer codEmpresa;

    private java.lang.Integer codError;

    private java.lang.String desError;

    private java.util.Calendar fechaCita;

    private java.lang.String nombreEmpresa;

    private java.lang.String nombreEspecialidad;

    private java.lang.String nombreMedico;

    private java.lang.String nombrePaciente;

    private java.lang.Long nroAgend;

    private java.lang.Integer nroRegAmb;

    private java.lang.Long token;

    private java.lang.String turnoCita;

    public RetornoCancelacionesMedicas() {
    }

    public RetornoCancelacionesMedicas(
           java.lang.Integer codEmpresa,
           java.lang.Integer codError,
           java.lang.String desError,
           java.util.Calendar fechaCita,
           java.lang.String nombreEmpresa,
           java.lang.String nombreEspecialidad,
           java.lang.String nombreMedico,
           java.lang.String nombrePaciente,
           java.lang.Long nroAgend,
           java.lang.Integer nroRegAmb,
           java.lang.Long token,
           java.lang.String turnoCita) {
           this.codEmpresa = codEmpresa;
           this.codError = codError;
           this.desError = desError;
           this.fechaCita = fechaCita;
           this.nombreEmpresa = nombreEmpresa;
           this.nombreEspecialidad = nombreEspecialidad;
           this.nombreMedico = nombreMedico;
           this.nombrePaciente = nombrePaciente;
           this.nroAgend = nroAgend;
           this.nroRegAmb = nroRegAmb;
           this.token = token;
           this.turnoCita = turnoCita;
    }


    /**
     * Gets the codEmpresa value for this RetornoCancelacionesMedicas.
     * 
     * @return codEmpresa
     */
    public java.lang.Integer getCodEmpresa() {
        return codEmpresa;
    }


    /**
     * Sets the codEmpresa value for this RetornoCancelacionesMedicas.
     * 
     * @param codEmpresa
     */
    public void setCodEmpresa(java.lang.Integer codEmpresa) {
        this.codEmpresa = codEmpresa;
    }


    /**
     * Gets the codError value for this RetornoCancelacionesMedicas.
     * 
     * @return codError
     */
    public java.lang.Integer getCodError() {
        return codError;
    }


    /**
     * Sets the codError value for this RetornoCancelacionesMedicas.
     * 
     * @param codError
     */
    public void setCodError(java.lang.Integer codError) {
        this.codError = codError;
    }


    /**
     * Gets the desError value for this RetornoCancelacionesMedicas.
     * 
     * @return desError
     */
    public java.lang.String getDesError() {
        return desError;
    }


    /**
     * Sets the desError value for this RetornoCancelacionesMedicas.
     * 
     * @param desError
     */
    public void setDesError(java.lang.String desError) {
        this.desError = desError;
    }


    /**
     * Gets the fechaCita value for this RetornoCancelacionesMedicas.
     * 
     * @return fechaCita
     */
    public java.util.Calendar getFechaCita() {
        return fechaCita;
    }


    /**
     * Sets the fechaCita value for this RetornoCancelacionesMedicas.
     * 
     * @param fechaCita
     */
    public void setFechaCita(java.util.Calendar fechaCita) {
        this.fechaCita = fechaCita;
    }


    /**
     * Gets the nombreEmpresa value for this RetornoCancelacionesMedicas.
     * 
     * @return nombreEmpresa
     */
    public java.lang.String getNombreEmpresa() {
        return nombreEmpresa;
    }


    /**
     * Sets the nombreEmpresa value for this RetornoCancelacionesMedicas.
     * 
     * @param nombreEmpresa
     */
    public void setNombreEmpresa(java.lang.String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }


    /**
     * Gets the nombreEspecialidad value for this RetornoCancelacionesMedicas.
     * 
     * @return nombreEspecialidad
     */
    public java.lang.String getNombreEspecialidad() {
        return nombreEspecialidad;
    }


    /**
     * Sets the nombreEspecialidad value for this RetornoCancelacionesMedicas.
     * 
     * @param nombreEspecialidad
     */
    public void setNombreEspecialidad(java.lang.String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }


    /**
     * Gets the nombreMedico value for this RetornoCancelacionesMedicas.
     * 
     * @return nombreMedico
     */
    public java.lang.String getNombreMedico() {
        return nombreMedico;
    }


    /**
     * Sets the nombreMedico value for this RetornoCancelacionesMedicas.
     * 
     * @param nombreMedico
     */
    public void setNombreMedico(java.lang.String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }


    /**
     * Gets the nombrePaciente value for this RetornoCancelacionesMedicas.
     * 
     * @return nombrePaciente
     */
    public java.lang.String getNombrePaciente() {
        return nombrePaciente;
    }


    /**
     * Sets the nombrePaciente value for this RetornoCancelacionesMedicas.
     * 
     * @param nombrePaciente
     */
    public void setNombrePaciente(java.lang.String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }


    /**
     * Gets the nroAgend value for this RetornoCancelacionesMedicas.
     * 
     * @return nroAgend
     */
    public java.lang.Long getNroAgend() {
        return nroAgend;
    }


    /**
     * Sets the nroAgend value for this RetornoCancelacionesMedicas.
     * 
     * @param nroAgend
     */
    public void setNroAgend(java.lang.Long nroAgend) {
        this.nroAgend = nroAgend;
    }


    /**
     * Gets the nroRegAmb value for this RetornoCancelacionesMedicas.
     * 
     * @return nroRegAmb
     */
    public java.lang.Integer getNroRegAmb() {
        return nroRegAmb;
    }


    /**
     * Sets the nroRegAmb value for this RetornoCancelacionesMedicas.
     * 
     * @param nroRegAmb
     */
    public void setNroRegAmb(java.lang.Integer nroRegAmb) {
        this.nroRegAmb = nroRegAmb;
    }


    /**
     * Gets the token value for this RetornoCancelacionesMedicas.
     * 
     * @return token
     */
    public java.lang.Long getToken() {
        return token;
    }


    /**
     * Sets the token value for this RetornoCancelacionesMedicas.
     * 
     * @param token
     */
    public void setToken(java.lang.Long token) {
        this.token = token;
    }


    /**
     * Gets the turnoCita value for this RetornoCancelacionesMedicas.
     * 
     * @return turnoCita
     */
    public java.lang.String getTurnoCita() {
        return turnoCita;
    }


    /**
     * Sets the turnoCita value for this RetornoCancelacionesMedicas.
     * 
     * @param turnoCita
     */
    public void setTurnoCita(java.lang.String turnoCita) {
        this.turnoCita = turnoCita;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RetornoCancelacionesMedicas)) return false;
        RetornoCancelacionesMedicas other = (RetornoCancelacionesMedicas) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codEmpresa==null && other.getCodEmpresa()==null) || 
             (this.codEmpresa!=null &&
              this.codEmpresa.equals(other.getCodEmpresa()))) &&
            ((this.codError==null && other.getCodError()==null) || 
             (this.codError!=null &&
              this.codError.equals(other.getCodError()))) &&
            ((this.desError==null && other.getDesError()==null) || 
             (this.desError!=null &&
              this.desError.equals(other.getDesError()))) &&
            ((this.fechaCita==null && other.getFechaCita()==null) || 
             (this.fechaCita!=null &&
              this.fechaCita.equals(other.getFechaCita()))) &&
            ((this.nombreEmpresa==null && other.getNombreEmpresa()==null) || 
             (this.nombreEmpresa!=null &&
              this.nombreEmpresa.equals(other.getNombreEmpresa()))) &&
            ((this.nombreEspecialidad==null && other.getNombreEspecialidad()==null) || 
             (this.nombreEspecialidad!=null &&
              this.nombreEspecialidad.equals(other.getNombreEspecialidad()))) &&
            ((this.nombreMedico==null && other.getNombreMedico()==null) || 
             (this.nombreMedico!=null &&
              this.nombreMedico.equals(other.getNombreMedico()))) &&
            ((this.nombrePaciente==null && other.getNombrePaciente()==null) || 
             (this.nombrePaciente!=null &&
              this.nombrePaciente.equals(other.getNombrePaciente()))) &&
            ((this.nroAgend==null && other.getNroAgend()==null) || 
             (this.nroAgend!=null &&
              this.nroAgend.equals(other.getNroAgend()))) &&
            ((this.nroRegAmb==null && other.getNroRegAmb()==null) || 
             (this.nroRegAmb!=null &&
              this.nroRegAmb.equals(other.getNroRegAmb()))) &&
            ((this.token==null && other.getToken()==null) || 
             (this.token!=null &&
              this.token.equals(other.getToken()))) &&
            ((this.turnoCita==null && other.getTurnoCita()==null) || 
             (this.turnoCita!=null &&
              this.turnoCita.equals(other.getTurnoCita())));
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
        if (getCodEmpresa() != null) {
            _hashCode += getCodEmpresa().hashCode();
        }
        if (getCodError() != null) {
            _hashCode += getCodError().hashCode();
        }
        if (getDesError() != null) {
            _hashCode += getDesError().hashCode();
        }
        if (getFechaCita() != null) {
            _hashCode += getFechaCita().hashCode();
        }
        if (getNombreEmpresa() != null) {
            _hashCode += getNombreEmpresa().hashCode();
        }
        if (getNombreEspecialidad() != null) {
            _hashCode += getNombreEspecialidad().hashCode();
        }
        if (getNombreMedico() != null) {
            _hashCode += getNombreMedico().hashCode();
        }
        if (getNombrePaciente() != null) {
            _hashCode += getNombrePaciente().hashCode();
        }
        if (getNroAgend() != null) {
            _hashCode += getNroAgend().hashCode();
        }
        if (getNroRegAmb() != null) {
            _hashCode += getNroRegAmb().hashCode();
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        if (getTurnoCita() != null) {
            _hashCode += getTurnoCita().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RetornoCancelacionesMedicas.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "retornoCancelacionesMedicas"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codEmpresa");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codEmpresa"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("fechaCita");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fechaCita"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreEmpresa");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombreEmpresa"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreEspecialidad");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombreEspecialidad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombreMedico");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombreMedico"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombrePaciente");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nombrePaciente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroAgend");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nroAgend"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroRegAmb");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nroRegAmb"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("turnoCita");
        elemField.setXmlName(new javax.xml.namespace.QName("", "turnoCita"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
