package com.tigo.cs.view.report;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Miguel Maciel
 * @version $Id: ReportSMSViewerFullBean.java 233 2011-12-26 12:47:58Z
 *          miguel.maciel $
 */
@ManagedBean(name = "reportSMSViewerFullBean")
@ViewScoped
@ReportFile(fileName = "rep_smsviewer_full")
public class ReportSMSViewerFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 1247641208083457498L;
    @EJB
    protected UserwebFacade userwebFacade;

    private List<Service> serviceList;
    private Service selectedService;
    private String reportOption = "success";
    private String where2;


    public ReportSMSViewerFullBean() {
    }
    
    @Override
    public String getReportName() {
        String reportName = "";
            if (reportOption.equals("success")) {
                reportName = "rep_smsviewer_full_success";
            } else {
                reportName = "rep_smsviewer_full_notsuccess";
            }
            
        return reportName;
    }
    
    
    @Override
    public String generateReport() {
        if (!parametersValidated) {
            return validateParameters();
        }
        try {
            signalReport(ReportType.PDF);
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("WHERE", getWhereReport());
            params.put("WHERE2", getWhere2()); 
            
            params.put("ORDER_BY", getOrderByReport());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
            params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
            params.put(JRParameter.REPORT_RESOURCE_BUNDLE,
                    i18n.getResourceBundle());

            ClientFile cf = fileFacade.getClientLogo(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            try {
                if (cf != null) {
                    params.put("CLIENT_LOGO",
                            JRImageLoader.loadImage(cf.getFileByt()));
                }
            } catch (JRException ex) {
            }

            String reportName = getReportName();

            if (reportName != null) {
                if (reportType.equals("xls")) {
                    reportName += "_xls";
                }
            }
            Connection conn = SMBaseBean.getDatabaseConnecction();
            if (reportType.equals("pdf")) {
                JasperReportUtils.respondReport(reportName, params, true, conn,
                        ReportType.PDF);
            } else if (reportType.equals("xls")) {
                JasperReportUtils.respondReport(reportName, params, true, conn,
                        ReportType.XLS);
            }

            parametersValidated = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getWhereReport() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        String valor = "";

        sqlWhere = String.format(sqlWhere.concat(" AND u.COD_CLIENT = %s"), clientCod.toString());
        sqlWhere += MessageFormat.format(" and EXISTS "
            + "(select * from USERPHONE_CLASSIFICATION uc "
            + "where uc.cod_userphone = u.userphone_cod "
            + "and uc.cod_classification in ({0})) ", classifications);
        
        if (selectedUserphoneList != null) {
            for (Userphone u: selectedUserphoneList){
                 valor += u.getUserphoneCod().toString() + ","; 
             }
            valor = valor.substring(0, valor.length()-1);
            sqlWhere = sqlWhere.concat(" AND u.USERPHONE_COD IN (" + valor + ")");
            
        }
        
        
        if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            
            sqlWhere = String.format(
                    sqlWhere.concat(" AND m.datein_dat BETWEEN TO_TIMESTAMP('%s 00:00:00', 'yyyy-mm-dd hh24:mi:ss') AND TO_TIMESTAMP('%s 23:59:59', 'yyyy-mm-dd hh24:mi:ss')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
            
            
            
        }

        where2 = sqlWhere;
        if (selectedService != null) {
            if(getReportOption().equals("success")) {
                sqlWhere = String.format(sqlWhere.concat(" AND sv.COD_SERVICE = %s"), selectedService.getServiceCod().toString());
                sqlWhere += " and sv.ENABLED_CHR = '1' ";
            
            }else{
                sqlWhere += String.format(" AND REPLACE(SUBSTR(m.MESSAGEIN_CHR,instr(m.MESSAGEIN_CHR,\'\"\',-1,1)+2),\'}\','') = '%s'",selectedService.getServiceCod().toString());
            }
            
        }
        
        
        return sqlWhere;
    }

    @Override
    public String getParameterDescriptionReport() {
        String parametersDesc = super.getParameterDescriptionReport();

        if (selectedService != null) {
            parametersDesc = String.format(parametersDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescService")), selectedService.getDescriptionChr());
        }

        return parametersDesc;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if (selectedOrderby.equals("FD")) {
            sqlOrderBy = " ORDER BY datein_dat DESC";
        } else if (selectedOrderby.equals("FA")) {
            sqlOrderBy = " ORDER BY datein_dat";
        } else if (selectedOrderby.equals("US")) {
            sqlOrderBy = " ORDER BY name_chr, datein_dat";
        }
        return sqlOrderBy;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    @Override
    public List<Service> getServiceList() {

        if (serviceList == null) {
            try {
                return userwebFacade.getClientServices(SecurityBean.getInstance().getLoggedInUserClient().getUserwebCod());
            } catch (GenericFacadeException ex) {
                java.util.logging.Logger.getLogger(ReportSMSViewerFullBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }


    public String getWhere2() {
        return where2;
    }

    public void setWhere2(String where2) {
        this.where2 = where2;
    }

    public String getReportOption() {
        return reportOption;
    }

    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    
    

}
