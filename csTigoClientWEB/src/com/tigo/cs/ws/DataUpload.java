/**
 * 
 */
package com.tigo.cs.ws;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.tigo.cs.domain.view.DataArpTipoFactura;
import com.tigo.cs.domain.view.DataBank;
import com.tigo.cs.domain.view.DataClient;
import com.tigo.cs.domain.view.DataDeliveryman;
import com.tigo.cs.domain.view.DataDeposit;
import com.tigo.cs.domain.view.DataEmployee;
import com.tigo.cs.domain.view.DataGuard;
import com.tigo.cs.domain.view.DataMotive;
import com.tigo.cs.domain.view.DataProduct;
import com.tigo.cs.domain.view.DataVehicle;
import com.tigo.cs.ws.facade.DataUploadResponse;
import com.tigo.cs.ws.facade.DataUploadWSFacade;

/**
 * Web service para el upload de meta-datos de Clientes. Define una operación
 * para cada servicio de Soluciones Coporativas.
 * 
 * Las operaciones devuelven siempre un {@link DataUploadResponse} con un código
 * del resultado según caso, una descripción del resultado y un array de los
 * keys de cada registro indicando aquellos que no han sido guardados de forma
 * exitosa.
 * 
 * @author Miguel Zorrilla
 * @since CS Fase 7
 */

@WebService
public class DataUpload {

    @EJB
    private DataUploadWSFacade uploadFacade;

    @WebMethod(operationName = "uploadClients")
    public DataUploadResponse uploadClients(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "clients") List<DataClient> clients) {
        
        return uploadFacade.uploadDataClients(user, password, clients);
    }
    
    @WebMethod(operationName = "uploadProducts")
    public DataUploadResponse uploadProducts(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "products") List<DataProduct> products) {
        
        return uploadFacade.uploadDataProducts(user, password, products);
    }
    
    @WebMethod(operationName = "uploadMotives")
    public DataUploadResponse uploadMotives(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "motives") List<DataMotive> motives) {
        
        return uploadFacade.uploadDataMotives(user, password, motives);
    }
    
    @WebMethod(operationName = "uploadGuards")
    public DataUploadResponse uploadGuards(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "guards") List<DataGuard> guards) {
        
        return uploadFacade.uploadDataGuards(user, password, guards);
    }
    
    @WebMethod(operationName = "uploadDeliverymans")
    public DataUploadResponse uploadDeliverymans(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "deliverymans") List<DataDeliveryman> deliverymans) {
        
        return uploadFacade.uploadDataDeliverymans(user, password, deliverymans);
    }
    
    @WebMethod(operationName = "uploadARPTipoFacturas")
    public DataUploadResponse uploadARPTipoFacturas(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "arpTipoFacturas") List<DataArpTipoFactura> arpTipoFacturas) {
        
        return uploadFacade.uploadDataARPTipoFacturas(user, password, arpTipoFacturas);
    }
    
    @WebMethod(operationName = "uploadEmployees")
    public DataUploadResponse uploadEmployees(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "employees") List<DataEmployee> employees) {
        
        return uploadFacade.uploadDataEmployees(user, password, employees);
    }
        
    @WebMethod(operationName = "uploadVehicles")
    public DataUploadResponse uploadVehicles(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "vehicles") List<DataVehicle> vehicles) {
        
        return uploadFacade.uploadDataVehicles(user, password, vehicles);
    }
    
    @WebMethod(operationName = "uploadBanks")
    public DataUploadResponse uploadBanks(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "banks") List<DataBank> banks) {
        
        return uploadFacade.uploadDataBanks(user, password, banks);
    }
    
    @WebMethod(operationName = "uploadDeposits")
    public DataUploadResponse uploadDeposits(
            @WebParam(name = "user") String user, 
            @WebParam(name = "password") String password, 
            @WebParam(name = "deposits") List<DataDeposit> deposits) {
        
        return uploadFacade.uploadDataDeposits(user, password, deposits);
    }
    
}
