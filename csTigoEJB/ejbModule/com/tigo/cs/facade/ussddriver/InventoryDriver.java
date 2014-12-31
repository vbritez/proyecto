package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.InventoryService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.InventoryStdServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.INVENTARIO_STANDARD)
public class InventoryDriver extends InventoryStdServiceAPI<InventoryService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    private static final String INVENTARIO_CODIGO_PRODUCTO = "INVENTARIO_CODIGO_PRODUCTO";
    private static final String INVENTARIO_CODIGO_DEPOSITO = "INVENTARIO_CODIGO_DEPOSITO";
    private static final String INVENTARIO_CANTIDAD = "INVENTARIO_CANTIDAD";
    
    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
        getEntity().setDepositCode(getNodeValue(INVENTARIO_CODIGO_DEPOSITO));
        getEntity().setProductCode(getNodeValue(INVENTARIO_CODIGO_PRODUCTO));
        getEntity().setQuantity(getNodeValue(INVENTARIO_CANTIDAD));
    }

}
