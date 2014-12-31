package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.SubscriberAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.domain.Subscriber;

@DependsOn("EJBFacadeContainer")
@Stateless
public class SubscriberFacade extends SubscriberAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public SubscriberFacade() {
    }

    @Override
    public Subscriber create(Subscriber entity) {

        Subscriber result = super.create(entity);
        getFacadeContainer().getEntityManager().flush();
        return result;
    }

}
