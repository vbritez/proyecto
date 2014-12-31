package com.tigo.cs.view.report;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.ServiceFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.RequestParametersBean;

public abstract class AbstractFullReportBean extends SMBaseBean {

	private static final long serialVersionUID = 5134520974199970764L;
	@EJB
	protected UserphoneFacade userphoneFacade;
	@EJB
	private UserwebFacade userwebFacade;
	@EJB
	protected ClientFileFacade fileFacade;
	@EJB
	protected ClassificationFacade classificationFacade;
	@EJB
	private Notifier notifier;
	@EJB
	protected ServiceFacade serviceFacade;
	@EJB
	protected I18nBean i18n;
	protected Date dateFrom;
	protected Date dateTo;
	protected List<Userphone> userphoneList;
	protected Userphone selectedUserphone;
	protected List<Userphone> selectedUserphoneList;
	protected List<SelectItem> orderByList;
	protected String selectedOrderby;
	protected String reportType;
	protected boolean parametersValidated = false;
	protected boolean parametersValidatedChart = false;
	private ChartModel chartModel;
	protected String chartReportOption;
	protected String chartType = "bar";
	protected Integer maxValue;
	protected String chartReportOptionLabel;
	protected String title;
	protected Boolean validatedAllUsers = false;

	protected List<Classification> classificationList; // Lista de clasificacion
													   // del usuario logueado
	protected List<Classification> selectedClassificationList;
	protected Boolean validatedAllClassification = false;
	protected Boolean check1;
	protected Boolean check2;
	protected Boolean checkUsers;
	protected Boolean checkClassification;

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

		if (((selectedUserphoneList == null || selectedUserphoneList.size() == 0) && !validatedAllUsers) && ((selectedClassificationList == null || selectedClassificationList.size() == 0) && !validatedAllClassification)) {
			parametersValidated = false;
			setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidUserphoneClassification"));
			return null;
		}
		parametersValidated = true;

