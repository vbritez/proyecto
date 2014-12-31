package com.tigo.cs.view.report;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.report.ReportFile;

@ManagedBean(name = "reportInventoryDAFullBean")
@ViewScoped
@ReportFile(fileName = "rep_inventory_full")
public class ReportInventoryDAFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 7144045702154832427L;

    public ReportInventoryDAFullBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_inventory_full_classification";
        } else {
            reportName = "rep_inventory_full";
        }
        return reportName;
    }

}
