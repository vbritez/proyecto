package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportInventoryFullBean")
@ViewScoped
@ReportFile(fileName = "rep_inventorystd_full")
public class ReportInventoryFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 6178736963824091535L;

    public ReportInventoryFullBean() {
        this.chartReportOption = "userphone";
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_inventorystd_full_classification";
        } else {
            reportName = "rep_inventorystd_full";
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
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,sv.SERVICEVALUE_COD DESC, sv.RECORDDATE_DAT DESC, svd.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,sv.SERVICEVALUE_COD, sv.RECORDDATE_DAT, svd.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR, sv.SERVICEVALUE_COD, sv.RECORDDATE_DAT, svd.RECORDDATE_DAT";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY sv.SERVICEVALUE_COD DESC, sv.RECORDDATE_DAT DESC, svd.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY sv.SERVICEVALUE_COD, sv.RECORDDATE_DAT, svd.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY u.NAME_CHR, sv.SERVICEVALUE_COD, sv.RECORDDATE_DAT, svd.RECORDDATE_DAT";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public String generateNativeQueryForChart() {
        String sql = null;
        if (chartReportOption.equals("userphone")) {
            sql = "SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                + "FROM SERVICE_VALUE_DETAIL svd "
                + "INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                + "INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                + "INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                + "WHERE 1 = 1 " + "AND sv.COD_SERVICE = 10 " + "%s "
                + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());
        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            chartReportOptionLabel = i18n.iValue("inventoryStd.chart.option.Userphone");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            title = i18n.iValue("inventoryStd.chart.label.InventoryNumberByUserphone");
        }
        return title;
    }

}
