package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.commons.ejb.FacadeException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.Route;
import com.tigo.cs.domain.RouteDetail;
import com.tigo.cs.domain.Userphone;

public abstract class RouteDetailAPI extends AbstractAPI<RouteDetail> {

    private Double cellAngle;
    private Double cellCoverage;
    private static final Double RAD_TO_SEXAGESIMAL = Math.PI / 180;

    public RouteDetailAPI() {
        super(RouteDetail.class);
    }

    public Double getCellAngle() {
        if (cellAngle == null) {
            try {
                cellAngle = Double.parseDouble(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "map.cell.sector.area.angle"));
            } catch (GenericFacadeException ex) {
                getFacadeContainer().getNotifier().signal(RouteDetailAPI.class,
                        Action.WARNING, "No se obtuvo el angulo de la celda.",
                        ex);
                cellAngle = 120.0;
            }
        }
        return cellAngle;
    }

    public Double getCellCoverage() {
        if (cellCoverage == null) {
            try {
                cellCoverage = Double.parseDouble(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "map.cell.sector.coverage"));
            } catch (GenericFacadeException ex) {
                getFacadeContainer().getNotifier().signal(RouteDetailAPI.class,
                        Action.WARNING,
                        "No se obtuvo la covertura de la celda.", ex);
                cellCoverage = 5000.0;
            }
        }
        return cellCoverage;
    }

    @Override
    public void remove(RouteDetail entity) {
        RouteDetail rd = (RouteDetail) entity;
        List<RouteDetail> routeDetails = getRouteDetailsAfterPosition(
                rd.getRoute().getRouteCod(), rd.getPositionNumber());
        for (RouteDetail routeDetail : routeDetails) {
            routeDetail.setPositionNumber(routeDetail.getPositionNumber() - 1);
            edit(routeDetail);
        }
        super.remove(entity);
    }

    public RouteDetail getRouteDetailByPosition(long routeCod, Integer pos) throws FacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT o FROM RouteDetail o WHERE o.route.routeCod = :routeCod AND o.positionNumber = :pos");
            query.setParameter("routeCod", routeCod);
            query.setParameter("pos", pos);
            query.setMaxResults(1);
            return (RouteDetail) query.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new FacadeException(getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<RouteDetail> getRouteDetailsAfterPosition(long routeCod, Integer pos) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT o FROM RouteDetail o WHERE o.route.routeCod = :routeCod AND o.positionNumber >= :pos");
            query.setParameter("routeCod", routeCod);
            query.setParameter("pos", pos);
            return query.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void updatePosition(RouteDetail routeDetail, boolean isUpping) throws FacadeException {
        int factor = isUpping ? -1 : 1;
        RouteDetail routeDetailRequestedPos = getRouteDetailByPosition(
                routeDetail.getRoute().getRouteCod(),
                routeDetail.getPositionNumber() + factor);
        routeDetail.setPositionNumber(routeDetail.getPositionNumber() + factor);
        edit(routeDetail);
        routeDetailRequestedPos.setPositionNumber(routeDetailRequestedPos.getPositionNumber()
            + (factor * -1));
        edit(routeDetailRequestedPos);
    }

    public void addDetailToRoute(Client client, Route route, double latitude, double longitude, String description) {

        MapMark mapMark = getFacadeContainer().getMapMarkAPI().findByClientLatLng(client, latitude,
                longitude);

        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setMapMark(mapMark);
        routeDetail.setRoute(route);
        Integer posNumber = getLastPosition(route);
        routeDetail.setPositionNumber(posNumber == null ? 1 : posNumber.intValue() + 1);
        routeDetail.setDescription(description);
        routeDetail = create(routeDetail);

    }

    public Integer getLastPosition(Route route) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(""
                + "SELECT max(d.positionNumber) FROM Route r , in (r.routeDetails) d "
                + "WHERE r.routeCod = :routeCod");
            q.setParameter("routeCod", route.getRouteCod());
            return (Integer) q.getSingleResult();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<MapMark> getRouteDetailMark(Route route) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT d.mapMark FROM RouteDetail d "
                + " WHERE d.route = :route " + " ORDER BY d.positionNumber ");
            q.setParameter("route", route);
            Set<MapMark> set = new HashSet<MapMark>(q.getResultList());
            return new ArrayList<MapMark>(set);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<MapMark> getNotInRouteDetailMark(Route route, Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT m FROM MapMark m "
                + " WHERE m.client = :client "
                + " AND m NOT IN ( SELECT DISTINCT d.mapMark FROM RouteDetail d "
                + " WHERE d.route = :route  )");
            q.setParameter("route", route);
            q.setParameter("client", client);
            return q.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * 
     * Este metodo recupera la lista de los puntos entre cada tramo de la hoja
     * de ruta definida previamente. Esta lista esta filtrada por las rutas del
     * usuario telefonico y por el rango de fechas definido en cada ruta para su
     * utilizacion, ademas de la hora definida en cada detalle con respecto a la
     * hora actual.
     * 
     * 
     * @param userphone
     *            - El usuario telefonico por el cual filtrar las rutas
     * @return la lista de rutas que el usuario telefonico esta habilitado para
     *         realizar marcaciones
     */
    public List<RouteDetail> getUserphoneRouteDetail(Userphone userphone) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String jpql = ""
                + " SELECT det FROM Userphone u "
                + ", IN (u.routeUserphones) ru "
                + ", In (ru.route.routeDetails ) det "
                + " WHERE u.userphoneCod = :userphone"
                + " AND trunc(ru.route.toDate) >= to_date(''{0}'',''yyyy/MM/dd'')"
                + " AND trunc(ru.route.fromDate) <= to_date(''{1}'',''yyyy/MM/dd'') "
                + " ORDER BY det.route, det.positionNumber";

            jpql = MessageFormat.format(jpql, sdf.format(new Date()),
                    sdf.format(new Date()));
            Query q = em.createQuery(jpql);

            q.setParameter("userphone", userphone.getUserphoneCod());
            return q.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public RouteDetail getNextRouteDetailInCoverage(Userphone userphone, Cell cell) throws InvalidOperationException {

        RouteDetail detail = getRemaingRouteDetails(userphone);
        if (detail == null) {
            throw new InvalidOperationException("Operacion Invalida. No tiene marcado ningun punto en su hoja de ruta.");
        }
        // for (RouteDetail detail : routeDetails) {
        /*
         * verificar si el punto esta dentro del area de cobertura de la celda;
         */
        MapMark mark = detail.getMapMark();
        Double r = 6371.0; // km
        Double distance = 1000
            * r
            * Math.acos(Math.cos(mark.getLatitudeNum() * RAD_TO_SEXAGESIMAL)
                * Math.cos(cell.getLatitudeNum() * RAD_TO_SEXAGESIMAL)
                * Math.cos(cell.getLongitudeNum() * RAD_TO_SEXAGESIMAL
                    - mark.getLongitudeNum() * RAD_TO_SEXAGESIMAL)
                + Math.sin(mark.getLatitudeNum() * RAD_TO_SEXAGESIMAL)
                * Math.sin(cell.getLatitudeNum() * RAD_TO_SEXAGESIMAL));

        Double dLon = (mark.getLongitudeNum() - cell.getLongitudeNum())
            * RAD_TO_SEXAGESIMAL;
        Double y = Math.sin(dLon) * Math.cos(mark.getLatitudeNum());
        Double x = Math.cos(cell.getLatitudeNum())
            * Math.sin(mark.getLatitudeNum()) - Math.sin(cell.getLatitudeNum())
            * Math.cos(mark.getLatitudeNum()) * Math.cos(dLon);
        Double betha = ((Math.toDegrees(Math.atan2(y, x))) + 360.0) % 360.0;

        // notifier.signal(RouteDetailFacade.class, Action.INFO,
        // MessageFormat.format(""
        // + "\nCelda:{0}."
        // + "\nDistancia:{1} metro."
        // + "\nAzimuth:{2} grados."
        // + "\nAngulo:{3} grados.",
        // cell.getIdentityNum(),
        // distance,
        // cell.getAzimuthNum(),
        // betha));

        /*
         * hallamos el cuadrante en el que esta el punto de la hoja de ruta y
         * verificamos si el punto se encuentra dentro del area de cobertura
         */
        Double alpha = getCellAngle() / 2;
        if ((cell.getAzimuthNum() - alpha <= betha
            && cell.getAzimuthNum() + alpha >= betha && distance <= getCellCoverage())) {
            return detail;
        } else {
            throw new InvalidOperationException("Usted se encuentra fuera del area de su hoja de ruta");
        }
        // }
        // return null;
    }

    /**
     * Obtiene la lista de los detalles o puntos dentro de la hoja de ruta que
     * aun no fueron marcados como recurridos durante el dia de hoy
     * 
     * @param userphone
     *            - el usuario telefonico que esta solicitando sus rutas
     *            pendientes
     * @return
     */
    public RouteDetail getRemaingRouteDetails(Userphone userphone) throws InvalidOperationException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            if (userphone == null) {
                throw new InvalidOperationException("El usuario no puede ser nulo");
            }

            Query q = em.createQuery(""
            /*
             * obtengo los puntos definidos en la hoja de ruta
             */
            + "SELECT det FROM Userphone u " + " ,IN (u.routeUserphones) ru "
                + " ,In (ru.route.routeDetails ) det"
                + " WHERE u.userphoneCod = :userphoneCod "
                /*
                 * sin los puntos ya visitados
                 */
                + " AND det not in ( "
                + "             SELECT det.routeDetail FROM Userphone u1 "
                + "                 ,IN (u1.routeDetailUserphones) det "
                + "             WHERE u.userphoneCod = u1.userphoneCod "
                + "             AND trunc(det.recordDate) = :today "
                + "             ) "
                + " AND trunc(det.route.fromDate) <= :today "
                + " AND trunc(det.route.toDate) >= :today "
                + " ORDER BY det.positionNumber");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setMaxResults(1);
            /*
             * obtenemos la lista de los puntos que aun faltan por recorrer en
             * la hoja de ruta del dia para el userphone
             */
            RouteDetail routeDetails = null;
            try {
                routeDetails = (RouteDetail) q.getSingleResult();
            } catch (NoResultException ex) {
                routeDetails = null;
            }
            if (routeDetails == null) {
                /*
                 * en el caso que no se tenga ningun resultado para la consulta
                 * anterior se deben analizar dos casos 1.- que el usuario ya
                 * recorrio todos sus puntos el dia de hoy, eso sabemos si en la
                 * conbinacion de resultados con la consulta anterior y el la
                 * verificacion de si hoy registro algun recorrido, la consulta
                 * anterior (de los puntos restantes) no retorna registros y
                 * tiene registros del dia ya marcados, nos lleva a concluir que
                 * ya recorrio todos sus puntos del dia.
                 * 
                 * 2.- que el usuario el usuario aun no registro ninguna tramo
                 * en su hoja de ruta, por ello se retornan todos los detalles
                 * de la ruta definida para el dia de hoy.
                 */
                routeDetails = getTodayRouteDetails(userphone);
                /*
                 * verificamos caso 1
                 */
                if (routeDetails != null) {
                    /*
                     * ya recorrio todos sus puntos
                     */
                    throw new InvalidOperationException("Operacion Invalida. Usted ya recorrio todos los puntos de su hoja de ruta");
                    // return null;
                } else {
                    /*
                     * retornamos el caso 2
                     */
                    routeDetails = getRouteDetails(userphone);
                }

            }
            return routeDetails;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public RouteDetail getTodayRouteDetails(Userphone userphone) throws InvalidOperationException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (userphone == null) {
                throw new InvalidOperationException("El usuario no puede ser nulo");
            }
            Query q = em.createQuery(""
                + "SELECT det.routeDetail FROM Userphone u1 "
                + " ,IN (u1.routeDetailUserphones) det "
                + " WHERE u1.userphoneCod = :userphoneCod "
                + " AND trunc(det.recordDate) = :today "
                + " ORDER BY det.routeDetail.positionNumber ");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setMaxResults(1);
            RouteDetail routeDetail = null;
            try {
                routeDetail = (RouteDetail) q.getSingleResult();
            } catch (NoResultException exception) {
                routeDetail = null;
            }
            return routeDetail;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public RouteDetail getRouteDetails(Userphone userphone) throws InvalidOperationException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            if (userphone == null) {
                throw new InvalidOperationException("El usuario no puede ser nulo");
            }
            Query q = em.createQuery(""
            /*
             * obtengo los puntos definidos en la hoja de ruta
             */
            + "SELECT det FROM Userphone u " + " ,IN (u.routeUserphones) ru "
                + " ,In (ru.route.routeDetails ) det"
                + " WHERE u.userphoneCod = :userphoneCod "
                /*
                 * de la ruta definida para hoy
                 */
                + " AND trunc(det.route.fromDate) <= :today "
                + " AND trunc(det.route.toDate) >= :today "
                + " ORDER BY det.positionNumber");
            q.setParameter("userphoneCod", userphone.getUserphoneCod());
            q.setParameter("today", new Date(), TemporalType.DATE);
            q.setMaxResults(1);
            RouteDetail routeDetail = null;
            try {
                routeDetail = (RouteDetail) q.getSingleResult();
            } catch (NoResultException ex) {
                routeDetail = null;
            }
            return routeDetail;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
