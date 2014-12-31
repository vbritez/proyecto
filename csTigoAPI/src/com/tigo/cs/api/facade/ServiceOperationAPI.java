package com.tigo.cs.api.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.util.ServiceResult;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;

public abstract class ServiceOperationAPI extends AbstractAPI<ServiceValue> {
    
    public ServiceOperationAPI() {
        super(ServiceValue.class);
    }
    
    public List<ServiceResult> lastMarks(Id service, Userphone userphone,  Integer cant){
        List<ServiceResult> result = new ArrayList<ServiceResult>();
        EntityManager em = null;
        String mark = null;
        List<ServiceValue> svList = null;
        List<ServiceValueDetail> svdList = null;
        
        try {
            switch (service) {
            /*
             * COLUMN1 = EVENTO (ENT, SAL, ENTSAL)
             * COLUMN2 = CLIENTE
             * */
            case VISITA:
            case VISITA_PEDIDO:  
                svdList = getServiceValueDetailList(" AND (o.column1Chr = 'ENT' OR o.column1Chr = 'ENTSAL') ",
                        userphone.getUserphoneCod(), service.value(), cant);

                if (svdList != null) {
                    mark = null;
                    for (ServiceValueDetail svd : svdList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.CLIENT.value(), 1L,
                                svd.getColumn2Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(svd.getColumn2Chr(), retValue,
                                svd.getRecorddateDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValueDetail(svd);
                        result.add(sr);
                    }
                }
                break;
                 
            /*
             * COLUMN1 = COD CLIENTE
             * */
            case PEDIDO:                
                svList = getServiceValueList(" AND (o.column9Chr = '0' OR o.column9Chr IS NULL) ",
                        userphone.getUserphoneCod(), service.value(), cant);
                    
                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.CLIENT.value(), 1L,
                                sv.getColumn1Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(sv.getColumn1Chr(), retValue,
                                sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break; 
                
            /*
             * COLUMN1 = COD CLIENTE
             */
            case COBRANZA:                
                svList = getServiceValueList("", userphone.getUserphoneCod(),
                        service.value(), cant);

                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.CLIENT.value(), 1L,
                                sv.getColumn1Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(sv.getColumn1Chr(), retValue,
                                sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break;
            
            /*
             * COLUMN1 = DEPOSITO
             * COLUMN2 = COD CLIENTE
             * COLUMN3 = CANTIDAD
             */
            case INVENTARIO_STANDARD:                
                svdList = getServiceValueDetailList("", userphone.getUserphoneCod(),
                        service.value(), cant);

                if (svdList != null) {
                    mark = null;
                    for (ServiceValueDetail svd : svdList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.PRODUCT.value(), 1L,
                                svd.getColumn2Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(svd.getColumn2Chr(), retValue, svd.getRecorddateDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValueDetail(svd);
                        result.add(sr);
                    }
                }
                break;     
            
            /*
             * COLUMN1 = NOMBRE PERSONA QEU RECIBIO
             */
            case COURRIER:
                svList = getServiceValueList("", userphone.getUserphoneCod(),
                        service.value(), cant);

                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        if (sv.getColumn1Chr() != null){
                            mark = formatResult(sv.getColumn1Chr(), "" ,
                                    sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");
                        
                        }else{
                            MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                    userphone.getClient().getClientCod(),
                                    MetaNames.MOTIVE.value(), 1L,
                                    sv.getColumn3Chr());
                            String retValue = "";
                            if (md != null) {
                                retValue = md.getValueChr();
                            }
                            mark = formatResult(sv.getColumn3Chr(), retValue,
                                    sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");
                        }
                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break;
            
            /*
             * COLUMN1 = CHOFER
             */    
            case FLOTA:
                svList = getServiceValueList("", userphone.getUserphoneCod(),
                        service.value(), cant);

                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.EMPLOYEE.value(), 1L,
                                sv.getColumn1Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(sv.getColumn1Chr(), retValue,
                                sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break; 
                    
            /* COLUMN1 = COD SANATORIO
             * COLUMN2 = COD MEDICO
             */   
            case VISITA_MEDICA:                
                svList = getServiceValueList(
                        " AND o.column3Chr = 'ME' ",
                        userphone.getUserphoneCod(), service.value(), cant);

                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.MEDIC.value(), 1L,
                                sv.getColumn2Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(sv.getColumn2Chr(), retValue,
                                sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break;
                
            /*
             * COLUMN1 = NOMBRE PERSONA QEU RECIBIO
             */
            case DELIVERY:
                svList = getServiceValueList("", userphone.getUserphoneCod(),
                        service.value(), cant);

                if (svList != null) {
                    mark = null;
                    for (ServiceValue sv : svList) {
                        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                userphone.getClient().getClientCod(),
                                MetaNames.CLIENT.value(), 1L,
                                sv.getColumn1Chr());
                        String retValue = "";
                        if (md != null) {
                            retValue = md.getValueChr();
                        }
                        mark = formatResult(sv.getColumn1Chr(), retValue,
                                sv.getMessage().getDateinDat(), "dd-MM-yy HH:mm");

                        ServiceResult sr = new ServiceResult();
                        sr.setResult(mark);
                        sr.setServiceValue(sv);
                        result.add(sr);
                    }
                }
                break;
    
            default:
                break;
            }
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private String formatResult(String code, String value, Date date, String datePattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        String res = "";
        
        if (code != null && code.length() > 0)
            res = code;
        
        if (value != null && value.length() > 0) {
            if (res.length() > 0)
                res = res.concat(",").concat(value);
            else
                res = value;
        }

        Integer markingSize;
        try {
            markingSize = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode("service.operation.marking.lenght"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            markingSize = 35;
        } catch (GenericFacadeException e) {
            e.printStackTrace();
            markingSize = 35;
        }
        
        if (res.length() > (markingSize - datePattern.length()))
            res = res.substring(0, (markingSize - datePattern.length())-1);
        
        if (res.length() > 0)
            res = res.concat(",");
        
        res = res.concat(sdf.format(date));
        return res;
    }

    private List<ServiceValue> getServiceValueList(String where, Long userphoneCod, Long serviceCod, Integer cant){
        EntityManager em = null;
        try{
        String sql =  " SELECT o "
                    + " FROM ServiceValue o "
                    + " WHERE o.enabledChr = true "
                    + " AND o.userphone.userphoneCod = :userphoneCod "
                    + " AND o.service.serviceCod = :serviceCod "
                    + " AND trunc(o.recorddateDat) <= :date "
                    + where
                    + " ORDER BY o.servicevalueCod DESC ";
        
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(sql);
            query.setParameter("userphoneCod", userphoneCod);
            query.setParameter("serviceCod", serviceCod);
            query.setParameter("date", new Date());
            query.setMaxResults(cant);
            List<ServiceValue> svList = query.getResultList();
            return svList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private List<ServiceValueDetail> getServiceValueDetailList(String where, Long userphoneCod, Long serviceCod, Integer cant){
        EntityManager em = null;
        try {
            String sql = " SELECT o " 
                        + " FROM ServiceValueDetail o "
                        + " WHERE o.enabledChr = true "
                        + " AND o.serviceValue.enabledChr = true "
                        + " AND o.serviceValue.userphone.userphoneCod = :userphoneCod "
                        + " AND o.serviceValue.service.serviceCod = :serviceCod   "
                        + " AND trunc(o.recorddateDat) <= :date "
                        + where
                        + " ORDER BY o.servicevaluedetailCod DESC ";

            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(sql);
            query.setParameter("userphoneCod", userphoneCod);
            query.setParameter("serviceCod", serviceCod);
            query.setParameter("date", new Date());
            query.setMaxResults(cant);
            List<ServiceValueDetail> svdList = query.getResultList();
            return svdList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
}
    
}
