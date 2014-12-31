package com.tigo.cs.view.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportAuditBean")
@ViewScoped
@ReportFile(fileName = "rep_audit_full")
public class ReportAuditBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -3180203461913304785L;
    @EJB
    private UserwebFacade userwebFacade;
    private List<Userweb> userwebList;
    private Userweb selectedUserweb;
    private List<Userweb> selectedUserwebList;

    public ReportAuditBean() {
    }

    @Override
    public String getReportName() {
        String reportName = "";

        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0
            && !parametersValidatedChart) {
            reportName = "rep_audit_full_classification";
        } else {
            reportName = "rep_audit_full";
        }

        return reportName;
    }

    @Override
    public String getWhereReport() {
        DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");

        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String sqlWhere = "";
        String valor = "";
        sqlWhere = String.format(sqlWhere.concat(" AND UW.COD_CLIENT = %s"),
                clientCod.toString());
        if (dateFrom != null && dateTo != null) {
            sqlWhere = String.format(
                    sqlWhere.concat(" AND a.auditdate_dat BETWEEN TO_DATE('%s', 'yyyy-mm-dd')"
                        + " AND TO_DATE('%s', 'yyyy-mm-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }

        if (selectedUserwebList != null && selectedUserwebList.size() > 0) {
            for (Userweb u : selectedUserwebList) {
                valor += u.getUserwebCod().toString() + ",";
            }
            valor = valor.substring(0, valor.length() - 1);
            sqlWhere = sqlWhere.concat(" AND a.cod_userweb IN (" + valor + ")");

        }

        if (validatedAllClassification && selectedClassificationList == null) {
            try {
                selectedClassificationList = new ArrayList<Classification>();
                selectedClassificationList = classificationFacade.findListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }
        }

        if (parametersValidatedChart) {
            if (selectedClassificationList != null
                && selectedClassificationList.size() > 0
                || validatedAllClassification) {
                for (Classification c : selectedClassificationList) {
                    Classification managedClassification = classificationFacade.find(
                            c.getClassificationCod(), "userphoneList");

                    for (Userphone u : managedClassification.getUserphoneList()) {
                        valor += u.getUserphoneCod().toString() + ",";
                    }
                    valor = valor.substring(0, valor.length() - 1);
                    sqlWhere = sqlWhere.concat(" AND a.cod_userweb IN ("
                        + valor + ")");
                }

            }
        }

        if (!parametersValidatedChart) {
            if (selectedClassificationList != null
                && selectedClassificationList.size() > 0) {
                for (Classification c : selectedClassificationList) {
                    valor += c.getClassificationCod().toString() + ",";
                }
                valor = valor.substring(0, valor.length() - 1);
                sqlWhere = sqlWhere.concat(" AND CL.CLASSIFICATION_COD IN ("
                    + valor + ")");
            }
        }
        return sqlWhere;

    }

    @Override
    public String getParameterDescriptionReport() {

        String parametersDesc = "";
        if (dateFrom != null && dateTo != null) {
            DateFormat descSdf = new SimpleDateFormat("dd/MM/yyyy");
            parametersDesc = String.format(
                    i18n.iValue("web.client.backingBean.report.message.ParameterDescDate"),
                    descSdf.format(dateFrom),
                    i18n.iValue("web.client.backingBean.report.message.ParameterDescDateTo"),
                    descSdf.format(dateTo));
        }
        if (selectedUserweb != null) {
            parametersDesc = String.format(
                    parametersDesc.concat(i18n.iValue("web.client.backingBean.report.message.WebUserReport")),
                    selectedUserweb.getNameChr());
        }

        return parametersDesc;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";

        if (getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0
            && !parametersValidatedChart) {
            sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,a.auditdate_dat desc";
        } else {
            sqlOrderBy = " ORDER BY a.auditdate_dat desc ";
        }

        return sqlOrderBy;
    }

    public Userweb getSelectedUserweb() {
        return selectedUserweb;
    }

    public void setSelectedUserweb(Userweb selectedUserweb) {
        this.selectedUserweb = selectedUserweb;
    }

    public List<Userweb> getUserwebList() {
        if (userwebList == null) {
            userwebList = userwebFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());// findAllEnabledByClientCod(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
        }
        return userwebList;
    }

    public void setUserwebList(List<Userweb> userwebList) {
        this.userwebList = userwebList;
    }

    public List<Userweb> getSelectedUserwebList() {
        return selectedUserwebList;
    }

    public void setSelectedUserwebList(List<Userweb> selectedUserwebList) {
        this.selectedUserwebList = selectedUserwebList;
    }

    @Override
    public String validateParameters() {

        if (((selectedUserwebList == null || selectedUserwebList.size() == 0) && !validatedAllUsers)
            && ((selectedClassificationList == null || selectedClassificationList.size() == 0) && !validatedAllClassification)) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidUserphoneClassification"));
            return null;
        }

        parametersValidated = true;

        return null;
    }

    public void selectAllUserwebs() {
        if (validatedAllUsers)
            selectedUserwebList = null;
        selectedClassificationList = null;
        validatedAllClassification = false;

    }

    /*
     * Retorna la lista de clasificación o clasificaciones asociadas al usuario
     * logueado
     */
    @Override
    public List<Classification> getClassificationList() {
        if (classificationList == null) {
            classificationList = classificationFacade.getEagerClassificationListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
        }
        return classificationList;
    }

    // metodo invocado por el listener del checkbox Todos de la seccion de
    // clasificacion
    @Override
    public void selectAllClassification() {
        if (validatedAllClassification)
            selectedClassificationList = null;
        selectedUserwebList = null;
        validatedAllUsers = false;
    }

    // Variable verificada en el disabled del componente que muestra la lista de
    // clasificaciones
    @Override
    public Boolean getCheck1() {
        check1 = false;
        if (validatedAllUsers || validatedAllClassification
            || selectedUserwebList != null) {
            check1 = true;
            return check1;
        }
        return check1;
    }

    // Variable verificada en el disabled del componente que muestra la lista de
    // userweb
    @Override
    public Boolean getCheck2() {
        check2 = false;
        if (validatedAllUsers || validatedAllClassification
            || selectedClassificationList != null) {
            check2 = true;
            return check2;
        }
        return check2;
    }

    // Variable verificada en el disabled del componente checkbox Todos de la
    // parte de clasificacion
    @Override
    public Boolean getCheckUsers() {
        checkUsers = false;
        if (validatedAllUsers || selectedUserwebList != null) {
            checkUsers = true;
            return checkUsers;
        }
        return checkUsers;
    }

}
