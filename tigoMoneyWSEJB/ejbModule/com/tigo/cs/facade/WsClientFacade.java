package com.tigo.cs.facade;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.WsClient;

@Stateless
public class WsClientFacade extends AbstractFacade<WsClient> {

	public WsClientFacade() {
		super(WsClient.class);
	}

	public WsClient findByConsumerID(String consumerID) throws GenericFacadeException {
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query query = em
					.createNamedQuery("WsClient.findByConsumerId");
			query.setParameter("consumerId", consumerID);
			return (WsClient) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			notifier.error(null,WsClientFacade.class, null,
					e.getMessage(), e);
			throw new GenericFacadeException(this.getClass(), e);
		}
	}

}
