
package com.tigo.cs.ws.client;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.1-hudson-28-
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ClientWS", targetNamespace = "http://ws.external/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ClientWS {


    /**
     * 
     * @param name
     * @param size
     * @return
     *     returns java.util.List<com.tigo.cs.ws.client.Customer>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByName", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByName")
    @ResponseWrapper(localName = "getCustomerByNameResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByNameResponse")
    @Action(input = "http://ws.external/ClientWS/getCustomerByNameRequest", output = "http://ws.external/ClientWS/getCustomerByNameResponse")
    public List<Customer> getCustomerByName(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "size", targetNamespace = "")
        int size);

    /**
     * 
     * @param name
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByNameCount", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByNameCount")
    @ResponseWrapper(localName = "getCustomerByNameCountResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByNameCountResponse")
    @Action(input = "http://ws.external/ClientWS/getCustomerByNameCountRequest", output = "http://ws.external/ClientWS/getCustomerByNameCountResponse")
    public Integer getCustomerByNameCount(
        @WebParam(name = "name", targetNamespace = "")
        String name);

    /**
     * 
     * @param code
     * @return
     *     returns com.tigo.cs.ws.client.Customer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCustomerByCode", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByCode")
    @ResponseWrapper(localName = "getCustomerByCodeResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetCustomerByCodeResponse")
    @Action(input = "http://ws.external/ClientWS/getCustomerByCodeRequest", output = "http://ws.external/ClientWS/getCustomerByCodeResponse")
    public Customer getCustomerByCode(
        @WebParam(name = "code", targetNamespace = "")
        String code);

    /**
     * 
     * @param name
     * @param size
     * @return
     *     returns java.util.List<com.tigo.cs.ws.client.Product>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getProductByName", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByName")
    @ResponseWrapper(localName = "getProductByNameResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByNameResponse")
    @Action(input = "http://ws.external/ClientWS/getProductByNameRequest", output = "http://ws.external/ClientWS/getProductByNameResponse")
    public List<Product> getProductByName(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "size", targetNamespace = "")
        int size);

    /**
     * 
     * @param name
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getProductByNameCount", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByNameCount")
    @ResponseWrapper(localName = "getProductByNameCountResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByNameCountResponse")
    @Action(input = "http://ws.external/ClientWS/getProductByNameCountRequest", output = "http://ws.external/ClientWS/getProductByNameCountResponse")
    public Integer getProductByNameCount(
        @WebParam(name = "name", targetNamespace = "")
        String name);

    /**
     * 
     * @param code
     * @return
     *     returns com.tigo.cs.ws.client.Product
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getProductByCode", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByCode")
    @ResponseWrapper(localName = "getProductByCodeResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetProductByCodeResponse")
    @Action(input = "http://ws.external/ClientWS/getProductByCodeRequest", output = "http://ws.external/ClientWS/getProductByCodeResponse")
    public Product getProductByCode(
        @WebParam(name = "code", targetNamespace = "")
        String code);

    /**
     * 
     * @param name
     * @param size
     * @return
     *     returns java.util.List<com.tigo.cs.ws.client.Motive>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMotiveByName", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByName")
    @ResponseWrapper(localName = "getMotiveByNameResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByNameResponse")
    @Action(input = "http://ws.external/ClientWS/getMotiveByNameRequest", output = "http://ws.external/ClientWS/getMotiveByNameResponse")
    public List<Motive> getMotiveByName(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "size", targetNamespace = "")
        int size);

    /**
     * 
     * @param name
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMotiveByNameCount", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByNameCount")
    @ResponseWrapper(localName = "getMotiveByNameCountResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByNameCountResponse")
    @Action(input = "http://ws.external/ClientWS/getMotiveByNameCountRequest", output = "http://ws.external/ClientWS/getMotiveByNameCountResponse")
    public Integer getMotiveByNameCount(
        @WebParam(name = "name", targetNamespace = "")
        String name);

    /**
     * 
     * @param code
     * @return
     *     returns com.tigo.cs.ws.client.Motive
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getMotiveByCode", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByCode")
    @ResponseWrapper(localName = "getMotiveByCodeResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetMotiveByCodeResponse")
    @Action(input = "http://ws.external/ClientWS/getMotiveByCodeRequest", output = "http://ws.external/ClientWS/getMotiveByCodeResponse")
    public Motive getMotiveByCode(
        @WebParam(name = "code", targetNamespace = "")
        String code);

    /**
     * 
     * @param name
     * @param size
     * @return
     *     returns java.util.List<com.tigo.cs.ws.client.Guard>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getGuardByName", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByName")
    @ResponseWrapper(localName = "getGuardByNameResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByNameResponse")
    @Action(input = "http://ws.external/ClientWS/getGuardByNameRequest", output = "http://ws.external/ClientWS/getGuardByNameResponse")
    public List<Guard> getGuardByName(
        @WebParam(name = "name", targetNamespace = "")
        String name,
        @WebParam(name = "size", targetNamespace = "")
        int size);

    /**
     * 
     * @param name
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getGuardByNameCount", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByNameCount")
    @ResponseWrapper(localName = "getGuardByNameCountResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByNameCountResponse")
    @Action(input = "http://ws.external/ClientWS/getGuardByNameCountRequest", output = "http://ws.external/ClientWS/getGuardByNameCountResponse")
    public Integer getGuardByNameCount(
        @WebParam(name = "name", targetNamespace = "")
        String name);

    /**
     * 
     * @param code
     * @return
     *     returns com.tigo.cs.ws.client.Guard
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getGuardByCode", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByCode")
    @ResponseWrapper(localName = "getGuardByCodeResponse", targetNamespace = "http://ws.external/", className = "com.tigo.cs.ws.client.GetGuardByCodeResponse")
    @Action(input = "http://ws.external/ClientWS/getGuardByCodeRequest", output = "http://ws.external/ClientWS/getGuardByCodeResponse")
    public Guard getGuardByCode(
        @WebParam(name = "code", targetNamespace = "")
        String code);

}