package com.tigo.cs.api.facade;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.RouteDetail;
import com.tigo.cs.domain.RouteDetailUserphone;
import com.tigo.cs.domain.Userphone;

public abstract class RouteDetailUserphoneAPI extends AbstractAPI<RouteDetailUserphone> {

    public RouteDetailUserphoneAPI() {
        super(RouteDetailUserphone.class);
    }

    public boolean addRouteDetailUserphone(Userphone userphone, Cell cell) throws InvalidOperationException {

        try {
            /*
             * validamos que el usuario tenga una hoja de ruta asignada
             */
            if (getFacadeContainer().getRouteAPI().validateRouteUserphone(
                    userphone)) {
                if (cell == null) {
                    throw new InvalidOperationException("El usuario o la celda no pueden ser nulos.");
                }

                /*
                 * obtenemos el primer punto que se encuentre en el area de
                 * cobertura de la celda
                 */
                RouteDetail routeDetail = getFacadeContainer().getRouteDetailAPI().getNextRouteDetailInCoverage(
                        userphone, cell);
                if (routeDetail == null) {
                    throw new InvalidOperationException("No tiene ningun punto marcado en el area de cobertura actual.");
                }
                RouteDetailUserphone routeDetailUserphone = new RouteDetailUserphone();
                routeDetailUserphone.setRouteDetail(routeDetail);
                routeDetailUserphone.setUserphone(userphone);
                routeDetailUserphone.setOffRoute(0);
                /*
                 * la fecha esta en el @PrePersist
                 */
                routeDetailUserphone = create(routeDetailUserphone);

                getFacadeContainer().getRouteUserphoneAPI().addActualPosition(
                        routeDetail.getRoute(), userphone, routeDetail);
                return true;
            }
        } catch (Exception ex) {
            throw new InvalidOperationException("No se puede procesar el mensaje");
        }
        return false;
    }
}
