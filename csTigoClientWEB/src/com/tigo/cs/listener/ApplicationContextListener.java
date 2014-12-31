package com.tigo.cs.listener;

import java.io.File;

import javax.ejb.EJB;
import javax.faces.FactoryFinder;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.facade.TmpWsresultFacade;
import com.tigo.cs.security.AccessPhaseListener;
import com.tigo.cs.security.CacheControlPhaseListener;
import com.tigo.cs.security.Notifier;

/**
 * Provide a listener for application context that allows application
 * configurations and behavior settings.
 * 
 * @author Miguel Zorrilla
 * @since csTigoClient 1.0
 * @version $Id: ApplicationContextListener.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
public class ApplicationContextListener implements ServletContextListener {

    @EJB
    private TmpWsresultFacade tmpWsresultFacade;
    @EJB
    private Notifier notifier;

    /** Creates a new instance of WSContextListener */
    public ApplicationContextListener() {
    }

    /**
     * Notification that the servlet context is about to be shut down.
     * 
     * @param event
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("************* LA APLICACION CSTIGO (CLIENTE) HA SIDO FINALIZADO SATISFACTORIAMENTE *************");
    }

    /**
     * Notification that the web application is ready to process requests.
     * 
     * @param event
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            // Setea el nombre del DS para obtener conexion directa a la base de
            // datos
            System.setProperty("Application.DS.name", "csTigoDS");
            System.setProperty("Application.name", "csTigoClient");

            // Configura e inicializa el LOG4J
            String log4jFile = event.getServletContext().getRealPath("/")
                + "/WEB-INF/conf/log4j.properties";
            File file = new File(log4jFile);
            if (file.exists()) {
                PropertyConfigurator.configure(log4jFile);
                notifier.signal(
                        this.getClass(),
                        Action.INFO,
                        "LOG4J configurado. Archivo de configuración: ".concat(log4jFile));
            } else {
                notifier.signal(
                        this.getClass(),
                        Action.WARNING,
                        "El archivo de configuración del Log4j no se ha localizado. Se utilizá la configuración básica");
                BasicConfigurator.configure();
            }

            // Register phase listeners
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            lifecycle.addPhaseListener(new AccessPhaseListener());
            lifecycle.addPhaseListener(new CacheControlPhaseListener());
            notifier.signal(this.getClass(), Action.DEBUG,
                    "PhaseListener agregados satisfactoriamente.");

            tmpWsresultFacade.removeTmpCacheData();
            notifier.signal(this.getClass(), Action.INFO,
                    "Tabla temporal de cache de datos de WS limpiada.");
            notifier.signal(
                    this.getClass(),
                    Action.INFO,
                    "************* LA APLICACIÓN CSTIGO (CLIENTE) HA SIDO INICIADO SATISFACTORIAMENTE *************");

            // CacheManager manager = CacheManager.getInstance();
            // MBeanServer mBeanServer =
            // ManagementFactory.getPlatformMBeanServer();
            // ManagementService.registerMBeans(manager, mBeanServer, false,
            // false, false, true);
            //
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
