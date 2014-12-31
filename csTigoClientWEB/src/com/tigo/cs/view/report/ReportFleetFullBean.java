package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportFleetFullBean")
@ViewScoped
@ReportFile(fileName = "rep_fleet_full")
public class ReportFleetFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -1062926882239842043L;

    public ReportFleetFullBean() {
        this.chartReportOption = "driver";
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_fleet_full_classification";
        } else {
            reportName = "rep_fleet_full";
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
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,SV.SERVICEVALUE_COD DESC, SVD.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR, SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD DESC, SVD.RECORDDATE_DAT DESC ";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY u.NAME_CHR, SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public String generateNativeQueryForChart() {
        String sql = null;
        if (chartReportOption.equals("driver")) {
            sql = "SELECT "
                + "CASE WHEN (mcD.ENABLED_CHR = '1' AND mdD.value_chr IS NOT NULL) THEN mdD.VALUE_CHR ELSE '"
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
                + " AND sv.COD_SERVICE = 12 "
                + " %s "
                + " GROUP BY SV.COLUMN1_CHR ) Q "
                + " LEFT JOIN META_DATA mdD ON (mdD.cod_client = %s AND mdD.cod_meta = 7 AND mdD.cod_member = 1 AND mdD.CODE_CHR = Q.COLUMN1_CHR) "
                + " LEFT JOIN META_CLIENT mcD ON (mcD.cod_client = %s AND mcD.COD_META = 7) ";

            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
            sql = String.format(sql, getWhereReport(), codClient, codClient);
        } else if (chartReportOption.equals("vehicle")) {
            sql = "SELECT "
                + "CASE WHEN (mcV.ENABLED_CHR = '1' AND mdV.value_chr IS NOT NULL) THEN mdV.VALUE_CHR ELSE '"
                + i18n.iValue("web.client.label.NoDescription")
                + "' END, "
                + "Q.* "
                + "FROM ( "
                + "		SELECT SV.COLUMN2_CHR, COUNT(*) AS COUNT "
                + "        FROM SERVICE_VALUE_DETAIL svd "
                + "        INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "        INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "        INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + "        INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                + " WHERE 1 = 1 "
                + " AND sv.COD_SERVICE = 12 "
                + " %s "
                + " GROUP BY SV.COLUMN2_CHR ) Q "
                + " LEFT JOIN META_DATA mdV ON (mdV.cod_client = %s AND mdV.cod_meta = 8  AND mdV.cod_member = 1 AND mdV.CODE_CHR = Q.COLUMN2_CHR) "
                + " LEFT JOIN META_CLIENT mcV ON (mcV.cod_client = %s AND mcV.COD_META = 8) ";

            Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
            sql = String.format(sql, getWhereReport(), codClient, codClient);

        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("driver")) {
            chartReportOptionLabel = i18n.iValue("fleet.chart.option.Driver");
        } else if (chartReportOption != null
            && chartReportOption.equals("vehicle")) {
            chartReportOptionLabel = i18n.iValue("fleet.chart.option.Vehicle");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("driver")) {
            title = i18n.iValue("fleet.chart.label.RetirementNumberByDriver");
        } else if (chartReportOption != null
            && chartReportOption.equals("vehicle")) {
            title = i18n.iValue("fleet.chart.label.RetirementNumberByVehicle");
        }
        return title;
    }

}
