package com.tigo.cs.view.pbeans;

import com.tigo.cs.domain.Moduleclient;
import com.tigo.cs.domain.Screenclient;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */
public class MenuModuleClient implements Serializable {

    private Moduleclient moduleClient;
    private List<Screenclient> screenclients;

    public MenuModuleClient() {
    }

    public MenuModuleClient(Moduleclient moduleClient, List<Screenclient> screenclients) {
        this.moduleClient = moduleClient;
        this.screenclients = screenclients;
    }

    public Moduleclient getModuleClient() {
        return moduleClient;
    }

    public void setModuleClient(Moduleclient moduleClient) {
        this.moduleClient = moduleClient;
    }

    public List<Screenclient> getScreenclients() {
        return screenclients;
    }

    public void setScreenclients(List<Screenclient> screenclients) {
        this.screenclients = screenclients;
    }
}
