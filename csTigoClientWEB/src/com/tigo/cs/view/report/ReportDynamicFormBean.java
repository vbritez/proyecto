package com.tigo.cs.view.report;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.domain.Feature;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportDynamicFormBean")
@ViewScoped
@ReportFile(fileName = "rep_dynamicform")
public class ReportDynamicFormBean extends ReportFeatureBean {

    @EJB
    private GlobalParameterFacade globalParameterFacade;

    private static final long serialVersionUID = -3469996846328093617L;

    @Override
    public String getReportName() {
        String reportName = "";
        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0) {
            reportName = "rep_dynamicform_classification";
        } else {
            reportName = "rep_dynamicform";
        }
        return reportName;
    }

    @Override
    public Feature getSelectedFeature() {
        if (super.getSelectedFeature() == null) {

            String featureDynamicFormId = null;
            try {
                featureDynamicFormId = globalParameterFacade.findByCode("feature.dynamicform.code");
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }

            super.setSelectedFeature(featureFacade.find(Long.parseLong(featureDynamicFormId)));
            featureTypeAvailable();
        }
        return super.getSelectedFeature();
    }

    @Override
    public List<FeatureElement> getFeatureElementList() {

        if (super.getFeatureElementList() == null) {
            super.setFeatureElementList(new ArrayList<FeatureElement>());
        }
        super.setFeatureElementList(featureElementFacade.getFeatureElements(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                getSelectedFeature().getFeatureCode()));

        return super.getFeatureElementList();
    }

}
