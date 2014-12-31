package com.tigo.cs.view.report;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportRankingProductBean")
@ViewScoped
public class ReportRankingProduct extends AbstractFullReportBean {

    private static final long serialVersionUID = 8964197885783590522L;

    @EJB
    private Notifier notifier;
    private List<Integer> rowQuantList;
    private Integer rowQuantSelected;

    public List<Integer> getRowQuantList() {
        rowQuantList = new ArrayList<Integer>();
        rowQuantList.add(new Integer("15"));
        rowQuantList.add(new Integer("25"));
        rowQuantList.add(new Integer("50"));
        rowQuantList.add(new Integer("100"));
        return rowQuantList;
    }

    @Override
    public String validateParameters() {

        if (rowQuantSelected == null) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.report.rankingproduct.limitSelected"));
            return null;
        }
        return super.validateParameters();
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_ranking_product_classification";
        } else {
            reportName = "rep_ranking_product";
        }

        return reportName;
    }

    @Override
    public String generateReport() {
        if (!parametersValidated) {
            return validateParameters();
        }
        try {
            signalReport(ReportType.PDF);
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("WHERE", getWhereReport());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
            params.put("LIMIT", getRowQuantSelected());
            params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
            params.put(JRParameter.REPORT_RESOURCE_BUNDLE,
                    i18n.getResourceBundle());

            ClientFile cf = fileFacade.getClientLogo(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            try {
                if (cf != null) {
                    params.put("CLIENT_LOGO",
                            JRImageLoader.loadImage(cf.getFileByt()));
                }
            } catch (JRException ex) {
            }

            String reportName = getReportName();

            if (reportName != null) {
                if (reportType.equals("xls")) {
                    reportName += "_xls";
                }
            }
            Connection conn = SMBaseBean.getDatabaseConnecction();
            if (reportType.equals("pdf")) {
                JasperReportUtils.respondReport(reportName, params, true, conn,
                        ReportType.PDF);
            } else if (reportType.equals("xls")) {
                JasperReportUtils.respondReport(reportName, params, true, conn,
                        ReportType.XLS);
            }

            parametersValidated = false;
        } catch (Exception e) {
            notifier.error(ReportVisitFullBean.class, null, e.getMessage(), e);
        }
        return null;
    }

    public Integer getRowQuantSelected() {
        return rowQuantSelected;
    }

    public void setRowQuantSelected(Integer rowQuantSelected) {
        this.rowQuantSelected = rowQuantSelected;
    }
}
