package com.tigo.cs.view.report;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.Category;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.CustomScopeELResolver;
import com.tigo.cs.view.RequestParametersBean;

@ManagedBean(name = "reportVisitFullBean")
@CustomScoped(value = "#{customScope}")
@ReportFile(fileName = "rep_visit_full")
public class ReportVisitFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -8557671685008057653L;

    @EJB
    private ClientParameterValueFacade clientParameterValueFacade;
    @EJB
    private Notifier notifier;
    private final DateFormat sqlSdf = new SimpleDateFormat("dd/MM/yyyy");
    private List<Object[]> categoriesList;
    private Map<String, String> categoriesMap;
    private String categoriesCondition = "";
    private DualListModel<Category> categories;
    private List<Category> motivesSource;
    private List<Category> motivesTarget;

    private boolean valueChange = false;

    public ReportVisitFullBean() {
        this.chartReportOption = "userphone";
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,SV.SERVICEVALUE_COD DESC,  SVD.RECORDDATE_DAT ASC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,U.NAME_CHR, SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD DESC,  SVD.RECORDDATE_DAT ASC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY U.NAME_CHR, SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public String generateNativeQueryForChart() {
        String sql = null;
        DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
        if (chartReportOption.equals("userphone")) {

            if (typeReport.equals("visit")) {
                sql = "SELECT u.NAME_CHR, to_char(u.CELLPHONE_NUM), count(*) "
                    + "FROM SERVICE_VALUE_DETAIL svd "
                    + "INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                    + "INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                    + "INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                    + "INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                    + "WHERE 1 = 1  AND svd.COLUMN1_CHR in ('SAL','ENTSAL') and sv.ENABLED_CHR = '1' AND svd.ENABLED_CHR = '1' "
                    + "AND sv.COD_SERVICE = '1' " + "%s "
                    + "GROUP by u.CELLPHONE_NUM, u.NAME_CHR ";
                sql = String.format(sql, getWhereReport());
            } else if (typeReport.equals("novisit")) {
                sql = "SELECT distinct u.NAME_CHR, to_char(u.CELLPHONE_NUM),  "
                    + " ((ROUND(TO_DATE('%s','yyyy-mm-dd') - TO_DATE('%s','yyyy-mm-dd'))) - "
                    + " (SELECT COUNT (DISTINCT(SV.RECORDDATE_DAT)) FROM USERPHONE u "
                    + " INNER JOIN SERVICE_VALUE sv on sv.COD_USERPHONE= u.USERPHONE_COD  "
                    + " INNER JOIN SERVICE_VALUE_DETAIL svd on svd.cod_servicevalue = sv.servicevalue_cod  "
                    + " WHERE 1=1 AND svd.COLUMN1_CHR in ('SAL','ENTSAL') "
                    + " and sv.ENABLED_CHR = '1' AND svd.ENABLED_CHR = '1'  "
                    + " AND sv.COD_SERVICE = 1 AND u.COD_CLIENT = %s "
                    + " AND sv.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd')  "
                    + " AND sv.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd')  %s ) ) as CANTIDAD  "
                    + " FROM USERPHONE u  "
                    + " INNER JOIN USERPHONE_CLIENT_SER_FUNC ucs on ucs.COD_USERPHONE = u.USERPHONE_COD  "
                    + " WHERE 1 = 1 AND u.cod_client = %s "
                    + " AND u.ENABLED_CHR = '1' ";

                String where = getWhereReportNoVisit();
                sql = String.format(
                        sql,
                        sqlSdf.format(dateTo),
                        sqlSdf.format(dateFrom),
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString(),
                        sqlSdf.format(dateFrom),
                        sqlSdf.format(dateTo),
                        where,
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString());
                sql = sql.concat(where);
            }

        } else if (chartReportOption.equals("motive")) {
            String motiveList = "";
            categoriesCondition = "";
            String value;
            if (categories != null) {
                for (Object cat : categories.getTarget()) {
                    try {
                        cat = cat != null ? new String(cat.toString().getBytes(), "UTF8") : new String("");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    value = categoriesMap.get(cat.toString());
                    if (value != null)
                        motiveList += "'".concat(value).concat("',");
                }
                if (motiveList.length() > 0) {
                    motiveList = motiveList.substring(0,
                            motiveList.length() - 1);
                    categoriesCondition = "AND svd.COLUMN3_CHR IN ( "
                        + motiveList + ") ";
                }
            }
            if (chartType.equals("bar")) {
                sql = "SELECT Q.*, CASE WHEN (mcmo.ENABLED_CHR = '1' AND mdmo.value_chr IS NOT NULL) THEN mdmo.value_chr ELSE '"
                    + i18n.iValue("web.client.label.NoDescription")
                    + "' END  "
                    + "FROM ( "
                    + "		SELECT to_char(u.CELLPHONE_NUM), u.NAME_CHR, COUNT(*) AS COUNT, svd.COLUMN3_CHR  "
                    + "        FROM SERVICE_VALUE_DETAIL svd "
                    + "        INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                    + "        INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                    + "        INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                    + "        INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                    + " WHERE 1 = 1  AND svd.COLUMN1_CHR in ('SAL','ENTSAL') "
                    + categoriesCondition
                    + " AND sv.COD_SERVICE = 1 "
                    + " %s "
                    + " GROUP BY u.CELLPHONE_NUM, u.NAME_CHR, svd.COLUMN3_CHR ORDER BY u.CELLPHONE_NUM, u.NAME_CHR) Q "
                    + " LEFT JOIN META_DATA mdmo ON (mdmo.cod_client = %s AND mdmo.COD_META = 3 AND mdmo.cod_member = 1 AND mdmo.CODE_CHR = Q.COLUMN3_CHR) "
                    + " LEFT JOIN META_CLIENT mcmo ON (mcmo.cod_client = %s AND mcmo.COD_META = 3) ";
                Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                sql = String.format(sql, getWhereReport(), codClient, codClient);
            } else { // pie
                sql = "SELECT CASE WHEN (mcmo.ENABLED_CHR = '1' AND mdmo.value_chr IS NOT NULL) THEN mdmo.value_chr ELSE '"
                    + i18n.iValue("web.client.label.NoDescription")
                    + "' END,  "
                    + "Q.* "
                    + "FROM ( "
                    + "		SELECT svd.COLUMN3_CHR, COUNT(*) AS COUNT "
                    + "        FROM SERVICE_VALUE_DETAIL svd "
                    + "        INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
                    + "        INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
                    + "        INNER JOIN MESSAGE m ON (m.MESSAGE_COD = sv.COD_MESSAGE) "
                    + "        INNER JOIN CLIENT c ON (c.CLIENT_COD = m.COD_CLIENT) "
                    + " WHERE 1 = 1  AND svd.COLUMN1_CHR in ('SAL','ENTSAL') "
                    + " AND sv.COD_SERVICE = 1 "
                    + categoriesCondition
                    + " %s "
                    + " GROUP BY svd.COLUMN3_CHR ) Q "
                    + " LEFT JOIN META_DATA mdmo ON (mdmo.cod_client = %s AND mdmo.COD_META = 3 AND mdmo.cod_member = 1 AND mdmo.CODE_CHR = Q.COLUMN3_CHR) "
                    + " LEFT JOIN META_CLIENT mcmo ON (mcmo.cod_client = %s AND mcmo.COD_META = 3) ";
                Long codClient = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
                sql = String.format(sql, getWhereReport(), codClient, codClient);
            }

        }
        return sql;
    }

    @Override
    public String getChartReportOptionLabel() {
        chartReportOption = RequestParametersBean.getInstance().getChartReportOption();
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            chartReportOptionLabel = i18n.iValue("visit.chart.option.Userphone");
        } else if (chartReportOption != null
            && chartReportOption.equals("motive")) {
            chartReportOptionLabel = i18n.iValue("visit.chart.option.Motive");
        }
        return chartReportOptionLabel;
    }

    public String setReportTitle() {
        String title = "";
        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            if (typeReport.equals("visit")) {
                title = i18n.iValue("visit.chart.label.VisitNumberByUserphone");
            } else {
                title = i18n.iValue("visit.chart.label.NoVisitNumberByUserphone");
            }

        } else if (chartReportOption != null
            && chartReportOption.equals("motive")) {
            title = i18n.iValue("visit.chart.label.VisitNumberByMotive");
        }

        return title;
    }

    @Override
    public CartesianChartModel createCategoryModel() {
        CartesianChartModel categoryModel = new CartesianChartModel();

        if (chartReportOption != null && chartReportOption.equals("userphone")) {
            Map<Object, Number> dataBar = generateDataBar(generateNativeQueryForChart());

            ChartSeries series = new ChartSeries();
            String label = getCurrentViewId().replaceAll(".xhtml", "");
            label = label.replaceAll("/", ".").substring(1);
            series.setLabel(i18n.iValue(label));
            series.setData(dataBar);
            categoryModel.addSeries(series);
        } else if (chartReportOption != null
            && chartReportOption.equals("motive")) {
            Map<String, Map<Object, Number>> dataBarMap = generateDataBarForStack(generateNativeQueryForChart());

            Iterator it = dataBarMap.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Map<Object, Number> val = dataBarMap.get(key);

                Map<Object, Number> sortedMap = new TreeMap(val);

                ChartSeries series = new ChartSeries();
                series.setLabel(String.valueOf(key));

                series.setData(sortedMap);

                categoryModel.addSeries(series);
            }

        }

        // Se sete el titulo del Reporte
        title = setReportTitle();

        return categoryModel;
    }

    public Map<String, Map<Object, Number>> generateDataBarForStack(String sql) {
        Map<Object, Number> map;
        Map<String, Map<Object, Number>> mapOfMaps = new HashMap<String, Map<Object, Number>>();
        Map<String, String> categoryMap = new HashMap<String, String>();
        maxValue = 0;
        int value = 0;
        String key;

        /*
         * SE TRAE LA LISTA DE REGISTROS PARA PROCESAR
         */
        List<Object[]> list = serviceFacade.executeNativeQuery(sql);

        /*
         * SE TRAE LA LISTA DE CATEGORIAS PARA EL REPORTE DE BARRAS
         */

        List<Category> catList = null;
        if (categories.getTarget() != null && categories.getTarget().size() > 0)
            catList = categories.getTarget();
        else if (categories.getSource() != null
            && categories.getSource().size() > 0)
            catList = categories.getSource();

        for (Object cat : catList) {
            try {
                cat = cat != null ? new String(cat.toString().getBytes(), "UTF8") : new String("");
                String catString = cat.toString();
                String[] str = catString.split("-");
                categoryMap.put(str[0].concat(" - ").concat(str[1]),
                        str[1].toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        /*
         * SE PROCESAN LOS REGISTROS
         */
        for (Object[] obj : list) {
            if (obj[3] == null || obj[1] == null)
                continue;
            key = ((String) obj[0]).concat(" - ").concat((String) obj[1]);
            map = mapOfMaps.get(key);
            if (map == null)
                map = new HashMap<Object, Number>();
            value = ((BigDecimal) obj[2]).intValue();
            map.put(((String) obj[3]).concat(" - ").concat((String) obj[4]),
                    value);
            // map.put(obj[3], value);
            mapOfMaps.put(key, map);
        }

        /*
         * SE AGREGAN LAS CATEGORIAS FALTANTES A TODOS LOS USERPHONES
         */
        for (Iterator<String> it = mapOfMaps.keySet().iterator(); it.hasNext();) {
            String keyMap = it.next();
            map = mapOfMaps.get(keyMap);
            for (Iterator<String> it2 = categoryMap.keySet().iterator(); it2.hasNext();) {
                String keyCategory = it2.next();
                if (!map.containsKey(keyCategory))
                    map.put(keyCategory, 0);
            }
            mapOfMaps.put(keyMap, map);
        }

        /*
         * SE TRAE EL MAXIMO VALOR PARA EL MAX DEL GRAFICO
         */
        Object maxValueObj = serviceFacade.executeNativeQuerySingleResult(getSqlForMaxValue());
        if (maxValueObj != null)
            maxValue = ((BigDecimal) maxValueObj).intValue();

        return mapOfMaps;
    }

    private String getSqlForCategory(String categoriesCondition) {
        if (categoriesCondition == null)
            categoriesCondition = "";
        String sql = " SELECT DISTINCT(svd.COLUMN3_CHR) "
            + " , CASE WHEN (mcmo.ENABLED_CHR = '1' AND mdmo.value_chr IS NOT NULL) THEN mdmo.value_chr ELSE '"
            + i18n.iValue("web.client.label.NoDescription")
            + "' END "
            + " FROM SERVICE_VALUE_DETAIL svd "
            + " INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
            + " INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
            + " LEFT JOIN META_DATA mdmo ON (mdmo.cod_client = u.COD_CLIENT AND mdmo.COD_META = 3 AND mdmo.cod_member = 1 AND mdmo.CODE_CHR = svd.COLUMN3_CHR) "
            + " LEFT JOIN META_CLIENT mcmo ON (mcmo.cod_client = u.COD_CLIENT AND mcmo.COD_META = 3) "
            + " WHERE 1 = 1  AND svd.COLUMN1_CHR in ('SAL','ENTSAL') AND svd.COLUMN3_CHR IS NOT NULL "
            + categoriesCondition + " AND sv.COD_SERVICE = 1 " + " %s ";

        // if (includeDates)
        // sql = String.format(sql, getWhereReport());
        // else
        sql = String.format(sql, getWhere());

        return sql;
    }

    private String getSqlForMaxValue() {
        if (categoriesCondition == null)
            categoriesCondition = "";
        String sql = " SELECT max(count(*)) "
            + " FROM SERVICE_VALUE_DETAIL svd "
            + " INNER JOIN SERVICE_VALUE sv ON (svd.COD_SERVICEVALUE = sv.SERVICEVALUE_COD) "
            + " INNER JOIN USERPHONE u ON (sv.COD_USERPHONE = u.USERPHONE_COD) "
            + " WHERE 1 = 1  AND svd.COLUMN1_CHR in ('SAL','ENTSAL')  "
            + " AND sv.COD_SERVICE = '1' " + " %s " + categoriesCondition
            + " GROUP BY svd.COLUMN3_CHR ";

        sql = String.format(sql, getWhereReport());

        return sql;
    }

    public String getWhere() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        sqlWhere = String.format(sqlWhere.concat(" AND U.COD_CLIENT = %s"),
                clientCod.toString());
        sqlWhere += MessageFormat.format(
                " AND sv.ENABLED_CHR = '1' and svd.ENABLED_CHR = '1' and sv.ENABLED_CHR = '1' "
                    + "and svd.ENABLED_CHR = '1' and EXISTS "
                    + "(select * from USERPHONE_CLASSIFICATION uc "
                    + "where uc.cod_userphone = u.userphone_cod "
                    + "and uc.cod_classification in ({0})) ", classifications);

        if (dateFrom == null || dateTo == null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            GregorianCalendar now = new GregorianCalendar();
            Date dateTo = now.getTime();
            now.add(Calendar.MONTH, -2);
            Date dateFrom = now.getTime();
            sqlWhere += String.format(
                    " AND SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd') "
                        + " SV.RECORDDATE_DAT <= AND TO_DATE('%s', 'yyyy-mm-dd')",
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        } else if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlWhere = String.format(
                    sqlWhere.concat(" and sv.ENABLED_CHR = '1' "
                        + " AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd') "
                        + " AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd')"),
                    sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }

        return sqlWhere;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DualListModel<Category> getCategories() {
        if (chartReportOption.equals("motive")) {
            motivesSource = new ArrayList<Category>();
            motivesTarget = new ArrayList<Category>();
            categoriesList = serviceFacade.executeNativeQuery(getSqlForCategory(null));
            categoriesMap = new HashMap<String, String>();

            for (Object[] obj : categoriesList) {
                if (obj[0] == null)
                    continue;
                String obj0 = obj[0] != null ? obj[0].toString() : "";
                String obj1 = obj[1] != null ? obj[1].toString() : "";
                motivesSource.add(new Category(obj0, obj1));
                categoriesMap.put(obj0.concat("-").concat(obj1), obj0);
            }
            categories = new DualListModel<Category>(motivesSource, motivesTarget);
        }

        valueChange = false;
        return categories;
    }

    public void setCategories(DualListModel<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String validateParametersChart() {
        if (dateFrom == null || dateTo == null) {
            parametersValidatedChart = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.DateRange"));
            return null;
        }
        if (dateFrom.after(dateTo)) {
            parametersValidatedChart = false;
            setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidDateRange"));
            return null;
        }

        if (categories != null
            && (chartReportOption != null
                && (chartReportOption.equals("motive")) && categories.getTarget().size() > 20)) {
            parametersValidatedChart = false;
            setWarnMessage(i18n.iValue("visit.chart.message.CategoryListLimited"));
            categories.setSource(motivesSource);
            categories.setTarget(new ArrayList<Category>());
            return null;
        }

        if (categories != null
            && (chartReportOption != null
                && (chartReportOption.equals("motive")) && categories.getTarget().size() == 0)) {
            if (categories.getSource().size() > 20) {
                parametersValidatedChart = false;
                setWarnMessage(i18n.iValue("visit.chart.message.MustSelectAtLeastOneElement"));
                return null;
            }
        }
        parametersValidatedChart = true;

        return null;
    }

    public void valueChangeDate1(DateSelectEvent event) {
        valueChange = true;
        dateFrom = event.getDate();
        getCategories();
    }

    public void valueChangeDate2(DateSelectEvent event) {
        valueChange = true;
        dateTo = event.getDate();
        getCategories();
    }

    /*-----------------------------------CUSTOM SCOPE---------------------------------------*/

    public String getValue() {
        return "Resolved";
    }

    // Lifecycle Methods

    @PostConstruct
    public void postConstruct() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getRequestMap().put("postConstructStatus",
                "Invoked");
    }

    @PreDestroy
    public void preDestroy() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getRequestMap().put("preDestroyStatus",
                "Invoked");
    }

    // Public Methods

    public String destroyScope() {
        if (this.userphoneList != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            CustomScopeELResolver.destroyScope(ctx);
        }
        return null;

    }

    public String generateReportVisit() {
        if (!parametersValidated) {
            return validateParameters();
        }
        try {
            signalReport(ReportType.PDF);

            String durationFormat = null;

            durationFormat = clientParameterValueFacade.findByCode(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    "client.duration.format");

            if (durationFormat == null)
                durationFormat = i18n.iValue("web.client.screen.configuration.field.ExtendedDurationFormatValue");

            String reportName = "";

            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("WHERE", getWhereReport());
            System.out.println("WHERE REPORT" + getWhereReport());
            if ((getSelectedClassificationList() != null && getSelectedClassificationList().size() > 0)
                || validatedAllClassification) {
                reportName = "rep_visit_full_classification";
            } else {
                reportName = getReportName();
            }

            params.put("ORDER_BY", getOrderByReport());
            params.put("PARAMETERS_DESCRIPTION",
                    getParameterDescriptionReport());
            params.put(
                    "USER",
                    SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
            params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
            params.put(JRParameter.REPORT_RESOURCE_BUNDLE,
                    i18n.getResourceBundle());
            params.put("DURATION_FORMAT", durationFormat);

            ClientFile cf = fileFacade.getClientLogo(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            try {
                if (cf != null) {
                    params.put("CLIENT_LOGO",
                            JRImageLoader.loadImage(cf.getFileByt()));
                }
            } catch (JRException ex) {
            }

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

    // REPORTE DE NO VISITA
    protected String typeReport = "visit";

    public String getTypeReport() {
        return typeReport;
    }

    public void setTypeReport(String typeReport) {
        this.typeReport = typeReport;
    }

    @Override
    public String generateReport() {
        if (typeReport.equals("visit")) {
            return generateReportVisit();
        } else {
            return generateReportNoVisit();
        }

    }

    // SECTION OF NO ATTENDANCE REPORT
    public String getWhereReportNoVisit() {
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

            if (selectedUserphoneList != null
                && selectedUserphoneList.size() > 0 && !validatedAllUsers) {
                for (Userphone u : selectedUserphoneList) {
                    valor += u.getUserphoneCod().toString() + ",";
                }
                if (valor.length() > 0) {
                    valor = valor.substring(0, valor.length() - 1);
                    where = where.concat(" AND U.USERPHONE_COD IN (" + valor
                        + ")");
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
                        where = where.concat(" AND CL.CLASSIFICATION_COD IN ("
                            + valor + ")");
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

    public String getOrderNoVisit() {
        String sqlOrderBy = "";
        if (selectedOrderby.equals("FD")) {
            sqlOrderBy = " ORDER BY FECHA DESC";
        } else {
            sqlOrderBy = " ORDER BY FECHA";
        }

        return sqlOrderBy;
    }

    public String getReportNameNoVisit() {
        String reportName = "";

        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            return reportName = "rep_no_visit_classification";
        } else {
            return reportName = "rep_no_visit";
        }

    }

    public String generateReportNoVisit() {
        if (!parametersValidated) {
            return validateParameters();
        }
        try {
            signalReport(ReportType.PDF);
            Map<Object, Object> params = new HashMap<Object, Object>();
            params.put("WHERE", getWhereReportNoVisit());
            params.put("ORDER_BY", getOrderNoVisit());
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

            String reportName = getReportNameNoVisit();

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

}
