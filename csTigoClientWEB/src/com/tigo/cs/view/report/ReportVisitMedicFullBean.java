package com.tigo.cs.view.report;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportVisitMedicFullBean")
@ViewScoped
@ReportFile(fileName = "rep_visitmedic_full")
public class ReportVisitMedicFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -590867472448645563L;
    private String reportOption = "grouped";

    public ReportVisitMedicFullBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (reportOption.equals("grouped")) {
                reportName = "rep_visitmedic_grouped_classification";
            } else {
                reportName = "rep_visitmedic_full_classification";
            }
            
        } else {
            if (reportOption.equals("grouped")) {
                reportName = "rep_visitmedic_grouped";
            } else {
                reportName = "rep_visitmedic_full";
            }
            
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
            params.put("ORDER_BY", getOrderByReport());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
            params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
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
    public String getOrderByReport() {
        String sqlOrderBy = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (reportOption.equals("grouped")) {
                if (selectedOrderby.equals("FD")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SVD.RECORDDATE_DAT DESC,svd.SERVICEVALUEDETAIL_COD";
                } else if (selectedOrderby.equals("FA")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SVD.RECORDDATE_DAT,svd.SERVICEVALUEDETAIL_COD";
                } else if (selectedOrderby.equals("US")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR,svd.SERVICEVALUEDETAIL_COD, SVD.RECORDDATE_DAT";
                } 
            }else{
                if (selectedOrderby.equals("FD")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,me.DATEIN_DAT DESC";            
                } else if (selectedOrderby.equals("FA")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,me.DATEIN_DAT";
                } else if (selectedOrderby.equals("US")) {
                    sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR, me.DATEIN_DAT";
                }
            }
            

        } else {
            if (reportOption.equals("grouped")) {
                if (selectedOrderby.equals("FD")) {
                    sqlOrderBy = " ORDER BY SVD.RECORDDATE_DAT DESC,svd.SERVICEVALUEDETAIL_COD";
                } else if (selectedOrderby.equals("FA")) {
                    sqlOrderBy = " ORDER BY SVD.RECORDDATE_DAT,svd.SERVICEVALUEDETAIL_COD";
                } else if (selectedOrderby.equals("US")) {
                    sqlOrderBy = " ORDER BY u.NAME_CHR,svd.SERVICEVALUEDETAIL_COD, SVD.RECORDDATE_DAT";
                } 
            }else{
                if (selectedOrderby.equals("FD")) {
                    sqlOrderBy = " ORDER BY me.DATEIN_DAT DESC ";
                } else if (selectedOrderby.equals("FA")) {
                    sqlOrderBy = " ORDER BY me.DATEIN_DAT ";
                } else if (selectedOrderby.equals("US")) {
                    sqlOrderBy = " ORDER BY u.NAME_CHR, me.DATEIN_DAT ";
                }
            }
            
        }

        return sqlOrderBy;
    }

    @Override
    public String getWhereReport() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        String valor = "";
        sqlWhere = String.format(sqlWhere.concat(" AND U.COD_CLIENT = %s"),
                clientCod.toString());

        if (validatedAllUsers) {
            sqlWhere += MessageFormat.format(" and EXISTS "
                + "(select * from USERPHONE_CLASSIFICATION uc "
                + "where uc.cod_userphone = u.userphone_cod "
                + "and uc.cod_classification in ({0})) ", classifications);
        }

        if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlWhere = String.format(
                    sqlWhere.concat(" and sv.ENABLED_CHR = '1' "
                        + "AND SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-MM-dd') "
                        + "AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-MM-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }

        if (selectedUserphoneList != null && selectedUserphoneList.size() > 0
            && !validatedAllUsers) {
            for (Userphone u : selectedUserphoneList) {
                valor += u.getUserphoneCod().toString() + ",";
            }
            if (valor.length() > 0) {
                valor = valor.substring(0, valor.length() - 1);
                sqlWhere = sqlWhere.concat(" AND U.USERPHONE_COD IN (" + valor
                    + ")");
            }

        }

        if (validatedAllClassification && selectedClassificationList == null) {
            try {
                selectedClassificationList = new ArrayList<Classification>();
                selectedClassificationList = classificationFacade.findListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }
        }

        if (parametersValidatedChart) {
            if (selectedClassificationList != null
                && selectedClassificationList.size() > 0
                || validatedAllClassification) {
                String classificationsCod = "";
                for (Classification c : selectedClassificationList) {
                    classificationsCod += c.getClassificationCod().toString()
                        + ",";
                }
                classificationsCod = classificationsCod.substring(0,
                        classificationsCod.length() - 1);
                sqlWhere += MessageFormat.format(" and EXISTS "
                    + "(select * from USERPHONE_CLASSIFICATION uc "
                    + "where uc.cod_userphone = u.userphone_cod "
                    + "and uc.cod_classification in ({0})) ",
                        classificationsCod);
            }
        }

        if (!parametersValidatedChart) {
            if (selectedClassificationList != null
                && selectedClassificationList.size() > 0) {
                for (Classification c : selectedClassificationList) {
                    valor += c.getClassificationCod().toString() + ",";
                }
                if (valor.length() > 0) {
                    valor = valor.substring(0, valor.length() - 1);
                    sqlWhere = sqlWhere.concat(" AND CL.CLASSIFICATION_COD IN ("
                        + valor + ")");
                }

            }
        }
        return sqlWhere;
    }

    public String getReportOption() {
        return reportOption;
    }

    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

}
