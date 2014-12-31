package com.tigo.cs.facade.ussddriver.menumovil;

import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class MenuMovilThanksDriver implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public String execute(String acoount, HashMap<String, HashMap<String, String>> parameters) {

        facadeContainer.getNotifier().info(getClass(), acoount,
                "Ha finalizado la sesión.");

        return "Gracias por utilizar el servicio.";
    }

}
