package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataClient;
import java.lang.reflect.Method;
import javax.persistence.NoResultException;
import javax.persistence.Query;
/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataClientAPI extends AbstractAPI<DataClient> {

    public DataClientAPI() {
        super(DataClient.class);
    }

    public List<DataClient> findByClientMetaCodigo(Long codClient, Long codMeta, String codigo, String orderBy, String orderType) {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        finderParams.put("codeChr", codigo);
        finderParams.put("orderBy", codigo);
        finderParams.put("orderType", codigo);
        return findListWithNamedQuery("DataClient.findByClientMetaCodigo", finderParams);
    }


    public List<DataClient> findByClientMeta(Long codClient, Long codMeta) {
        finderParams = prepareParams();
        finderParams.put("codClient", codClient);
        finderParams.put("codMeta", codMeta);
        return findListWithNamedQuery("MetaData.findByCodClientCodMeta", finderParams);
    }
    
    public DataClient findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT dc "
                    + " FROM DataClient dc " 
                    + " WHERE dc.dataPK.codClient = :clientcod "
                    + " AND dc.dataPK.codMeta = :metaCod "
                    + " AND dc.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            return (DataClient)query.getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public List<DataClient> findByClientMetaClientName(Long codClient, Long codMeta, String clientName) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(""
                    + " SELECT dc "
                    + " FROM DataClient dc " 
                    + " WHERE dc.dataPK.codClient = :clientcod "
                    + " AND dc.dataPK.codMeta = :metaCod "
                    + " AND lower(dc.cliente) like '%".concat(clientName.toLowerCase()).concat("%' "));
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            return query.getResultList();
        }catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public void updateDataClientList(Userweb userweb, List<? extends Data> datas, Long codMeta) throws Exception{
        List<String> keys = new ArrayList<String>();

        // Para cada cliente de la lista
        for (Data data : datas) {
            Set<Integer> memberOrders = data.getMembers().keySet();
            // Para cada meta-member del meta
            for (Integer memberOrder : memberOrders) {
                String value = getMemberValue(data, data.getMembers().get(memberOrder));

                // Solo si el valor no es nulo, se intenta persistir.;
                if (value != null) {
                    MetaData md = new MetaData();
                    MetaDataPK mdPK = new MetaDataPK(userweb.getClient().getClientCod(), codMeta, memberOrder.longValue(), data.getCode());
                    md.setMetaDataPK(mdPK);
                    md.setValueChr(value);
                    try {
                        getFacadeContainer().getMetaDataAPI().edit(md);
                    } catch (Exception e) {
                        // Do nothing, just log
                    }
                }
            }
        }
    }
    
    private String getMemberValue(Object o, String methodName) {
        String memberValue = null;
        try {
            String capitalMethodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            Method method = o.getClass().getMethod("get" + capitalMethodName, (Class<?>[]) null);
            Object value = method.invoke(o, (Object[]) null);
            if (value != null) {
                memberValue = method.invoke(o, (Object[]) null).toString();
            }
        } catch (Exception e) {
            memberValue = null;
        }
        return memberValue;
    }
}
