package com.tigo.cs.view.report;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Feature;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.FeatureElementFacade;
import com.tigo.cs.facade.FeatureFacade;
import com.tigo.cs.facade.PhoneListFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportFeatureBean")
@ViewScoped
@ReportFile(fileName = "rep_feature")
public class ReportFeatureBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 4916948993635665380L;
    private List<Feature> featureList;
    private Feature selectedFeature;
    private Boolean abierta = true;
    private Boolean externa = false;
    private Boolean interna = false;
    private List<Phone> phonesList;
    private Boolean validatedAllPhoneList;
    private List<Phone> selectedPhoneList;
    private List<FeatureElement> featureElementList;
    private FeatureElement selectedFeatureElement;
    @EJB
    protected FeatureFacade featureFacade;
    @EJB
    protected FeatureElementFacade featureElementFacade;
    @EJB
    private PhoneListFacade phoneListFacade;

    @Override
    public String getReportName() {
        String reportName = "";
        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0) {
            reportName = "rep_feature_classification";
        } else {
            reportName = "rep_feature";
        }
        return reportName;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";

        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0) {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY cl.CLASSIFICATION_COD ASC,m.DATEIN_DAT DESC,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY cl.CLASSIFICATION_COD,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD, m.DATEIN_DAT";
            } else if (selectedOrderby.equals("US")) {
                if (getSelectedUserphone() != null || validatedAllPhoneList) {
                    sqlOrderBy = " ORDER BY cl.CLASSIFICATION_COD,u.NAME_CHR,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
                } else {
                    sqlOrderBy = " ORDER BY cl.CLASSIFICATION_COD,p.NAME_CHR,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
                }
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = "ORDER BY m.DATEIN_DAT DESC,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD ";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = "ORDER BY fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
            } else if (selectedOrderby.equals("US")) {
                if (getSelectedUserphone() != null || validatedAllPhoneList) {
                    sqlOrderBy = "ORDER BY u.NAME_CHR,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
                } else {
                    sqlOrderBy = "ORDER BY p.NAME_CHR,fvd.COD_FEATURE_VALUE,fee.FEATURE_ELEMENT_ENTRY_COD,m.DATEIN_DAT";
                }
            }
        }

        return sqlOrderBy;
    }

    /*
     * Traemos los tipos de prestaciones que tiene el feature seleccionado,
     * pudiendo ser Abierta, Interna o Externa
     */
    public void featureTypeAvailable() {
        if (featureElementList == null) {
            featureElementList = new ArrayList<FeatureElement>();
        }
        featureElementList = featureElementFacade.getFeatureElements(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                getSelectedFeature().getFeatureCode());
    }

    /*
     * Dependiendo de la prestacion que selecciono el usuario, habilitamos la
     * lista de phones, userphones o clasificacion
     */
    public void availableOptions() {
        FeatureElement featureElement = null;
        featureElement = featureElementFacade.getIsAbierta(selectedFeatureElement.getFeatureElementCod());

        if (featureElement != null) {
            validatedAllPhoneList = false;
            validatedAllClassification = false;
            validatedAllUsers = false;
            selectedUserphone = null;
            selectedPhoneList = null;
            selectedClassificationList = null;
            abierta = true;
            externa = false;
            interna = false;
            return;
        }

        List<Userphone> listUserphones = featureElementFacade.getIsInterna(selectedFeatureElement.getFeatureElementCod());

        if (listUserphones != null && listUserphones.size() > 0) {
            validatedAllPhoneList = false;
            validatedAllClassification = false;
            validatedAllUsers = false;
            selectedUserphone = null;
            selectedPhoneList = null;
            selectedClassificationList = null;
            interna = true;
            abierta = false;
            externa = false;
            return;

        }

        List<PhoneList> listPhoneList = featureElementFacade.getIsExterna(selectedFeatureElement.getFeatureElementCod());
        if (listPhoneList != null && listPhoneList.size() > 0) {
            validatedAllPhoneList = false;
            validatedAllClassification = false;
            validatedAllUsers = false;
            selectedUserphone = null;
            selectedPhoneList = null;
            selectedClassificationList = null;
            externa = true;
            abierta = false;
            interna = false;
            return;
        }
    }

    public void selectAllPhoneList() {
        if (validatedAllPhoneList) {
            selectedPhoneList = null;
        }
    }

    @Override
    public String validateParameters() {
        if (dateFrom == null || dateTo == null) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.DateRange"));
            abierta = false;
            externa = false;
            interna = false;
            return null;
        }
        if (dateFrom.after(dateTo)) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidDateRange"));
            abierta = false;
            externa = false;
            interna = false;
            return null;
        }

        if (externa) {
            if ((!validatedAllPhoneList)
                && (selectedPhoneList.size() == 0 || selectedPhoneList == null)) {
                parametersValidated = false;
                setWarnMessage(i18n.iValue("web.client.backingBean.report.feature.NotSelectedPhone"));
                abierta = false;
                externa = true;
                interna = false;
                return null;
            }
        }

        if (interna) {
            if (((selectedUserphoneList == null || selectedUserphoneList.size() == 0) && !validatedAllUsers)
                && ((selectedClassificationList == null || selectedClassificationList.size() == 0) && !validatedAllClassification)) {
                parametersValidated = false;
                setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidUserphoneClassification"));
                abierta = false;
                externa = false;
                interna = true;
                return null;
            }
        }

        if (selectedFeature == null) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.feature.NotSelectedFeature"));
            abierta = false;
            externa = false;
            interna = false;
            return null;
        }

        if (selectedFeatureElement == null) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.feature.NotSelectedFeatureElement"));
            abierta = false;
            externa = false;
            interna = false;
            return null;
        }

        parametersValidated = true;

        return null;
    }

    @Override
    public String getWhereReport() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        String valor = "";
        String valor2 = "";
        sqlWhere = String.format(sqlWhere.concat(" AND fe.COD_CLIENT = %s"),
                clientCod.toString());
        sqlWhere = String.format(
                sqlWhere.concat(" AND fe.FEATURE_ELEMENT_COD = %s"),
                selectedFeatureElement.getFeatureElementCod().toString());

        if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlWhere = String.format(
                    sqlWhere.concat(" AND trunc(m.DATEIN_DAT) >= TO_DATE('%s', 'yyyy-mm-dd') "
                        + "AND trunc(m.DATEIN_DAT) <= TO_DATE('%s', 'yyyy-mm-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }

        if (!abierta) {

            if (validatedAllUsers) {
                sqlWhere += MessageFormat.format(" and EXISTS "
                    + "(select * from USERPHONE_CLASSIFICATION uc "
                    + "where uc.cod_userphone = u.userphone_cod "
                    + "and uc.cod_classification in ({0})) ", classifications);
            }

            if (selectedUserphoneList != null
                && selectedUserphoneList.size() > 0) {
                for (Userphone u : selectedUserphoneList) {
                    valor += u.getUserphoneCod().toString() + ",";
                }
                valor = valor.substring(0, valor.length() - 1);
                sqlWhere = sqlWhere.concat(" AND U.USERPHONE_COD IN (" + valor
                    + ")");

            }

            if (validatedAllClassification
                && selectedClassificationList == null) {
                try {
                    selectedClassificationList = new ArrayList<Classification>();
                    selectedClassificationList = classificationFacade.findListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
                } catch (GenericFacadeException e) {
                    e.printStackTrace();
                }
            }

            if (selectedClassificationList != null
                && selectedClassificationList.size() > 0) {
                for (Classification c : selectedClassificationList) {
                    valor += c.getClassificationCod().toString() + ",";
                }
                valor = valor.substring(0, valor.length() - 1);
                sqlWhere = sqlWhere.concat(" AND CL.CLASSIFICATION_COD IN ("
                    + valor + ")");
            }

            if (selectedPhoneList != null && selectedPhoneList.size() > 0
                || validatedAllPhoneList) {
                if (validatedAllPhoneList) {
                    selectedPhoneList = phoneListFacade.getClientPhone(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
                }
                for (Phone p : selectedPhoneList) {
                    valor2 += p.getPhoneCod().toString() + ",";
                }
                valor2 = valor2.substring(0, valor2.length() - 1);
                sqlWhere += MessageFormat.format(" and EXISTS "
                    + "(select * from PHONE ph "
                    + "where ph.phone_cod = fv.cod_phone "
                    + "and fv.cod_phone in ({0})) ", valor2);
            }
        }

        return sqlWhere;
    }

    @Override
    public String generateReport() {
        if (!parametersValidated) {
            return validateParameters();
        }
        try {
            signalReport(ReportType.PDF);
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("REPORT_NAME", MessageFormat.format(
                    i18n.iValue("web.client.report.FeatureName"),
                    getSelectedFeature().getDescriptionChr()));
            params.put("WHERE", getWhereReport());
            params.put("ORDER_BY", getOrderByReport());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
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
            e.printStackTrace();
        }

        return null;
    }

    // GETTERS AND SETTERS

    public List<Feature> getFeatureList() {
        /* Si es nulo traemos todos los features del cliente */
        if (featureList == null) {
            this.featureList = featureFacade.getFeatureByClientByShowOnMenu(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    true);
        }
        return featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
    }

    public List<Phone> getPhonesList() {
        List<PhoneList> list = featureElementFacade.getPhoneList(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                selectedFeatureElement.getFeatureElementCod());
        this.phonesList = new ArrayList<Phone>();
        for (PhoneList pl : list) {
            List<Phone> listPhone = null;
            listPhone = phoneListFacade.getClientPhone(
                    pl.getPhoneListCod(),
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());

            for (Phone p : listPhone) {
                phonesList.add(p);
            }
        }
        return phonesList;
    }

    public void setPhonesList(List<Phone> phonesList) {
        this.phonesList = phonesList;
    }

    public Feature getSelectedFeature() {
        return selectedFeature;
    }

    public void setSelectedFeature(Feature selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

    public List<FeatureElement> getFeatureElementList() {
        return featureElementList;
    }

    public void setFeatureElementList(List<FeatureElement> featureElementList) {
        this.featureElementList = featureElementList;
    }

    public FeatureElement getSelectedFeatureElement() {
        return selectedFeatureElement;
    }

    public void setSelectedFeatureElement(FeatureElement selectedFeatureElement) {
        this.selectedFeatureElement = selectedFeatureElement;
    }

    public Boolean getAbierta() {
        return abierta;
    }

    public void setAbierta(Boolean abierta) {
        this.abierta = abierta;
    }

    public Boolean getExterna() {
        return externa;
    }

    public void setExterna(Boolean externa) {
        this.externa = externa;
    }

    public Boolean getInterna() {
        return interna;
    }

    public void setInterna(Boolean interna) {
        this.interna = interna;
    }

    public Boolean getValidatedAllPhoneList() {
        return validatedAllPhoneList;
    }

    public void setValidatedAllPhoneList(Boolean validatedAllPhoneList) {
        this.validatedAllPhoneList = validatedAllPhoneList;
    }

    public List<Phone> getSelectedPhoneList() {
        return selectedPhoneList;
    }

    public void setSelectedPhoneList(List<Phone> selectedPhoneList) {
        this.selectedPhoneList = selectedPhoneList;
    }

    @Override
    public Boolean getCheck1() {
        check1 = false;
        if (validatedAllUsers
            || validatedAllClassification
            || (selectedUserphoneList != null && selectedUserphoneList.size() > 0)) {
            check1 = true;
            return check1;
        }
        return check1;
    }

    @Override
    public Boolean getCheck2() {
        check2 = false;
        if (validatedAllUsers
            || validatedAllClassification
            || (selectedClassificationList != null && selectedClassificationList.size() > 0)) {
            check2 = true;
            return check2;
        }
        return check2;
    }

    @Override
    public Boolean getCheckClassification() {
        checkClassification = false;
        if (validatedAllClassification
            || (selectedClassificationList != null && selectedClassificationList.size() > 0)) {
            checkClassification = true;
            return checkClassification;
        }

        return checkClassification;
    }

    @Override
    public Boolean getCheckUsers() {
        checkUsers = false;
        if (validatedAllUsers
            || (selectedUserphoneList != null && selectedUserphoneList.size() > 0)) {
            checkUsers = true;
            return checkUsers;
        }
        return checkUsers;
    }

    @Override
    public List<Userphone> getUserphoneList() {
        userphoneList = featureElementFacade.getUserphones(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                selectedFeatureElement.getFeatureElementCod());
        return userphoneList;
    }

    @Override
    public List<Classification> getClassificationList() {
        classificationList = featureElementFacade.getClassificationList(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                selectedFeatureElement.getFeatureElementCod());
        return classificationList;
    }

}
