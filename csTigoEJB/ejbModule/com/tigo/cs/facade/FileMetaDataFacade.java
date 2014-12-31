package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.FileMetaDataAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class FileMetaDataFacade extends FileMetaDataAPI {
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
