package com.tigo.cs.api.facade;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.ClientParameterValue;
import com.tigo.cs.domain.ClientParameterValuePK;
import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.RoleClient;
import com.tigo.cs.domain.RoleClientScreen;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.domain.ServiceFunctionalityPK;
import com.tigo.cs.domain.Useradmin;

public abstract class ClientAPI extends AbstractAPI<Client> {

    private final String LOGO_PARAM_STR = "client.image.logo";

    public ClientAPI() {
        super(Client.class);
    }

    @Override
    public Client edit(Client entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            Client clientManaged = find(entity.getClientCod(),
                    "clientFeatures", "clientServiceFunctionalityList");
            clientManaged.setUssdProfileId(entity.getUssdProfileId());
            clientManaged.setSystemcodeChr(entity.getSystemcodeChr());
            clientManaged.setCreatedDate(entity.getCreatedDate());
            clientManaged.setDemoChr(entity.getDemoChr());
            clientManaged.setDemoCount(entity.getDemoCount());
            clientManaged.setDemoExpirationDate(entity.getDemoExpirationDate());
            clientManaged.setDescriptionChr(entity.getDescriptionChr());
            clientManaged.setDisabledByDemoExpChr(entity.getDisabledByDemoExpChr());
            clientManaged.setEnabledChr(entity.getEnabledChr());
            clientManaged.setNameChr(entity.getNameChr());
            clientManaged.setRegNonexistentMeta(entity.getRegNonexistentMeta());
            clientManaged.setSeller(entity.getSeller());
            clientManaged.setSendConfirm(entity.getSendConfirm());
            clientManaged.setSystemcodeChr(entity.getSystemcodeChr());
            clientManaged.setClientFeatures(entity.getClientFeatures());
            clientManaged.setClientServiceFunctionalityList(entity.getClientServiceFunctionalityList());

            em.merge(clientManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return clientManaged;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClientAPI.class, null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Client> findAllEnabled() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Client.findByEnabledChr");
            query.setParameter("enabledChr", true);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);

        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> findServices(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT DISTINCT csfL.serviceFunctionality.service "
                + "FROM Client c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.clientCod = :clientcod");
            query.setParameter("clientcod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceFunctionality> getServiceFunctionalitiesByClient(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT csfL.serviceFunctionality "
                + "FROM Client c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.clientCod = :clientcod "
                + "ORDER BY csfL.serviceFunctionality.service.descriptionChr, csfL.serviceFunctionality.functionality.descriptionChr");
            query.setParameter("clientcod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceFunctionality> getServiceFunctionalitiesByClient(Long clientCod, Long idFunctionality) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT csfL.serviceFunctionality "
                + "FROM Client c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.clientCod = :clientcod "
                + "AND csfL.serviceFunctionality.functionality.functionalityCod = :functionality "
                + "ORDER BY csfL.serviceFunctionality.service.descriptionChr, csfL.serviceFunctionality.functionality.descriptionChr ");
            query.setParameter("clientcod", clientCod);
            query.setParameter("functionality", idFunctionality);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String saveClientParameters(Client entity, ClientFile clientFile) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            if (entity.getClientCod() != null) {

                Client client = this.find(entity.getClientCod());
                List<ClientParameterValue> newParamList = entity.getClientParameterValueList();
                String oldFileName = null;
                for (ClientParameterValue cpv : newParamList) {
                    ClientParameterValue param = getFacadeContainer().getClientParameterValueAPI().findByPK(
                            cpv.getClient().getClientCod(),
                            cpv.getClientParameter().getClientParameterCod());
                    if (param != null) {
                        oldFileName = param.getValueChr();

                        param.setValueChr(cpv.getValueChr());
                        getFacadeContainer().getClientParameterValueAPI().edit(
                                param);

                        if (cpv.getClientParameter().getClientParameterCod().equals(
                                LOGO_PARAM_STR)
                            && clientFile.getFilenameChr() != null) {
                            ClientFile cf = getFacadeContainer().getClientFileAPI().getClientLogo(
                                    client, oldFileName);
                            if (cf != null) {
                                cf.setDescriptionChr(clientFile.getDescriptionChr());
                                cf.setFileByt(clientFile.getFileByt());
                                cf.setFilenameChr(clientFile.getFilenameChr());
                                cf.setFiletypeChr(clientFile.getFiletypeChr());
                                cf.setUploaddateDat(new Date());
                                cf.setUseradmin(new Useradmin(0L));
                                cf.setClient(client);
                                getFacadeContainer().getClientFileAPI().edit(cf);
                            } else {
                                clientFile.setClient(client);
                                clientFile.setUploaddateDat(new Date());
                                clientFile.setUseradmin(new Useradmin(0L));
                                getFacadeContainer().getClientFileAPI().create(
                                        clientFile);
                            }
                        }
                    } else {
                        ClientParameterValuePK pk = new ClientParameterValuePK(client.getClientCod(), cpv.getClientParameter().getClientParameterCod());
                        cpv.setClientParameterValuePK(pk);
                        getFacadeContainer().getClientParameterValueAPI().create(
                                cpv);
                        if (cpv.getClientParameter().getClientParameterCod().equals(
                                LOGO_PARAM_STR)) {
                            clientFile.setClient(client);
                            clientFile.setUploaddateDat(new Date());
                            clientFile.setUseradmin(new Useradmin(0L));
                            getFacadeContainer().getClientFileAPI().create(
                                    clientFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

        return null;
    }

    public String deleteLogo(Client entity) {
        try {

            Client client = this.find(entity.getClientCod());
            ClientFile cf = getFacadeContainer().getClientFileAPI().getClientLogo(
                    client);
            if (cf != null) {
                getFacadeContainer().getClientFileAPI().remove(cf);
                ClientParameterValue param = getFacadeContainer().getClientParameterValueAPI().findByPK(
                        entity.getClientCod(), LOGO_PARAM_STR);
                getFacadeContainer().getClientParameterValueAPI().remove(param);
            }

        } catch (Exception ejbe) {
            // throw new Exception(facadeContainer.getI18nAPI().iValue(
            // "web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotEditError"),
            // ejbe);
            return null;
        }

        return null;
    }

    /**
     * @param bccsClientCode
     *            BCCS code for client.
     * @return the client found by the method. Null if not found.
     */
    public Client findClientByBCCSClientCode(String bccsClientCode) throws MoreThanOneResultException, GenericFacadeException {
        try {
            return findEntityWithNamedQuery("Client.findBySystemcode",
                    addSingleParam("systemcodeChr", bccsClientCode));
        } catch (EmptyResultException e) {
            return null;
        } catch (MoreThanOneResultException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericFacadeException(getClass(), e);
        }
    }

    public List<ServiceFunctionality> getServiceFunctionalitiesByClientForUserweb(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery(""
                + "SELECT csfL.serviceFunctionality "
                + "FROM Client c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.clientCod = :clientcod "
                + "AND csfL.clientServiceFunctionalityPK.codFunctionality IN (0, 1) "
                + "ORDER BY csfL.serviceFunctionality.service.descriptionChr, csfL.serviceFunctionality.functionality.descriptionChr");
            query.setParameter("clientcod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ServiceFunctionality> getAllServiceFunctionalities() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("ServiceFunctionality.findAll");
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Long getUssdProfileId(Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT ussdProfileId  "
                + "FROM Client c " + "WHERE c.clientCod = :clientcod");
            query.setParameter("clientcod", client.getClientCod());
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void removeClientservicefunctionalityList(Client client, List<ClientServiceFunctionality> list) throws GenericFacadeException {
        try {
            for (ClientServiceFunctionality csf : list) {
                List<RoleClient> rcList = getFacadeContainer().getRoleClientAPI().getRoleClientList(
                        client.getClientCod(),
                        csf.getServiceFunctionality().getService().getServiceCod(),
                        csf.getServiceFunctionality().getFunctionality().getFunctionalityCod());
                for (RoleClient rc : rcList) {
                    rc.getClientServiceFunctionalityList().remove(csf);
                    getFacadeContainer().getRoleClientAPI().edit(rc);
                }
                getFacadeContainer().getClientServiceFunctionalityAPI().remove(
                        csf);
            }

        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        }
    }

    public void removeRoleclientscreen(Client client, List<Meta> metalist, List<Service> serviceList) throws GenericFacadeException {
        try {
            for (Meta m : metalist) {
                List<RoleClientScreen> rcsList = getFacadeContainer().getRoleClientScreenAPI().getRoleclientscreenListByMeta(
                        client.getClientCod(), m.getMetaCod());
                for (RoleClientScreen rcs : rcsList) {
                    getFacadeContainer().getRoleClientScreenAPI().remove(rcs);
                }
            }

            for (Service s : serviceList) {
                List<RoleClientScreen> rcsList = getFacadeContainer().getRoleClientScreenAPI().getRoleclientscreenListByService(
                        client.getClientCod(), s.getServiceCod());
                for (RoleClientScreen rcs : rcsList) {
                    getFacadeContainer().getRoleClientScreenAPI().remove(rcs);
                }
            }

        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        }
    }

    public void saveClientServiceFunctionalityFeature(Client client) {
        ServiceFunctionality sf0 = getFacadeContainer().getServiceFunctionalityAPI().findByCodeAndFunctionality(
                100L, 0L);
        if (sf0 == null) {
            ServiceFunctionalityPK pk = new ServiceFunctionalityPK();
            pk.setCodService(100L);
            pk.setCodFunctionality(0L);
            sf0 = new ServiceFunctionality();
            sf0.setServiceFunctionalityPK(pk);
            getFacadeContainer().getServiceFunctionalityAPI().create(sf0);
        }
        ServiceFunctionality sf1 = getFacadeContainer().getServiceFunctionalityAPI().findByCodeAndFunctionality(
                100L, 1L);
        if (sf1 == null) {
            ServiceFunctionalityPK pk = new ServiceFunctionalityPK();
            pk.setCodService(100L);
            pk.setCodFunctionality(1L);
            sf1 = new ServiceFunctionality();
            sf1.setServiceFunctionalityPK(pk);
            getFacadeContainer().getServiceFunctionalityAPI().create(sf1);
        }

        ClientServiceFunctionality csf0 = getFacadeContainer().getClientServiceFunctionalityAPI().getClientServiceFunctionality(
                client.getClientCod(), 100L, 0L);
        if (csf0 == null) {
            csf0 = new ClientServiceFunctionality(client.getClientCod(), 100L, 0L);
            csf0.setClient(client);
            csf0.setServiceFunctionality(getFacadeContainer().getServiceFunctionalityAPI().findByCodeAndFunctionality(
                    100L, 0L));
            getFacadeContainer().getClientServiceFunctionalityAPI().create(csf0);
        }
        ClientServiceFunctionality csf1 = getFacadeContainer().getClientServiceFunctionalityAPI().getClientServiceFunctionality(
                client.getClientCod(), 100L, 1L);
        if (csf1 == null) {
            csf1 = new ClientServiceFunctionality(client.getClientCod(), 100L, 1L);
            csf1.setClient(client);
            csf1.setServiceFunctionality(getFacadeContainer().getServiceFunctionalityAPI().findByCodeAndFunctionality(
                    100L, 1L));
            getFacadeContainer().getClientServiceFunctionalityAPI().create(csf1);
        }
    }

    public void deleteClientServiceFunctionalityFeature(Client client) {
        ClientServiceFunctionality csf0 = getFacadeContainer().getClientServiceFunctionalityAPI().getClientServiceFunctionality(
                client.getClientCod(), 100L, 0L);
        if (csf0 != null)
            getFacadeContainer().getClientServiceFunctionalityAPI().remove(csf0);

        ClientServiceFunctionality csf1 = getFacadeContainer().getClientServiceFunctionalityAPI().getClientServiceFunctionality(
                client.getClientCod(), 100L, 1L);
        if (csf1 != null)
            getFacadeContainer().getClientServiceFunctionalityAPI().remove(csf1);
    }

    public List<Client> findClientForNoVist() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String sql = "SELECT c " + "FROM Client c "
                + "WHERE c.clientCod in (%s) ";

            sql = String.format(
                    sql,
                    getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "admin.report.NoVisitClients"));
            Query query = em.createQuery(sql);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientServiceFunctionality> getClientServiceFunctionalitiesByClientService(Long clientCod, Long codService) {
    	EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                + "SELECT DISTINCT csfL "
                + "FROM Client c, "
                + "IN (c.clientServiceFunctionalityList) csfL "
                + "WHERE c.clientCod = :clientcod "
                + "AND csfL.clientServiceFunctionalityPK.codService = :codService");
            query.setParameter("clientcod", clientCod);
            query.setParameter("codService", codService);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
