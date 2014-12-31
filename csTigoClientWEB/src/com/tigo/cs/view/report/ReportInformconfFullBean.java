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
 * @author Miguel Zorrilla
 * @version $Id: ReportInformconfFullBean.java 233 2011-12-26 12:47:58Z
 *          miguel.maciel $
 */
@ManagedBean(name = "reportInformconfFullBean")
@ViewScoped
@ReportFile(fileName = "rep_informconf_full")
public class ReportInformconfFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 5607860638160350484L;

    public ReportInformconfFullBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_informconf_full_classification";
        } else {
            reportName = "rep_informconf_full";
        }

        return reportName;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,M.DATEIN_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,M.DATEIN_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR, M.DATEIN_DAT";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY M.DATEIN_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY M.DATEIN_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY u.NAME_CHR, M.DATEIN_DAT";
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
                        + " AND SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd') "
                        + " AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd') "),
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
