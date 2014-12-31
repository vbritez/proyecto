/**
 * ServiciosConsultasSIHServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package py.gov.ips.consultas.servicios;

public class ServiciosConsultasSIHServiceLocator extends org.apache.axis.client.Service implements py.gov.ips.consultas.servicios.ServiciosConsultasSIHService {

    public ServiciosConsultasSIHServiceLocator() {
    }


    public ServiciosConsultasSIHServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiciosConsultasSIHServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ServiciosConsultasSIHPort
    private java.lang.String ServiciosConsultasSIHPort_address = "http://10.20.11.133:8080/WsConsultasSIH/ServiciosConsultasSIH";

    public java.lang.String getServiciosConsultasSIHPortAddress() {
        return ServiciosConsultasSIHPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServiciosConsultasSIHPortWSDDServiceName = "ServiciosConsultasSIHPort";

    public java.lang.String getServiciosConsultasSIHPortWSDDServiceName() {
        return ServiciosConsultasSIHPortWSDDServiceName;
    }

    public void setServiciosConsultasSIHPortWSDDServiceName(java.lang.String name) {
        ServiciosConsultasSIHPortWSDDServiceName = name;
    }

    public py.gov.ips.consultas.servicios.ServiciosConsultasSIH getServiciosConsultasSIHPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ServiciosConsultasSIHPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiciosConsultasSIHPort(endpoint);
    }

    public py.gov.ips.consultas.servicios.ServiciosConsultasSIH getServiciosConsultasSIHPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            py.gov.ips.consultas.servicios.ServiciosConsultasSIHBindingStub _stub = new py.gov.ips.consultas.servicios.ServiciosConsultasSIHBindingStub(portAddress, this);
            _stub.setPortName(getServiciosConsultasSIHPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setServiciosConsultasSIHPortEndpointAddress(java.lang.String address) {
        ServiciosConsultasSIHPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (py.gov.ips.consultas.servicios.ServiciosConsultasSIH.class.isAssignableFrom(serviceEndpointInterface)) {
                py.gov.ips.consultas.servicios.ServiciosConsultasSIHBindingStub _stub = new py.gov.ips.consultas.servicios.ServiciosConsultasSIHBindingStub(new java.net.URL(ServiciosConsultasSIHPort_address), this);
                _stub.setPortName(getServiciosConsultasSIHPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ServiciosConsultasSIHPort".equals(inputPortName)) {
            return getServiciosConsultasSIHPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "ServiciosConsultasSIHService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://servicios.consultas.ips.gov.py/", "ServiciosConsultasSIHPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ServiciosConsultasSIHPort".equals(portName)) {
            setServiciosConsultasSIHPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
