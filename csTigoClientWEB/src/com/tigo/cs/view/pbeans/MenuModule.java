package com.tigo.cs.view.pbeans;

import com.tigo.cs.domain.Moduleadmin;
import com.tigo.cs.domain.Screen;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Javier Kovacs
 * @version $Id$
 */
public class MenuModule implements Serializable {

    private Moduleadmin moduleAdmin;
    private List<Screen> screens;

    public MenuModule() {
    }

    public MenuModule(Moduleadmin moduleAdmin, List<Screen> screens) {
        this.moduleAdmin = moduleAdmin;
        this.screens = screens;
    }

    public Moduleadmin getModuleAdmin() {
        return moduleAdmin;
    }

    public void setModuleAdmin(Moduleadmin moduleAdmin) {
        this.moduleAdmin = moduleAdmin;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void setScreens(List<Screen> screens) {
        this.screens = screens;
    }
}
