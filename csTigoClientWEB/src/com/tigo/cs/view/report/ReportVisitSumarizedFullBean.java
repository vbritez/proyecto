package com.tigo.cs.view.report;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.tigo.cs.commons.report.ReportFile;

@ManagedBean(name = "reportVisitSumarizedFullBean")
@ViewScoped
@ReportFile(fileName = "rep_visit_sumarized")
public class ReportVisitSumarizedFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -590867472448645563L;

    public ReportVisitSumarizedFullBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_visit_sumarized_classification";
        } else {
            reportName = "rep_visit_sumarized";
        }

        return reportName;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,u.NAME_CHR";
            }
        } else {
            if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY u.NAME_CHR ";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public List<SelectItem> getOrderByList() {
        if (orderByList == null) {
            orderByList = new ArrayList<SelectItem>();
            orderByList.add(new SelectItem("US", i18n.iValue("web.client.backingBean.report.message.User")));
        }
        return orderByList;
    }

}