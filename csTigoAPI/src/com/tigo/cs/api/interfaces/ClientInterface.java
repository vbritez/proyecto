package com.tigo.cs.api.interfaces;

import javax.persistence.EntityManager;

import com.tigo.cs.api.facade.AuditoryAPI;
import com.tigo.cs.api.facade.AuditoryActionAPI;
import com.tigo.cs.api.facade.ClientAPI;
import com.tigo.cs.api.facade.ClientFileAPI;
import com.tigo.cs.api.facade.ClientParameterValueAPI;
import com.tigo.cs.api.facade.ClientServiceFunctionalityAPI;
import com.tigo.cs.api.facade.GlobalParameterAPI;
import com.tigo.cs.api.facade.I18nAPI;
import com.tigo.cs.api.facade.NotifierAPI;
import com.tigo.cs.api.facade.RoleClientAPI;
import com.tigo.cs.api.facade.RoleClientScreenAPI;
import com.tigo.cs.api.facade.ServiceFunctionalityAPI;

public interface ClientInterface {

    public EntityManager getEntityManager();

    public boolean isEntityManagerTransactional();

    public NotifierAPI getNotifier();

    public I18nAPI getI18nAPI();

    public GlobalParameterAPI getGlobalParameterAPI();

    public AuditoryActionAPI getAuditoryActionAPI();

    public AuditoryAPI getAuditoryAPI();

    public ClientAPI getClientAPI();

    public ClientParameterValueAPI getClientParameterValueAPI();

    public ClientFileAPI getClientFileAPI();

    public RoleClientAPI getRoleClientAPI();

    public ClientServiceFunctionalityAPI getClientServiceFunctionalityAPI();

    public RoleClientScreenAPI getClientScreenAPI();

    public ServiceFunctionalityAPI getServiceFunctionalityAPI();

}