		return null;
	}

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

		parametersValidatedChart = true;

		return null;
	}

	public boolean isParametersValidatedChart() {
		return parametersValidatedChart;
	}

	public void setParametersValidatedChart(boolean parametersValidatedChart) {
		this.parametersValidatedChart = parametersValidatedChart;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Userphone getSelectedUserphone() {
		return selectedUserphone;
	}

	public void setSelectedUserphone(Userphone selectedUserphone) {
		this.selectedUserphone = selectedUserphone;
	}

	public List<Userphone> getUserphoneList() {
		if (userphoneList == null) {
			userphoneList = userphoneFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());
		}
		return userphoneList;
	}

	public void setUserphoneList(List<Userphone> userphoneList) {
		this.userphoneList = userphoneList;
	}

	public List<Userphone> getSelectedUserphoneList() {
		return selectedUserphoneList;
	}

	public void setSelectedUserphoneList(List<Userphone> selectedUserphoneList) {
		this.selectedUserphoneList = selectedUserphoneList;
	}

	public List<SelectItem> getOrderByList() {
		if (orderByList == null) {
			orderByList = new ArrayList<SelectItem>();
			orderByList.add(new SelectItem("FD", i18n.iValue("web.client.backingBean.report.message.DescendantDate")));
			orderByList.add(new SelectItem("FA", i18n.iValue("web.client.backingBean.report.message.AscendantDate")));
			orderByList.add(new SelectItem("US", i18n.iValue("web.client.backingBean.report.message.User")));
		}
		return orderByList;
	}

	public void setOrderByList(List<SelectItem> orderByList) {
		this.orderByList = orderByList;
	}

	public String getSelectedOrderby() {
		return selectedOrderby;
	}

	public void setSelectedOrderby(String selectedOrderby) {
		this.selectedOrderby = selectedOrderby;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public boolean isParametersValidated() {
		return parametersValidated;
	}

	public void setParametersValidated(boolean parametersValidated) {
		this.parametersValidated = parametersValidated;
	}

	public Boolean getValidatedAllUsers() {
		return validatedAllUsers;
	}

	public void setValidatedAllUsers(Boolean validatedAllUsers) {
		this.validatedAllUsers = validatedAllUsers;
	}

	public ClassificationFacade getClassificationFacade() {
		return classificationFacade;
	}

	public void setClassificationFacade(ClassificationFacade classificationFacade) {
		this.classificationFacade = classificationFacade;
	}

	public ClientFileFacade getFileFacade() {
		return fileFacade;
	}

	public void setFileFacade(ClientFileFacade fileFacade) {
		this.fileFacade = fileFacade;
	}

	public UserphoneFacade getUserphoneFacade() {
		return userphoneFacade;
	}

	public void setUserphoneFacade(UserphoneFacade userphoneFacade) {
		this.userphoneFacade = userphoneFacade;
	}

	public String getWhereReport() {
		Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
		String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
		String sqlWhere = "";
		String valor = "";
		sqlWhere = String.format(sqlWhere.concat(" AND U.COD_CLIENT = %s"), clientCod.toString());

		if (validatedAllUsers) {
			sqlWhere += MessageFormat.format(" and EXISTS " + "(select * from USERPHONE_CLASSIFICATION uc " + "where uc.cod_userphone = u.userphone_cod " + "and uc.cod_classification in ({0})) ", classifications);
		}

		if (dateFrom != null && dateTo != null) {
			DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd");
			sqlWhere = String.format(sqlWhere.concat(" and sv.ENABLED_CHR = '1' " + "and svd.ENABLED_CHR = '1' " + "AND SV.RECORDDATE_DAT >= TO_DATE('%s', 'yyyy-mm-dd') " + "AND SV.RECORDDATE_DAT <= TO_DATE('%s', 'yyyy-mm-dd') "), sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
		}

		if (selectedUserphoneList != null && selectedUserphoneList.size() > 0 && !validatedAllUsers) {
			for (Userphone u : selectedUserphoneList) {
				valor += u.getUserphoneCod().toString() + ",";
			}
			if (valor.length() > 0) {
				valor = valor.substring(0, valor.length() - 1);
				sqlWhere = sqlWhere.concat(" AND U.USERPHONE_COD IN (" + valor + ")");
			}
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
			if (selectedClassificationList != null && selectedClassificationList.size() > 0) {
				String classificationsCod = "";
				for (Classification c : selectedClassificationList) {
					classificationsCod += c.getClassificationCod().toString() + ",";
				}
				if (classificationsCod.length() > 0) {
					classificationsCod = classificationsCod.substring(0, classificationsCod.length() - 1);
					sqlWhere += MessageFormat.format(" and EXISTS " + "(select * from USERPHONE_CLASSIFICATION uc " + "where uc.cod_userphone = u.userphone_cod " + "and uc.cod_classification in ({0})) ", classificationsCod);
				}
			}
		}

		if (!parametersValidatedChart) {
			if (selectedClassificationList != null && selectedClassificationList.size() > 0) {
				for (Classification c : selectedClassificationList) {
					valor += c.getClassificationCod().toString() + ",";
				}
				if (valor.length() > 0) {
					valor = valor.substring(0, valor.length() - 1);
					sqlWhere = sqlWhere.concat(" AND CL.CLASSIFICATION_COD IN (" + valor + ")");
				}
			}
		}
		if (validatedAllClassification) {
			selectedClassificationList = null;
		}
		return sqlWhere;
	}

	public String getParameterDescriptionReport() {
		String parametersDesc = "";
		if (dateFrom != null && dateTo != null) {
			DateFormat descSdf = new SimpleDateFormat("dd/MM/yyyy");
			parametersDesc = String.format(i18n.iValue("web.client.backingBean.report.message.ParameterDescDate"), descSdf.format(dateFrom), i18n.iValue("web.client.backingBean.report.message.ParameterDescDateTo"), descSdf.format(dateTo));
		}
		if (selectedUserphone != null) {
			parametersDesc = String.format(parametersDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescUser")), selectedUserphone.getNameChr().toString());
		}
		return parametersDesc;
	}

	public String getOrderByReport() {
		String sqlOrderBy = "";
		if (getSelectedClassificationList() != null && getSelectedClassificationList().size() > 0 && !parametersValidatedChart) {
			if (selectedOrderby.equals("FD")) {
				// sqlOrderBy = " ORDER BY SV.RECORDDATE_DAT DESC";
				sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD, M.DATEIN_DAT DESC";
			} else if (selectedOrderby.equals("FA")) {
				// sqlOrderBy = " ORDER BY SV.RECORDDATE_DAT";
				sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD, M.DATEIN_DAT ";
			} else if (selectedOrderby.equals("US")) {
				// sqlOrderBy = " ORDER BY U.NAME_CHR, SV.RECORDDATE_DAT";
				sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD, U.NAME_CHR, M.DATEIN_DAT ";
			}
		} else {
			if (selectedOrderby.equals("FD")) {
				// sqlOrderBy = " ORDER BY SV.RECORDDATE_DAT DESC";
				sqlOrderBy = " ORDER BY M.DATEIN_DAT DESC";
			} else if (selectedOrderby.equals("FA")) {
				// sqlOrderBy = " ORDER BY SV.RECORDDATE_DAT";
				sqlOrderBy = " ORDER BY M.DATEIN_DAT ";
			} else if (selectedOrderby.equals("US")) {
				// sqlOrderBy = " ORDER BY U.NAME_CHR, SV.RECORDDATE_DAT";
				sqlOrderBy = " ORDER BY U.NAME_CHR, M.DATEIN_DAT ";
			}
		}
		return sqlOrderBy;
	}

	public String getReportName() {
		String reportName = null;
		Annotation[] annotations = getClass().getAnnotations();
		for (Annotation a : annotations) {
			if (a instanceof ReportFile) {
				ReportFile reportFile = (ReportFile) a;
				reportName = reportFile.fileName();
			}
		}
		return reportName;
	}

	public String generateReportChart() {
		if (!parametersValidatedChart) {
			return validateParametersChart();
		}

		if (chartType.equals("pie")) {
			PieChartModel pieModel = createPieModel();
			RequestParametersBean.getInstance().setChartModel(pieModel);
		} else if (chartType.equals("bar")) {
			CartesianChartModel barModel = createCategoryModel();
			RequestParametersBean.getInstance().setChartModel(barModel);
		}
		RequestParametersBean.getInstance().setChartType(chartType);
		RequestParametersBean.getInstance().setChartReportOption(chartReportOption);
		RequestParametersBean.getInstance().setMaxValue(getMaxValue());
		RequestParametersBean.getInstance().setReportTitle(title);

		parametersValidatedChart = false;
		// return "repvisitfull_charts";
		return getCurrentViewId().replaceAll(".xhtml", "_charts");

	}

	public String generateReport() {
		if (!parametersValidated) {
			return validateParameters();
		}
		try {
			signalReport(ReportType.PDF);
			Map<Object, Object> params = new HashMap<Object, Object>();
			params.put("WHERE", getWhereReport());
			params.put("ORDER_BY", getOrderByReport());
			params.put("PARAMETERS_DESCRIPTION", getParameterDescriptionReport());
			params.put("USER", SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
			params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
			params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

			ClientFile cf = fileFacade.getClientLogo(SecurityBean.getInstance().getLoggedInUserClient().getClient());
			try {
				if (cf != null) {
					params.put("CLIENT_LOGO", JRImageLoader.loadImage(cf.getFileByt()));
				}
			} catch (JRException ex) {
			}

			String reportName = getReportName();

			if (reportName.equalsIgnoreCase("rep_courier_full")) {
				if (getSelectedClassificationList() != null && getSelectedClassificationList().size() > 0) {
					reportName = "rep_courier_full_classification";
					String sqlOrderBy = "";
					if (selectedOrderby.equals("FD")) {
						sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD ASC,SV.SERVICEVALUE_COD DESC,  SVD.RECORDDATE_DAT ASC";
					} else if (selectedOrderby.equals("FA")) {
						sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
					} else if (selectedOrderby.equals("US")) {
						sqlOrderBy = " ORDER BY CL.CLASSIFICATION_COD,U.NAME_CHR, SV.SERVICEVALUE_COD, SVD.RECORDDATE_DAT";
					}
					params.put("ORDER_BY", sqlOrderBy);
				}
			}

			if (reportName != null) {
				if (reportType.equals("xls")) {
					reportName += "_xls";
				}
			}
			Connection conn = SMBaseBean.getDatabaseConnecction();
			if (reportType.equals("pdf")) {
				JasperReportUtils.respondReport(reportName, params, true, conn, ReportType.PDF);
			} else if (reportType.equals("xls")) {
				JasperReportUtils.respondReport(reportName, params, true, conn, ReportType.XLS);
			}

			parametersValidated = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String generateNativeQueryForChart() {
		return null;
	}

	public ChartModel getChartModel() {
		if (chartModel == null) {
			chartModel = RequestParametersBean.getInstance().getChartModel();
		}
		return chartModel;
	}

	public CartesianChartModel createCategoryModel() {
		CartesianChartModel categoryModel = new CartesianChartModel();
		// Map<Object, Number> data =
		// generateDataBar(generateNativeQueryForChart());
		Map<Object, Number> dataBar = generateDataBar(generateNativeQueryForChart());

		ChartSeries series = new ChartSeries();
		String label = getCurrentViewId().replaceAll(".xhtml", "");
		label = label.replaceAll("/", ".").substring(1);
		series.setLabel(i18n.iValue(label));
		series.setData(dataBar);

		categoryModel.addSeries(series);

		return categoryModel;
	}

	public PieChartModel createPieModel() {
		PieChartModel pieModel = new PieChartModel();
		// Map<String, Number> data =
		// generateDataPie(generateNativeQueryForChart());
		Map<String, Number> dataPie = generateDataPie(generateNativeQueryForChart());

		pieModel.setData(dataPie);
		// pieModel.set("Brand 1", 540);
		// pieModel.set("Brand 2", 325);
		// pieModel.set("Brand 3", 702);
		// pieModel.set("Brand 4", 421);

		return pieModel;
	}

	public Map<String, Number> generateDataPie(String sql) {
		Map<String, Number> map = new HashMap<String, Number>();
		List<Object[]> list = serviceFacade.executeNativeQuery(sql);
		for (Object[] obj : list) {
			map.put(((String) obj[0] != null ? (String) obj[0] : "").concat(" - ").concat(((String) obj[1] != null ? (String) obj[1] : "")), ((BigDecimal) obj[2]).intValue());
		}
		return map;
	}

	public Map<Object, Number> generateDataBar(String sql) {
		Map<Object, Number> map = new HashMap<Object, Number>();
		maxValue = 0;
		int value = 0;
		List<Object[]> list = serviceFacade.executeNativeQuery(sql);
		for (Object[] obj : list) {
			value = ((BigDecimal) obj[2]).intValue();
			map.put(((String) obj[0]).concat(" - ").concat((String) obj[1]), value);
			if (value > maxValue)
				maxValue = value;
		}

		return map;
	}

	private int roundUp(int numToRound, int multiple) {
		if (multiple == 0) {
			return numToRound;
		}

		int remainder = numToRound % multiple;
		if (remainder == 0)
			return numToRound;
		return numToRound + multiple - remainder;
	}

	protected void signalReport(ReportType repType) {
		String criterio = getWhereReport().isEmpty() ? "" : i18n.iValue("web.client.backingBean.abstractServiceBean.message.Criteria").concat(getWhereReport());
		String action = "";

		action = i18n.iValue("web.client.backingBean.abstractServiceBean.message.ServiceRecordRequest").concat(getReportName()).concat(criterio).concat(".");

		Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
		notifier.signal(this.getClass(), Action.REPORT, userweb, null, this.getCurrentViewId(), repType.name().concat(i18n.iValue("web.client.backingBean.abstractServiceBean.message.Report")).concat(action), getSessionId(), getIpAddress());
	}

	public String getChartReportOption() {
		if (RequestParametersBean.getInstance().getChartReportOption() != null)
			return RequestParametersBean.getInstance().getChartReportOption();
		else
			return chartReportOption;
	}

	public void setChartReportOption(String chartReportOption) {
		this.chartReportOption = chartReportOption;
	}

	public String getChartType() {
		if (RequestParametersBean.getInstance().getChartType() != null)
			return RequestParametersBean.getInstance().getChartType();
		else
			return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Integer getMaxValue() {
		if (maxValue == null) {
			maxValue = RequestParametersBean.getInstance().getMaxValue();
		}
		if (maxValue != null)
			maxValue = roundUp(maxValue, 4);
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public String getChartReportOptionLabel() {
		return chartReportOptionLabel;
	}

	public void setChartReportOptionLabel(String chartReportOptionLabel) {
		this.chartReportOptionLabel = chartReportOptionLabel;
	}

	public String getTitle() {
		if (RequestParametersBean.getInstance().getReportTitle() != null)
			return RequestParametersBean.getInstance().getReportTitle();
		else
			return title;
	}

	public void selectAllUserphones() {
		if (validatedAllUsers)
			selectedUserphoneList = null;
		selectedClassificationList = null;
		validatedAllClassification = false;
	}

	public List<com.tigo.cs.domain.Service> getServiceList() {
		return serviceFacade.findAll();
	}

	public void selectAll() {
		selectedClassificationList = null;
		selectedUserphoneList = null;
	};

	// CORTE

	/*
	 * Retorna la lista de clasificación o clasificaciones asociadas al usuario
	 * logueado
	 */
	public List<Classification> getClassificationList() {
		if (classificationList == null) {
			classificationList = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
		}
		return classificationList;
	}

	public void setClassificationList(List<Classification> classificationList) {
		this.classificationList = classificationList;
	}

	/* Retorna la lista de clasificaciones seleccionadas por el usuario */
	public List<Classification> getSelectedClassificationList() {
		return selectedClassificationList;
	}

	public void setSelectedClassificationList(List<Classification> selectedClassificationList) {
		this.selectedClassificationList = selectedClassificationList;
	}

	// metodo invocado por el listener del checkbox Todos de la seccion de
	// clasificacion
	public void selectAllClassification() {
		if (validatedAllClassification)
			selectedClassificationList = null;
		selectedUserphoneList = null;
		validatedAllUsers = false;
	}

	public Boolean getValidatedAllClassification() {
		return validatedAllClassification;
	}

	public void setValidatedAllClassification(Boolean validatedAllClassification) {
		this.validatedAllClassification = validatedAllClassification;
	}

	// Variable verificada en el disabled del componente que muestra la lista de
	// clasificaciones
	public Boolean getCheck1() {
		check1 = false;
		if (validatedAllUsers || validatedAllClassification || selectedUserphoneList != null) {
			check1 = true;
			return check1;
		}
		return check1;
	}

	public void setCheck1(Boolean check1) {
		this.check1 = check1;
	}

	// Variable verificada en el disabled del componente que muestra la lista de
	// userphones
	public Boolean getCheck2() {
		check2 = false;
		if (validatedAllUsers || validatedAllClassification || selectedClassificationList != null) {
			check2 = true;
			return check2;
		}
		return check2;
	}

	public void setCheck2(Boolean check2) {
		this.check2 = check2;
	}

	// Variable verificada en el disabled del componente checkbox Todos de la
	// parte de clasificacion
	public Boolean getCheckUsers() {
		checkUsers = false;
		if (validatedAllUsers || selectedUserphoneList != null) {
			checkUsers = true;
			return checkUsers;
		}
		return checkUsers;
	}

	public void setCheckUsers(Boolean checkUsers) {
		this.checkUsers = checkUsers;
	}

	// Variable verificada en el disabled del componente checkbox Todos de la
	// parte de userphones
	public Boolean getCheckClassification() {
		checkClassification = false;
		if (validatedAllClassification || selectedClassificationList != null) {
			checkClassification = true;
			return checkClassification;
		}

		return checkClassification;
	}

	public void setCheckClassification(Boolean checkClassification) {
		this.checkClassification = checkClassification;
	}

	// metodo invocado en el listener al seleccionar alguna clasificacion para
	// desabilitar la seccion de userphones
	public void putTrueCheck2() {
		check2 = true;
	}

	// metodo invocado en el listener al seleccionar algun userphones para
	// desabilitar la seccion de clasificaciones
	public void putTrueCheck1() {
		check1 = true;
	}

}
