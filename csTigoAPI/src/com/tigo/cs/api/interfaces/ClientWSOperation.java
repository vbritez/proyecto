package com.tigo.cs.api.interfaces;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Defines common operations for web service integration. Each service must
 * defines its own list of operations available here.
 * @author Miguel Zorrilla
 */
public interface ClientWSOperation {

    /**
     * Return a list of customers with a specific size for the list to avoid
     * heavy traffic on the network.
     * @param urlChr URL for the web service
     * @param name the customer name
     * @param size the list size
     * @return a list of Customers.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    List<com.tigo.cs.ws.client.Customer> getCustomerByName(String name, int size) throws MalformedURLException;

    /**
     * Return a customer given a code.
     * @param urlChr URL for the web service
     * @param code customer identifier
     * @return a customer with the specific <code>code<code>.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    com.tigo.cs.ws.client.Customer getCustomerByCode(String code) throws MalformedURLException;

    /**
     * Return the number of matching records given a customer name.
     * @param urlChr URL for the web service
     * @param name the customer name
     * @return the number of matching records.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    int getCustomerByNameCount(String name) throws MalformedURLException;

    /**
     *Return a list of products with a specific size for the list to avoid
     * heavy traffic on the network
     * @param urlChr URL for the web service
     * @param name the product description or name
     * @param size the list size to be obtained
     * @return a list of product.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    List<com.tigo.cs.ws.client.Product> getProductByName(String name, int size) throws MalformedURLException;

    /**
     * Return a product given a code.
     * @param urlChr URL for the web service
     * @param code product identifier
     * @return a product with the specific <code>code<code>.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    com.tigo.cs.ws.client.Product getProductByCode(String code) throws MalformedURLException;

    /**
     * Return the number of matching records given a product name.
     * @param urlChr URL for the web service
     * @param name the product name
     * @return the number of matching records.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    int getProductByNameCount(String name) throws MalformedURLException;

    /**
     * Return a list of guards with a specific size for the list to avoid
     * heavy traffic on the network
     * @param urlChr URL for the web service
     * @param name the guard name
     * @param size the list size to be obtained
     * @return a list of guard.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    List<com.tigo.cs.ws.client.Guard> getGuardByName(String name, int size) throws MalformedURLException;

    /**
     * Return a guard given a code.
     * @param urlChr URL for the web service
     * @param code guard identifier
     * @return a guard with the specific <code>code<code>.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    com.tigo.cs.ws.client.Guard getGuardByCode(String code) throws MalformedURLException;

    /**
     * Return the number of matching records given a guard name.
     * @param urlChr URL for the web service
     * @param name the guard name
     * @return the number of matching records.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    int getGuardByNameCount(String name) throws MalformedURLException;

    /**
     *Return a list of products with a specific size for the list to avoid
     * heavy traffic on the network
     * @param urlChr URL for the web service
     * @param name the motive description or name
     * @param size the list size to be obtained
     * @return a list of motive.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    List<com.tigo.cs.ws.client.Motive> getMotiveByName(String name, int size) throws MalformedURLException;

    /**
     * Return a motive given a code.
     * @param urlChr URL for the web service
     * @param code motive identifier
     * @return a motive with the specific <code>code<code>.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException
     */
    com.tigo.cs.ws.client.Motive getMotiveByCode(String code) throws MalformedURLException;

    /**
     * Return the number of matching records given a motive name.
     * @param urlChr URL for the web service
     * @param name the motive description
     * @return the number of matching records.
     * @throws MalformedURLException in case of an erroneous URL.
     * @throws UnfinishedServiceException 
     */
    int getMotiveByNameCount(String name) throws MalformedURLException;
}
