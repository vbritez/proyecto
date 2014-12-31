package com.tigo.cs.api.facade;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Route;
import com.tigo.cs.domain.RouteDetail;
import com.tigo.cs.domain.RouteUserphone;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;

/**
 * 
 * @author Miguel Maciel
 * @version $Id: RouteUserphoneAPI.java 2246 2012-12-21 19:39:48Z miguel.maciel
 *          $
 */

public abstract class RouteUserphoneAPI extends AbstractAPI<RouteUserphone> {

    public RouteUserphoneAPI() {
        super(RouteUserphone.class);
    }

    public List<RouteUserphone> getRouteUserphonesByUserweb(Userweb userweb, Route route) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("select r from RouteUserphone r, "
                + " in (r.userphone.classificationList) cl where cl in (:classifications) "
                + " AND r.userphone.client.clientCod = :clientCod"
                + " AND r.route.routeCod = :routeCod");
//            List<Classification> classifications = getFacadeContainer().getClassificationAPI().findByUserweb(
//                    userweb);
            List<Classification> classifications = getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(userweb);
            if (classifications == null) {
                return null;
            }
            query.setParameter("classifications", classifications);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter("routeCod", route.getRouteCod());
            return (List<RouteUserphone>) query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public boolean hasRouteToday(Userphone userphone, Route route) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");

            Query query = em.createQuery("select r from RouteUserphone r "
                + " WHERE r.route.client.clientCod = :clientCod"
                + " AND ( '"
                + sdf.format(route.getFromDate())
                + "' BETWEEN TRUNC (r.route.fromDate) AND TRUNC (r.route.toDate) "
                + " OR '"
                + sdf.format(route.getToDate())
                + "' BETWEEN TRUNC (r.route.fromDate) AND TRUNC (r.route.toDate) "
                + " OR TRUNC (r.route.fromDate) BETWEEN '"
                + sdf.format(route.getFromDate()) + "' AND '"
                + sdf.format(route.getToDate()) + "'"
                + " OR TRUNC (r.route.toDate) BETWEEN '"
                + sdf.format(route.getFromDate()) + "' AND '"
                + sdf.format(route.getToDate()) + "' )");

            // + " AND ( '" + sdf.format(Calendar.getInstance().getTime()) +
            // "' BETWEEN TRUNC (r.route.fromDate) AND TRUNC (r.route.toDate) "
            // + " OR TRUNC (o.route.fromDate) BETWEEN '" +
            // sdf.format(Calendar.getInstance().getTime()) + "' AND '" +
            // sdf.format(Calendar.getInstance().getTime()) + "')");

            query.setParameter("clientCod",
                    userphone.getClient().getClientCod());
            return !query.getResultList().isEmpty();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RouteUserphone> getRouteUserphonesByRoute(Long routeCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("select r from RouteUserphone r "
                + " WHERE r.route.routeCod = :routeCod");
            query.setParameter("routeCod", routeCod);
            return (List<RouteUserphone>) query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void addActualPosition(Route route, Userphone userphone, RouteDetail detail) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery("" + "SELECT r FROM RouteUserphone r "
                + " WHERE r.route =  :route "
                + " AND r.userphone = :userphone ");
            q.setParameter("route", route);
            q.setParameter("userphone", userphone);
            RouteUserphone routeUserphone = (RouteUserphone) q.getSingleResult();
            routeUserphone.setActualPositionNumber(detail.getPositionNumber());
            routeUserphone = edit(routeUserphone);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
