package com.tigo.cs.api.facade;

import java.text.MessageFormat;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.ejb.FacadeException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Userphone;
import com.tigo.lbs.ws.LocationResponse;

public abstract class CellAPI extends AbstractAPI<Cell> {

    public CellAPI() {
        super(Cell.class);
    }

    public Cell getCellId(Userphone userphone, Integer lac, Integer identityNum) {

        if (userphone == null) {
            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    "CNFNULL - No se consultara la localizacion de la linea porque el userphone es nulo.");
            return null;
        }

        if (lac == null || identityNum == null) {

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    "CNFNULL - Consultamos al LBS en el caso que no tengamos informacion de localizacion");

            String notReachableState;
            try {
                notReachableState = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "tracking.lbs.state.notreachable");
            } catch (GenericFacadeException e) {
                getFacadeContainer().getNotifier().info(
                        getClass(),
                        null,
                        "CNFNULL - No se recupera parametro tracking.lbs.state.notreachable de la base de datos ");
                return null;
            }

            LocationResponse locationResponse = getFacadeContainer().getLbsGwServiceAPI().locate(
                    userphone.getCellphoneNum().toString());
            if (locationResponse != null
                && !locationResponse.getState().equals(notReachableState)) {
                if (locationResponse.getLca() != null
                    && locationResponse.getCellId() >= 0) {
                    lac = Integer.parseInt(locationResponse.getLca());
                    identityNum = locationResponse.getCellId();
                } else {
                    lac = -1;
                    identityNum = -1;
                }

            } else {
                lac = -1;
                identityNum = -1;
            }

        }

        if (lac <= 0 && identityNum <= 0) {
            getFacadeContainer().getNotifier().debug(
                    CellAPI.class,
                    null,
                    MessageFormat.format(
                            "CNFNULL - possible null origin data. CellID: {0,number,#}. LAC: {1,number,#}.",
                            identityNum, lac));
            return null;
        }
        String siteChr = "G";
        if (lac >= 3000) {
            siteChr = "W";
        }
        String sql = "SELECT c FROM Cell c "
            + "WHERE c.identityNum = :identityNum "
            + "AND substr(c.siteChr,0,1) = :siteChr "
            + "Order By c.versionNum desc";
        EntityManager em = getFacadeContainer().getEntityManager();
        try {
            Query q = em.createQuery(sql);
            q.setParameter("identityNum", identityNum);
            q.setParameter("siteChr", siteChr);
            q.setMaxResults(1);
            Cell result = (Cell) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            if (siteChr.equals("G")) {
                siteChr = "W";
            } else {
                siteChr = "G";
            }
            getFacadeContainer().getNotifier().warn(
                    CellAPI.class,
                    userphone.getCellphoneNum().toString(),
                    MessageFormat.format(
                            "CNFRET - retrying cell search in {0}. CellID: {1,number,#}. LAC: {2,number,#}.",
                            siteChr.equals("W") ? "3G" : "2G", identityNum, lac));
            try {
                Query q = em.createQuery(sql);
                q.setParameter("identityNum", identityNum);
                q.setParameter("siteChr", siteChr);
                q.setMaxResults(1);
                Cell result = (Cell) q.getSingleResult();
                return result;
            } catch (Exception ex) {

                getFacadeContainer().getNotifier().warn(
                        CellAPI.class,
                        userphone.getCellphoneNum().toString(),
                        MessageFormat.format(
                                "CNFLAST - last retrying cell search in Site. CellID:{0,number,#}. LAC: {1,number,#}",
                                identityNum, lac));
                try {

                    String identity = identityNum.toString().substring(0,
                            identityNum.toString().length() - 1).concat("%");

                    Query q = em.createQuery("SELECT c FROM Cell c "
                        + "WHERE c.identityNum like '" + identity + "' "
                        + "Order by c.versionNum desc");
                    q.setMaxResults(1);
                    Cell result = (Cell) q.getSingleResult();
                    return result;
                } catch (Exception exc) {
                    getFacadeContainer().getNotifier().warn(
                            CellAPI.class,
                            userphone.getCellphoneNum().toString(),
                            MessageFormat.format(
                                    "CNFNULL - cell not found. CellID:{0,number,#}. LAC: {1,number,#}",
                                    identityNum, lac));
                    return null;
                }

            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(CellAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String getSite(Cell cell) {

        if (cell == null) {
            return null;
        }

        return cell.getIdentityNum().toString().substring(0,
                cell.getIdentityNum().toString().length() - 1);

    }

    public boolean isSameSite(Cell sectorA, Cell sectorB) {

        if (sectorA != null && sectorB != null) {
            return getSite(sectorA).compareToIgnoreCase(getSite(sectorB)) == 0;
        }

        return false;
    }

    public Cell getCellId(Integer lac, Integer identityNum) throws FacadeException {

        String siteChr = "G";
        if (lac >= 3000) {
            siteChr = "W";
        }
        String sql = "SELECT c FROM Cell c "
            + "WHERE c.identityNum = :identityNum "
            + "AND substr(c.siteChr,0,1) = :siteChr "
            + "Order By c.versionNum desc";
        EntityManager em = getFacadeContainer().getEntityManager();
        try {
            Query q = em.createQuery(sql);
            q.setParameter("identityNum", identityNum);
            q.setParameter("siteChr", siteChr);
            q.setMaxResults(1);
            Cell result = (Cell) q.getSingleResult();
            return result;
        } catch (NoResultException e) {
            if (siteChr.equals("G")) {
                siteChr = "W";
            } else {
                siteChr = "G";
            }
            getFacadeContainer().getNotifier().warn(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "CNFRET - retrying cell search in {0}. CellID: {1,number,#}. LAC: {2,number,#}.",
                            siteChr.equals("W") ? "3G" : "2G", identityNum, lac));
            try {
                Query q = em.createQuery(sql);
                q.setParameter("identityNum", identityNum);
                q.setParameter("siteChr", siteChr);
                q.setMaxResults(1);
                Cell result = (Cell) q.getSingleResult();
                return result;
            } catch (Exception ex) {

                getFacadeContainer().getNotifier().warn(
                        getClass(),
                        null,
                        MessageFormat.format(
                                "CNFLAST - last retrying cell search in Site. CellID:{0,number,#}. LAC: {1,number,#}",
                                identityNum, lac));
                try {

                    String identity = identityNum.toString().substring(0,
                            identityNum.toString().length() - 1).concat("%");

                    Query q = em.createQuery("SELECT c FROM Cell c "
                        + "WHERE c.identityNum like '" + identity + "' "
                        + "Order by c.versionNum desc");
                    q.setMaxResults(1);
                    Cell result = (Cell) q.getSingleResult();
                    return result;
                } catch (Exception exc) {
                    getFacadeContainer().getNotifier().warn(
                            getClass(),
                            null,
                            MessageFormat.format(
                                    "CNFNULL - cell not found. CellID:{0,number,#}. LAC: {1,number,#}",
                                    identityNum, lac));
                    return null;
                }

            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
