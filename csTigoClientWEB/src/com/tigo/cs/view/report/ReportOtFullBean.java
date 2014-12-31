package com.tigo.cs.view.report;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.view.DataStatus;
import com.tigo.cs.facade.view.DataStatusFacade;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportOtFullBean")
@ViewScoped
@ReportFile(fileName = "rep_ot_full")
public class ReportOtFullBean extends AbstractFullReportBean {

    @EJB
    private DataStatusFacade dataStatusFacade;

    private static final long serialVersionUID = -8557671685008057653L;

    private DataStatus selectedDataStatus;
    private List<DataStatus> statusList;
    private Classification selectedClassification;

    public ReportOtFullBean() {
    }

    @Override
    public String validateParameters() {
        if (dateFrom == null || dateTo == null) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.DateRange"));
            return null;
        }
        if (dateFrom.after(dateTo)) {
            parametersValidated = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidDateRange"));
            return null;
        }

        parametersValidated = true;

        return null;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if (selectedOrderby.equals("FD")) {
            sqlOrderBy = " ORDER BY o.CREATED_DATE DESC ";
        } else if (selectedOrderby.equals("FA")) {
            sqlOrderBy = " ORDER BY o.CREATED_DATE ";
        } else if (selectedOrderby.equals("US")) {
            sqlOrderBy = " ORDER BY U.NAME_CHR, o.CREATED_DATE ";
        }
        return sqlOrderBy;
    }

    @Override
    public String getWhereReport() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        sqlWhere = String.format(sqlWhere.concat(" AND o.COD_CLIENT = %s"),
                clientCod.toString());
        if (getSelectedDataStatus() != null
            && getSelectedDataStatus().getCode().equals("0")) {
            sqlWhere += " AND o.STATUS_COD = '0' ";

        } else {
            sqlWhere += MessageFormat.format(
                    " AND ( (EXISTS (select * from USERPHONE_CLASSIFICATION uc where uc.cod_userphone = u.userphone_cod and uc.cod_classification in ({0})) "
                        + " AND o.STATUS_COD <> '0') "
                        + " OR (o.STATUS_COD = '0') )", classifications);
            if (getSelectedDataStatus() != null)
                sqlWhere += " AND o.STATUS_COD = '"
                    + getSelectedDataStatus().getCode() + "'";

        }

        if (dateFrom != null && dateTo != null) {

            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlWhere = String.format(
                    sqlWhere.concat(" AND o.CREATED_DATE >= TO_DATE('%s', 'yyyy-mm-dd') "
                        + "AND o.CREATED_DATE <= TO_DATE('%s', 'yyyy-mm-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }
        if (selectedUserphone != null) {
            sqlWhere = String.format(
                    sqlWhere.concat(" AND u.USERPHONE_COD = %s"),
                    selectedUserphone.getUserphoneCod().toString());
        }
        return sqlWhere;
    }

    @Override
    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = userphoneFacade.findByUserwebAndClassificationAndService(
                    SecurityBean.getInstance().getLoggedInUserClient(), 16L);
        }
        return userphoneList;
    }

    public DataStatus getSelectedDataStatus() {
        return selectedDataStatus;
    }

    public void setSelectedDataStatus(DataStatus selectedDataStatus) {
        this.selectedDataStatus = selectedDataStatus;
    }

    public List<DataStatus> getStatusList() {
        if (statusList == null) {
            statusList = dataStatusFacade.findByClientMeta(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    12L);
        }
        return statusList;
    }

    public void setStatusList(List<DataStatus> statusList) {
        this.statusList = statusList;
    }

    @Override
    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public Classification getSelectedClassification() {
        return selectedClassification;
    }

    public void setSelectedClassification(Classification selectedClassification) {
        this.selectedClassification = selectedClassification;
    }

    public String getUserphoneListByClassification(Classification classif) {
        List<Userphone> userphoneList;
        try {
            userphoneList = userphoneFacade.findByClientAndClassification(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    selectedClassification.getClassificationCod());
        } catch (GenericFacadeException e) {
            userphoneList = new ArrayList<Userphone>();
            e.printStackTrace();
        }
        String list = "";
        for (Userphone up : userphoneList) {
            list += up.getUserphoneCod().toString() + ",";
        }
        if (list.length() > 0)
            list = list.substring(0, list.length() - 1);
        return list;
    }

    @Override
    public List<SelectItem> getOrderByList() {
        if (orderByList == null) {
            orderByList = new ArrayList<SelectItem>();
            orderByList.add(new SelectItem("FD", i18n.iValue("web.client.backingBean.report.message.DescendantDate")));
            orderByList.add(new SelectItem("FA", i18n.iValue("web.client.backingBean.report.message.AscendantDate")));
            orderByList.add(new SelectItem("US", i18n.iValue("ot.report.message.WorkingGroup")));
        }
        return orderByList;
    }

}
