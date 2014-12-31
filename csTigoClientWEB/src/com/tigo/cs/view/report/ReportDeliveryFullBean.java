package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportDeliveryFullBean")
@ViewScoped
@ReportFile(fileName = "rep_delivery_full")
public class ReportDeliveryFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 3973747585784684009L;

    public ReportDeliveryFullBean() {
        this.chartReportOption = "userphone";
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_delivery_full_classification";
        } else {
            reportName = "rep_delivery_full";
        }
        return reportName;
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
                + "WHERE 1 = 1 " + "AND sv.COD_SERVICE = 7 " + "%s "
                + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
            sql = String.format(sql, getWhereReport());
        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            chartReportOptionLabel = i18n.iValue("delivery.chart.option.Userphone");
        }
        return chartReportOptionLabel;
    }

    @Override
    public String getTitle() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            title = i18n.iValue("delivery.chart.label.DeliveryNumberByUserphone");
        }
        return title;
    }
}
