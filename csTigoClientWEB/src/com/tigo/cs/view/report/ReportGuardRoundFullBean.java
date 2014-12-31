package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportGuardRoundFullBean")
@ViewScoped
@ReportFile(fileName = "rep_guardround_full")
public class ReportGuardRoundFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 5663179495203891404L;

    public ReportGuardRoundFullBean() {
        this.chartReportOption = "marking";
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_guardround_full_classification";
        } else {
            reportName = "rep_guardround_full";
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
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,SV.SERVICEVALUE_COD DESC, sv.RECORDDATE_DAT DESC, SVD.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SV.SERVICEVALUE_COD, sv.RECORDDATE_DAT, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR, SV.SERVICEVALUE_COD, sv.RECORDDATE_DAT, SVD.RECORDDATE_DAT";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD DESC, sv.RECORDDATE_DAT DESC, SVD.RECORDDATE_DAT DESC ";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD, sv.RECORDDATE_DAT, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY u.NAME_CHR, SV.SERVICEVALUE_COD, sv.RECORDDATE_DAT, SVD.RECORDDATE_DAT";
            }
        }
        return sqlOrderBy;
    }

    @Override
    public String generateNativeQueryForChart() {
        String sql = null;
        if (chartReportOption.equals("marking")) {
            sql = "SELECT CASE WHEN (mc.ENABLED_CHR = '1' AND md.value_chr IS NOT NULL) THEN md.VALUE_CHR ELSE '"
                + i18n.iValue("web.client.label.NoDescription")
                + "' END, "
                + "Q.* "
                + "FROM ( "
                + "		SELECT SV.COLUMN1_CHR, COUNT(*) AS COUNT "
                + "        FROM SERVICE_VALUE_DETAIL svd "
                + "        INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "        INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "        INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + " WHERE 1 = 1 "
                + " AND SV.COD_SERVICE = 15 AND SVD.COLUMN2_CHR IS NUll "
                + " %s "
                + " GROUP BY SV.COLUMN1_CHR ) Q "
                + " LEFT JOIN META_DATA md ON (md.COD_CLIENT = %s AND md.COD_META = 4 AND md.COD_MEMBER = 1 AND TRIM(md.CODE_CHR) = TRIM(Q.COLUMN1_CHR)) "
                + " LEFT JOIN META_CLIENT mc ON (mc.COD_CLIENT = %s AND mc.COD_META = 4) ";

            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
            sql = String.format(sql, getWhereReport(), codClient, codClient);
        } else if (chartReportOption.equals("nomarking")) {
            sql = "SELECT CASE WHEN (mc.ENABLED_CHR = '1' AND md.value_chr IS NOT NULL) THEN md.VALUE_CHR ELSE '"
                + i18n.iValue("web.client.label.NoDescription")
                + "' END, "
                + "Q.* "
                + "FROM ( "
                + "		SELECT SV.COLUMN1_CHR, COUNT(*) AS COUNT "
                + "        FROM SERVICE_VALUE_DETAIL svd "
                + "        INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "        INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "        INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + " WHERE 1 = 1 "
                + " AND SV.COD_SERVICE = 15 AND SVD.COLUMN2_CHR IS NOT NUll "
                + " %s "
                + " GROUP BY SV.COLUMN1_CHR ) Q "
                + " LEFT JOIN META_DATA md ON (md.COD_CLIENT = %s AND md.COD_META = 4 AND md.COD_MEMBER = 1 AND TRIM(md.CODE_CHR) = TRIM(Q.COLUMN1_CHR)) "
                + " LEFT JOIN META_CLIENT mc ON (mc.COD_CLIENT = %s AND mc.COD_META = 4) ";

            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
            sql = String.format(sql, getWhereReport(), codClient, codClient);
        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null) {
            chartReportOptionLabel = i18n.iValue("guardround.chart.label.Guard");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("marking")) {
            title = i18n.iValue("guardround.chart.label.SuccesfullyMarkingNumberByGuard");
        } else if (chartReportOption != null
            && chartReportOption.equals("nomarking")) {
            title = i18n.iValue("guardround.chart.label.NoMarkingNumberByGuard");
        }
        return title;
    }

}
