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
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportCollectionFullBean")
@ViewScoped
@ReportFile(fileName = "rep_collection_full")
public class ReportCollectionFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 8778484791335065155L;

    public ReportCollectionFullBean() {
        this.chartReportOption = "invoice";
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_collection_full_classification";
        } else {
            reportName = "rep_collection_full";
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
        sqlWhere += MessageFormat.format(
                " and EXISTS "
                    + "(select * from USERPHONE_CLASSIFICATION uc "
                    + " inner join CLASSIFICATION c on c.CLASSIFICATION_cod = uc.cod_CLASSIFICATION "
                    + " where uc.cod_userphone = u.userphone_cod "
                    + " AND c.cod_client = u.cod_client "
                    + " and uc.cod_classification in ({0})) ", classifications);

        if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlWhere = String.format(
                    sqlWhere.concat(" and sv.ENABLED_CHR = '1'"
                        + " and svd.ENABLED_CHR = '1'"
                        + " AND SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd')"
                        + " AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }

        if (selectedUserphoneList != null && selectedUserphoneList.size() > 0) {
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
                for (Classification c : selectedClassificationList) {
                    Classification managedClassification = classificationFacade.find(
                            c.getClassificationCod(), "userphoneList");

                    for (Userphone u : managedClassification.getUserphoneList()) {
                        valor += u.getUserphoneCod().toString() + ",";
                    }
                    valor = valor.substring(0, valor.length() - 1);
                    sqlWhere = sqlWhere.concat(" AND U.USERPHONE_COD IN ("
                        + valor + ")");
                }

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

    @Override
    public String generateNativeQueryForChart() {
        String sql = null;
        if (chartReportOption.equals("invoice")) {
            sql = " SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + " FROM SERVICE_VALUE_DETAIL svd "
                + " INNER JOIN SERVICE_VALUE sv ON (sv.SERVICEVALUE_COD = svd.COD_SERVICEVALUE) "
                + " INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + " INNER JOIN MESSAGE m ON (sv.COD_MESSAGE = m.MESSAGE_COD) "
                + " WHERE sv.COD_SERVICE = 5 AND SVD.COLUMN1_CHR = 'INVOICE'"
                + " %s " + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());
        } else if (chartReportOption.equals("paymentcash")) {
            sql = " SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + " FROM SERVICE_VALUE_DETAIL svd "
                + " INNER JOIN SERVICE_VALUE sv ON (sv.SERVICEVALUE_COD = svd.COD_SERVICEVALUE) "
                + " INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + " INNER JOIN MESSAGE m ON (sv.COD_MESSAGE = m.MESSAGE_COD) "
                + " WHERE sv.COD_SERVICE = 5 AND svd.COLUMN2_CHR = '1' and SVD.COLUMN1_CHR = 'PAYMENT' "
                + " %s " + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());

        } else if (chartReportOption.equals("paymentcheck")) {
            sql = " SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + " FROM SERVICE_VALUE_DETAIL svd "
                + " INNER JOIN SERVICE_VALUE sv ON (sv.SERVICEVALUE_COD = svd.COD_SERVICEVALUE) "
                + " INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + " INNER JOIN MESSAGE m ON (sv.COD_MESSAGE = m.MESSAGE_COD) "
                + " WHERE sv.COD_SERVICE = 5 AND svd.COLUMN2_CHR <> '1' AND SVD.COLUMN1_CHR = 'PAYMENT' "
                + " %s " + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());

        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null) {
            chartReportOptionLabel = i18n.iValue("collection.chart.option.Userphone");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("invoice")) {
            title = i18n.iValue("collection.chart.label.InvoiceNumberByUserphone");
        } else if (chartReportOption != null
            && chartReportOption.equals("paymentcash")) {
            title = i18n.iValue("collection.chart.label.CashPaymentNumberByUserphone");
        } else if (chartReportOption != null
            && chartReportOption.equals("paymentcheck")) {
            title = i18n.iValue("collection.chart.label.CheckPaymentNumberByUserphone");
        }
        return title;
    }

}
