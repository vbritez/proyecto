package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.domain.Route;
import com.tigo.cs.domain.RouteUserphone;
import com.tigo.cs.domain.Userphone;

public abstract class RouteAPI extends AbstractAPI<Route> {

    public RouteAPI() {
        super(Route.class);
    }

    @Override
    public Route create(Route entity) {
        Route route = (Route) entity;
        List<RouteUserphone> ruList = new ArrayList<RouteUserphone>(route.getRouteUserphones());
        route.setRouteUserphones(new ArrayList<RouteUserphone>());

        if (route.getToDate() == null) {
            route.setToDate(route.getFromDate());
        }

        route = (Route) super.create(entity);
        for (RouteUserphone routeUserphone : ruList) {
            routeUserphone.setRoute(route);
            routeUserphone.getRouteUserphonePK().setCodRoute(
                    route.getRouteCod());
            getFacadeContainer().getRouteUserphoneAPI().create(routeUserphone);
        }
        return route;
    }

    @Override
    public Route edit(Route entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Route route = (Route) entity;

            Route pRoute = find(route.getRouteCod());
            for (RouteUserphone routeUserphone : pRoute.getRouteUserphones()) {
                getFacadeContainer().getRouteUserphoneAPI().remove(
                        routeUserphone);
            }
            em.detach(pRoute);

            for (RouteUserphone routeUserphone : route.getRouteUserphones()) {
                getFacadeContainer().getRouteUserphoneAPI().create(
                        routeUserphone);
            }
            return em.merge(entity); // super.edit(entity);
                                       // //(T)persistedRoute;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * Validar que el userphone este asignado a una sola hoja de ruta para el
     * dia de hoy
     * 
     * @param userphone
     *            - usuario que sera validado
     * @return
     */
    public boolean validateRouteUserphone(Userphone userphone) throws InvalidOperationException {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (userphone == null) {
                throw new InvalidOperationException("El usuario no puede ser nulo");
            }
            Query q = em.createQuery(""
                + "SELECT ru.route FROM Userphone u , IN (u.routeUserphones) ru "
                + " WHERE u.userphoneCod = :userphoneCod "
                + " AND trunc(ru.route.fromDate) <= :today "
                + " AND trunc(ru.route.toDate) >= :today  " + ")");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("today", new Date(), TemporalType.DATE);
            List<Route> routes = q.getResultList();
            if ((routes != null && routes.size() > 1)) {
                throw new InvalidOperationException("Operacion invalida. Tiene asignada mas de una hoja de ruta.");
            }
            if (routes != null && routes.isEmpty()) {
                return false;
            }
            if (routes.get(0).getOffrouteMarkotion()
                || userphone.getOffroadMarkoption()) {
                return false;
            }
            return true;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }    
    
    
    
    /**
     * Validar que el userphone este asignado a una sola hoja de ruta para el
     * dia de hoy
     * 
     * @param userphone
     *            - usuario que sera validado
     * @return
     */
    public Route currentRoute(Userphone userphone) throws InvalidOperationException {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (userphone == null) {
                throw new InvalidOperationException("El usuario no puede ser nulo");
            }
            Query q = em.createQuery(""
                + "SELECT ru.route FROM Userphone u , IN (u.routeUserphones) ru "
                + " WHERE u.userphoneCod = :userphoneCod "
                + " AND trunc(ru.route.fromDate) <= :today "
                + " AND trunc(ru.route.toDate) >= :today  " + ")");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("today", new Date(), TemporalType.DATE);
            List<Route> routes = q.getResultList();
            if ((routes != null && routes.size() > 1)) {
                throw new InvalidOperationException("Operacion invalida. Tiene asignada mas de una hoja de ruta.");
            }
            if (routes != null && routes.isEmpty()) {
                return null;
            }
            if (routes.get(0).getOffrouteMarkotion()
                || userphone.getOffroadMarkoption()) {
                return null;
            }
            return routes.get(0);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }    
    
}
