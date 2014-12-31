package com.tigo.cs.facade;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.tigo.cs.api.facade.MetaDataAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.ejb.ConstraintException;
import com.tigo.cs.commons.ejb.MassivePersistenceException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.Userweb;

@DependsOn("EJBFacadeContainer")
@Stateless
public class MetaDataFacade extends MetaDataAPI {

    @EJB
    protected FacadeContainer facadeContainer;
    
    
    @Resource private EJBContext context;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public MetaDataFacade() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public MetaData persistMetaData(MetaData md) throws Exception {
        return super.persistMetaData(md);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String deleteUserphoneMetadataRelation(Long codClient, Long codMeta) throws Exception {
        return super.deleteUserphoneMetadataRelation(codClient, codMeta);
    }
    
    @Override
    public void processMetadatas(Userweb userweb,HashMap<MetaDataPK, String> mdPks,HashMap<MetaDataPK, List<Long>> mdUserphone) 
    		throws MassivePersistenceException, GenericFacadeException,ConstraintException {
    	try{
    		super.processMetadatas(userweb,mdPks, mdUserphone);
    	}catch(MassivePersistenceException e) {
    		context.setRollbackOnly();
    		throw e;
    	}catch(GenericFacadeException e) {
    		context.setRollbackOnly();
    		throw e;
    	}catch(ConstraintException e) {
    		context.setRollbackOnly();
    		throw e;
    	}
    }

}
