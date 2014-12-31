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
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;

@ManagedBean(name = "reportAttendanceFullBean")
@ViewScoped
public class ReportAttendanceFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = 3623089837036345697L;

    @EJB
    private MetaDataFacade metadataFacade;
    @EJB
    private Notifier notifier;
    private List<MetaData> employeeMetadataList;
    private MetaData selectedEmployeeMetadata;
    private String reportOption = "grouped";
    private final DateFormat sqlSdf = new SimpleDateFormat("dd/MM/yyyy");
    private boolean validatedAllEmployee;
    private List<SelectItem> orderByListNoAttendance;
    private List<MetaData> selectedEmployeeMetadataList;

    public ReportAttendanceFullBean() {
    }

    @Override
    public String getWhereReport() {
        String where = super.getWhereReport();
        if (selectedEmployeeMetadata != null) {
            where = String.format(where.concat(" AND mdE.CODE_CHR ='%s'"),
                    selectedEmployeeMetadata.getMetaDataPK().getCodeChr());
        }
        return where;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD ASC,q.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD,q.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD,q.NAME_CHR, q.RECORDDATE_DAT";
            }
        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY q.RECORDDATE_DAT DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY q.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY q.NAME_CHR, q.RECORDDATE_DAT";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public String getParameterDescriptionReport() {
        String parameterDesc = super.getParameterDescriptionReport();
        if (selectedEmployeeMetadata != null) {
            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescEmployee")),
                    selectedEmployeeMetadata.getValueChr().toString());
        }
        return parameterDesc;
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if (reportOption.equals("grid")) {
            if ((getSelectedClassificationList() != null
                && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
                || validatedAllClassification) {
                return reportName = "rep_attendance_full_classification_grid";
            } else {
                return reportName = "rep_attendance_full_grid";
            }

        }

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_attendance_full_classification";
        } else {
            reportName = "rep_attendance_full";
        }

        return reportName;
    }

    @Override
    public String generateReport() {
        return super.generateReport();
    }

    @Override
    public String validateParameters() {
        return super.validateParameters();
    }

    // SECTION OF NO ATTENDANCE REPORT
    public String getWhereReportNoAttendance() {
        String where = "";
        String valor = "";
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        try {
            if (validatedAllUsers) {
                where += MessageFormat.format(" and EXISTS "
                    + "(select * from USERPHONE_CLASSIFICATION uc "
                    + "where uc.cod_userphone = u.userphone_cod "
                    + "and uc.cod_classification in ({0})) ", classifications);
            }
            if (selectedEmployeeMetadataList != null
                && selectedEmployeeMetadataList.size() > 0) {
                String aux = "";
                for (MetaData md : selectedEmployeeMetadataList) {
                    aux += md.getMetaDataPK().getCodeChr() + ",";
                }

                if (aux.length() > 0) {
                    aux = aux.substring(0, aux.length() - 1);
                    where = String.format(
                            where.concat(" AND mde.CODE_CHR in (%s) "), aux);
                }

            }

            if (selectedUserphoneList != null
                && selectedUserphoneList.size() > 0 && !validatedAllUsers) {
                for (Userphone u : selectedUserphoneList) {
                    valor += u.getUserphoneCod().toString() + ",";
                }
                if (valor.length() > 0) {
                    valor = valor.substring(0, valor.length() - 1);
                    where = String.format(
                            where.concat(" AND U.USERPHONE_COD IN (%s) "),
                            valor);
                }
            }
            if (validatedAllClassification
                && selectedClassificationList == null) {
                try {
                    selectedClassificationList = new ArrayList<Classification>();
                    selectedClassificationList = classificationFacade.findListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
                } catch (GenericFacadeException e) {
                    notifier.error(ReportVisitFullBean.class, null,
                            e.getMessage(), e);
                }
            }

            if (parametersValidatedChart) {
                if (selectedClassificationList != null
                    && selectedClassificationList.size() > 0) {
                    String classificationsCod = "";
                    for (Classification c : selectedClassificationList) {
                        classificationsCod += c.getClassificationCod().toString()
                            + ",";
                    }
                    if (classificationsCod.length() > 0) {
                        classificationsCod = classificationsCod.substring(0,
                                classificationsCod.length() - 1);
                        where += MessageFormat.format(" and EXISTS "
                            + "(select * from USERPHONE_CLASSIFICATION uc "
                            + "where uc.cod_userphone = u.userphone_cod "
                            + "and uc.cod_classification in ({0})) ",
                                classificationsCod);
                    }
                }
            }

            if (!parametersValidatedChart) {
                if (selectedClassificationList != null
                    && selectedClassificationList.size() > 0) {
                    for (Classification c : selectedClassificationList) {
                        valor += c.getClassificationCod().toString() + ",";
                    }
                    if (valor.length() > 0) {
                        valor = valor.substring(0, valor.length() - 1);
                        where = String.format(
                                where.concat(" AND CL.CLASSIFICATION_COD IN (%s) "),
                                valor);
                    }
                }
            }
            if (validatedAllClassification) {
                selectedClassificationList = null;
            }
        } catch (Exception e) {
            notifier.error(ReportVisitFullBean.class, null, e.getMessage(), e);
        }
        return where;
    }

    public String getOrderNoAttendance() {
        String sqlOrderBy = "";
        if (selectedOrderby.equals("FD")) {
            sqlOrderBy = " ORDER BY FECHA DESC";
        } else {
            sqlOrderBy = " ORDER BY FECHA";
        }

        return sqlOrderBy;
    }

    public String getReportNameNoAttendance() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            return reportName = "rep_no_attendance_classification";
        } else {
            return reportName = "rep_no_attendance";
        }

    }

    public String validateParametersNoAttendence() {
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

    public String generateReportNoAttendance() {
        if (!parametersValidated) {
            return validateParametersNoAttendence();
        }
        try {
            signalReport(ReportType.PDF);
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("WHERE", getWhereReportNoAttendance());
            params.put("ORDER_BY", getOrderNoAttendance());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
            params.put("FECHA_DESDE", sqlSdf.format(dateFrom));
            params.put("FECHA_HASTA", sqlSdf.format(dateTo));
            params.put(
                    "CLIENT_COD",
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
            params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
            params.put(JRParameter.REPORT_RESOURCE_BUNDLE,
                    i18n.getResourceBundle());
            params.put("SUBREPORT_DIR", getReportsPath().concat("/"));

            ClientFile cf = fileFacade.getClientLogo(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            try {
                if (cf != null) {
                    params.put("CLIENT_LOGO",
                            JRImageLoader.loadImage(cf.getFileByt()));
                }
            } catch (JRException ex) {
            }

            String reportName = getReportNameNoAttendance();

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

    public List<MetaData> getEmployeeMetadataList() {
        if (employeeMetadataList == null) {
            employeeMetadataList = metadataFacade.findByClientMetaMember(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    7L, 1L);
        }
        return employeeMetadataList;
    }

    public void setEmployeeMetadataList(List<MetaData> employeeMetadataList) {
        this.employeeMetadataList = employeeMetadataList;
    }

    public MetaData getSelectedEmployeeMetadata() {
        return selectedEmployeeMetadata;
    }

    public void setSelectedEmployeeMetadata(MetaData selectedEmployeeMetadata) {
        this.selectedEmployeeMetadata = selectedEmployeeMetadata;
    }

    public String getReportOption() {
        return reportOption;
    }

    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    public void selectAllEmployee() {
        selectedEmployeeMetadataList = null;
    }

    public List<SelectItem> getOrderByListNoAttendance() {
        if (orderByListNoAttendance == null) {
            orderByListNoAttendance = new ArrayList<SelectItem>();
            orderByListNoAttendance.add(new SelectItem("FD", i18n.iValue("web.client.backingBean.report.message.DescendantDate")));
            orderByListNoAttendance.add(new SelectItem("FA", i18n.iValue("web.client.backingBean.report.message.AscendantDate")));
        }
        return orderByListNoAttendance;
    }

    public boolean isValidatedAllEmployee() {
        return validatedAllEmployee;
    }

    public void setValidatedAllEmployee(boolean validatedAllEmployee) {
        this.validatedAllEmployee = validatedAllEmployee;
    }

    public List<MetaData> getSelectedEmployeeMetadataList() {
        return selectedEmployeeMetadataList;
    }

    public void setSelectedEmployeeMetadataList(List<MetaData> selectedEmployeeMetadataList) {
        this.selectedEmployeeMetadataList = selectedEmployeeMetadataList;
    }
}
