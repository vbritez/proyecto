package com.tigo.cs.api.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MapMark;

public abstract class MapMarkAPI extends AbstractAPI<MapMark> {

	public MapMarkAPI() {
		super(MapMark.class);
	}

	public MapMark findByClientLatLng(Client client, Double latitude,
			Double lonitude) {
		try {
			finderParams = prepareParams();
			finderParams.put("client", client);
			finderParams.put("latitudeNum", latitude);
			finderParams.put("longitudeNum", lonitude);
			return super.findEntityWithNamedQuery(
					"MapMark.findByLatitudeNumAndLongitudeNumAndClient",
					finderParams, 1);
		} catch (EmptyResultException ex) {
			return null;
		} catch (MoreThanOneResultException ex) {
			return null;
		}
	}

	public void removeByClientLatLng(Client client, Double latitude,
			Double lonitude) throws Exception {

		finderParams = prepareParams();
		finderParams.put("client", client);
		finderParams.put("latitudeNum", latitude);
		finderParams.put("longitudeNum", lonitude);
		super.executeUpdateWithNamedQuery(
				"MapMark.deleteByLatitudeNumAndLongitudeNumAndClient",
				finderParams);

	}

	public MapMark editByClientLatLng(Client client, Double latitude,
			Double lonitude, String title, String desc) {
		try {
			// getEntityManager().flush();
			finderParams = prepareParams();
			finderParams.put("client", client);
			finderParams.put("latitudeNum", latitude);
			finderParams.put("longitudeNum", lonitude);
			MapMark mapMarks = super.findEntityWithNamedQuery(
					"MapMark.findByLatitudeNumAndLongitudeNumAndClient",
					finderParams);
			mapMarks.setTitleChr(title);
			mapMarks.setDescriptionChr(desc);
			super.edit(mapMarks);
			return mapMarks;
		} catch (EmptyResultException ex) {
			return null;
		} catch (MoreThanOneResultException ex) {
			return null;
		}
	}

//	public List<MapMark> findClientMarks(Client client) {
//		finderParams = prepareParams();
//		finderParams.put("codClient", client.getClientCod());
//		List<MapMark> list = super.findListWithNamedQuery("MapMark.findByCodClient",
//				finderParams);
//		
//		return list;
//
//	}
	
	public List<MapMark> findClientMarks(Client client) {
//		finderParams = prepareParams();
//		finderParams.put("codClient", client.getClientCod());
//		List<MapMark> list = super.findListWithNamedQuery("MapMark.findByCodClient",finderParams);
//		
//		return list;
		List<MapMark> mList = new ArrayList<MapMark>();
		EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery("select m.* from CSTIGO.MAP_MARK m where m.COD_CLIENT = " + client.getClientCod());
            List<Object[]> list = (List<Object[]>) q.getResultList();
            if (list != null){
            	for (Object[] obj : list) {
					MapMark m = new MapMark();
					m.setMapMarkCod(((BigDecimal)obj[0]).longValue());
					m.setLatitudeNum(((BigDecimal)obj[2]).doubleValue());
					m.setLongitudeNum(((BigDecimal)obj[3]).doubleValue());
					m.setTitleChr(obj[5] != null ? (String)obj[5] : null);
					m.setDescriptionChr(obj[6] != null ? (String)obj[6] : null);
					mList.add(m);
				}
            }
            return mList;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UserphoneAPI.class, null,
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
