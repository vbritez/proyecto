package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.tigo.cs.api.facade.UserphoneAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.domain.Userphone;

@DependsOn("EJBFacadeContainer")
@Stateless
public class UserphoneFacade extends UserphoneAPI {

    @EJB
    protected FacadeContainer facadeContainer;
    @EJB
    protected I18nBean i18nBean;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public UserphoneFacade() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void editUserphone(Userphone entity) {
       try {
         super.edit(entity);
         facadeContainer.getEntityManager().refresh(entity);
       }catch(Exception e) {
           throw new EJBException(i18nBean.iValue("web.client.backingBean.message.Error"), e);
       }
    }
}
