package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportGuardFullBean")
@ViewScoped
@ReportFile(fileName = "rep_guard_full")
public class ReportGuardFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -590867472448645563L;

    public ReportGuardFullBean() {
        this.chartReportOption = "marking";
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_guard_full_classification";
        } else {
            reportName = "rep_guard_full";
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
            sql = "SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + "FROM SERVICE_VALUE_DETAIL svd "
                + "INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + "INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                + "WHERE 1 = 1 "
                + "AND SV.COD_SERVICE = 6 AND SVD.COLUMN2_CHR IS NUll " + "%s "
                + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());
        } else if (chartReportOption.equals("nomarking")) {
            sql = "SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + "FROM SERVICE_VALUE_DETAIL svd "
                + "INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + "INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                + "WHERE 1 = 1 "
                + "AND SV.COD_SERVICE = 6 AND SVD.COLUMN2_CHR IS NOT NUll "
                + "%s " + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());

        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();

        if (chartReportOption != null) {
            chartReportOptionLabel = i18n.iValue("guard.chart.label.Userphone");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();

        if (chartReportOption != null && chartReportOption.equals("marking")) {
            title = i18n.iValue("guard.chart.label.SuccesfullyMarkingNumberByUserphone");
        } else if (chartReportOption != null
            && chartReportOption.equals("nomarking")) {
            title = i18n.iValue("guard.chart.label.NoMarkingNumberByUserphone");
        }
        return title;
    }
}
