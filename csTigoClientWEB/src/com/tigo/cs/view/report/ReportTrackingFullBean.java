package com.tigo.cs.view.report;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Tania Nunez
 * @version $Id: ReportTrackingFullBean.java 233 2011-12-26 12:47:58Z
 *          miguel.maciel $
 */
@ManagedBean(name = "reportTrackingFullBean")
@ViewScoped
@ReportFile(fileName = "rep_tracking_full")
public class ReportTrackingFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -4721683026326311462L;

    public ReportTrackingFullBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0
            && !parametersValidatedChart) {
            reportName = "rep_tracking_full_classification";
        } else {
            reportName = "rep_tracking_full";
        }
        return reportName;
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
                        + "and SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd') "
                        + "and SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd') "),
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

}
