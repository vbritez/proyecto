package com.tigo.cs.view.report;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.event.DateSelectEvent;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportFile;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.ClientGoal;
import com.tigo.cs.domain.Moduleclient;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClientGoalFacade;
import com.tigo.cs.facade.ModuleclientFacade;
import com.tigo.cs.facade.ServiceFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.SessionBean;

@ManagedBean(name = "reportClientGoalBean")
@ReportFile(fileName = "rep_client_goal")
@ViewScoped
public class ReportClientGoalBean extends AbstractFullReportBean {

	private static final long serialVersionUID = -484044466144264847L;

	@EJB
	private ModuleclientFacade mcFacade;
	@EJB
	private ServiceFacade serviceFacade;// ejb usado para traer todos los
										// servicios disponibles para el usuario
	@EJB
	private ClientGoalFacade clientgoalFacade; // ejb usado para traer todos los
											   // periodos disponibles en el
											   // rango de fecha y servicio
											   // seleccionado
	private Date date1; // Fecha Desde
	private Date date2; // Fecha Hasta
	private Service selectedService; // servicio seleccionado
	private ClientGoal selectedPeriod; // periodo seleccionado
	private List<ClientGoal> periodsList; // lista de periodos que puede ser
										  // seleccionado
	private List<Service> serviceList; // lista de servicios que puede ser
									   // seleccionado

	private List<String> rangeList;
	private String selectedRangeList;
	protected Boolean allRanges = false;

	private Date fechaIni;
	private Date fechaFin;

	public ReportClientGoalBean() {

	}

	public void valueChangePeriod() {
		getUserphoneList();
		getRangeList();
	}

	/*
	 * m�todo invocado al seleccionar el campo Fecha Desde de manera a setear en
	 * la variable date1 el valor del mismo, para luego setear en la variable
	 * periodsList, la lista de clientGoal disponibles de acuerdo al rango de
	 * fecha y servicio especificado
	 */
	public void valueChangeDate1(DateSelectEvent event) {
		this.date1 = event.getDate();
		periodsAvailable();
	}

	/*
	 * m�todo invocado al seleccionar el campo Fecha Hasta de manera a setear en
	 * la variable date2 el valor del mismo, para luego setear en la variable
	 * periodsList, la lista de clientGoal disponibles de acuerdo al rango de
	 * fecha y servicio especificado
	 */
	public void valueChangeDate2(DateSelectEvent event) {
		this.date2 = event.getDate();
		periodsAvailable();
	}

	/*
	 * m�todo invocado al seleccionar el rango de fecha y servicio,el cual setea
	 * a la variable periodsList la lista de clientGoal de acuerdo al rango de
	 * fecha y servicio especificado
	 */
	public void periodsAvailable() {
		try {
			if (selectedService != null){
				this.periodsList = clientgoalFacade.getAvailableGoals(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
																		selectedService.getServiceCod(), this.date1, this.date2);
				for (ClientGoal goal : periodsList) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");			        
					String desc = goal.getDescription().concat(" (").concat(sdf.format(goal.getValidityDateFrom()));
			        
					if (goal.getValidityDateTo() != null)
			        	desc = desc.concat(" - ").concat(sdf.format(goal.getValidityDateTo()));
			        
					desc = desc.concat(")");
			        goal.setDescription(desc);
				}
			}
		} catch (GenericFacadeException e) {
			e.printStackTrace();
		}
	}

	/* retorna la lista de servicios disponibles para la funcionalidad de metas */
	public List<Service> getServicesAvailable() {
		List<Service> servicesAvailable = new ArrayList<Service>();
		servicesAvailable.add(serviceFacade.find(1L));
		servicesAvailable.add(serviceFacade.find(2L));
		servicesAvailable.add(serviceFacade.find(3L));
		servicesAvailable.add(serviceFacade.find(5L));
		servicesAvailable.add(serviceFacade.find(6L));
		servicesAvailable.add(serviceFacade.find(7L));
		servicesAvailable.add(serviceFacade.find(10L));
		servicesAvailable.add(serviceFacade.find(15L));
		servicesAvailable.add(serviceFacade.find(17L));
		servicesAvailable.add(serviceFacade.find(18L));
		return servicesAvailable;
	}

	/* GETTERS AND SETTERS */
	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Service getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(Service selectedService) {
		this.selectedService = selectedService;
	}

	public ClientGoal getSelectedPeriod() {
		return selectedPeriod;
	}

	public void setSelectedPeriod(ClientGoal selectedPeriod) {
		this.selectedPeriod = selectedPeriod;
	}

	public List<ClientGoal> getPeriodsList() {
		return periodsList;
	}

	public void setPeriodsList(List<ClientGoal> periodsList) {
		this.periodsList = periodsList;
	}

	/* retorna la lista de servicios disponibles para el usuario logueado */
	@Override
	public List<Service> getServiceList() {
		if (serviceList == null) {
			serviceList = new ArrayList<Service>();
			try {
				List<Moduleclient> mcList = mcFacade.getActiveModules();
				Userweb user = SecurityBean.getInstance().getLoggedInUserClient();
				for (Moduleclient moduleclient : mcList) {
					moduleclient.setDescriptionChr(moduleclient.getDescriptionChr());
					if (moduleclient.getDescriptionChr().equalsIgnoreCase("web.client.module.Service")) {
						List<Screenclient> screenClientList = mcFacade.getScreenclientListByModuleAndUserweb(moduleclient.getModuleclientCod(), user.getUserwebCod(), user.getAdminNum());

						/*
						 * recorremos la lista de p�ginas que el usuario puede
						 * visualizar
						 */
						for (Screenclient screenclient : screenClientList) {
							/*
							 * preguntamos si es que en nuestra lista ya no se
							 * encuentra el servicio y si el servicio de la
							 * p�gina no es nulo
							 */

							if (!serviceList.contains(screenclient.getService()) && screenclient.getService() != null) {

								/*
								 * recorremos la lista de servicios disponibles
								 * para metas
								 */
								for (Service service : getServicesAvailable()) {
									/*
									 * preguntamos si el nombre del servicio del
									 * screenlist forma parte de la lista de
									 * servicios disponibles para metas
									 */

									if (service.getServiceCod().equals(screenclient.getService().getServiceCod())) {

										serviceList.add(screenclient.getService());
									}
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return serviceList;
	}

	public void setServiceList(List<Service> serviceList) {
		this.serviceList = serviceList;
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

		if (((selectedUserphoneList == null || selectedUserphoneList.size() == 0) && !validatedAllUsers) && ((selectedClassificationList == null || selectedClassificationList.size() == 0) && !validatedAllClassification)) {
			parametersValidated = false;
			setWarnMessage(i18n.iValue("web.client.backingBean.report.message.InvalidUserphoneClassification"));
			return null;
		}

		if (selectedService == null) {
			parametersValidated = false;
			setWarnMessage(i18n.iValue("web.client.crudadmingoal.report.message.ServiceCanNotBeNull"));
			return null;
		}

		if (selectedPeriod == null) {
			parametersValidated = false;
			setWarnMessage(i18n.iValue("web.client.crudadmingoal.report.message.GoalPeriodCanNotBeNull"));
			return null;
		}

		if (selectedRangeList == null) {
			parametersValidated = false;
			setWarnMessage(i18n.iValue("web.client.crudadmingoal.report.message.DateRangeCanNotBeNull"));
			return null;
		}

		parametersValidated = true;

		return null;
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
			params.put("ORDER_BY", getOrderByReport());
			params.put("PARAMETERS_DESCRIPTION", getParameterDescriptionReport());
			params.put("USER", SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
			params.put("SUBREPORT_DIR", getReportsPath().concat("/"));
			params.put("COD_META", selectedPeriod.getClientGoalCod());
			params.put("SERVICE_NAME", selectedService.getDescriptionChr());
			params.put("GOAL_NUM", selectedPeriod.getGoal());

			params.put("USERPHONE_LIST", getUserphoneWhere());

			setDateRange();
			params.put("FECHA_INI", fechaIni);
			params.put("FECHA_FIN", fechaFin);

			// params.put("SQL_RANGO_FECHAS", getDateRangeSql());

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

	private void setDateRange() {
		DateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateFormat());
		String[] dates = selectedRangeList.split("-");
		try {
			fechaIni = sdf.parse(dates[0]);
			fechaFin = sdf.parse(dates[1]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getWhereReport() {
		Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
		String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
		String sqlWhere = "";
		sqlWhere = String.format(sqlWhere.concat(" AND u.COD_CLIENT = %s"), clientCod.toString());
		sqlWhere += MessageFormat.format(" and EXISTS " + "(select * from USERPHONE_CLASSIFICATION uc " + "where uc.cod_userphone = u.userphone_cod " + "and uc.cod_classification in ({0})) ", classifications);

		if (selectedService != null) {
			// Servicio de Visitas
			if (selectedService.getServiceCod() == 1) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0} " + " AND (svd.COLUMN1_CHR = ''SAL'' or svd.COLUMN1_CHR = ''ENTSAL'') ", selectedService.getServiceCod());

			}

			// Servicio de Pedidos
			if (selectedService.getServiceCod() == 2) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0} " + " AND (sv.COLUMN9_CHR != ''0'' OR SV.COLUMN9_CHR IS NULL)", selectedService.getServiceCod());

			}

			// Servicio de Visita-Pedido
			if (selectedService.getServiceCod() == 3) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0} " + " AND  svd.COLUMN1_CHR = ''SAL'' ", selectedService.getServiceCod());

			}

			// Si es Servicio de Entregas,Inventario o Courrier
			if (selectedService.getServiceCod() == 7 || selectedService.getServiceCod() == 10 || selectedService.getServiceCod() == 18) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0} ", selectedService.getServiceCod());

			}
			
			// Si es Servicio de Cobranzas
            if (selectedService.getServiceCod() == 5) {
                sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0} AND (SV.COLUMN5_CHR = ''0'' OR SV.COLUMN5_CHR IS NULL) ", selectedService.getServiceCod());

            }

			// Servicio de Guardias
			if (selectedService.getServiceCod() == 6 || selectedService.getServiceCod() == 15) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0}" + " AND svd.COLUMN2_CHR is null ", selectedService.getServiceCod());
			}

			// Servicio de Visitadores Medicos
			if (selectedService.getServiceCod() == 17) {
				sqlWhere += MessageFormat.format(" AND sv.COD_SERVICE = {0} AND cg.COD_SERVICE = {0}" + " AND (svd.column1_chr = ''SAL'' or svd.column1_chr = ''ENTSAL'') AND sv.column3_chr = ''ME''", selectedService.getServiceCod());
			}

		}

		return sqlWhere;
	}

	private String getUserphoneWhere() {
		String sql = "";
		if (selectedUserphoneList != null && selectedUserphoneList.size() > 0) {
			String valor = "";
			for (Userphone u : selectedUserphoneList) {
				valor += u.getUserphoneCod().toString() + ",";
			}
			 if(valor.length() > 0) {
             	valor = valor.substring(0, valor.length()-1);
             	sql = " AND u.USERPHONE_COD in (";
             	sql = sql.concat(valor).concat(") ");
             }
		}

		if (selectedClassificationList != null && selectedClassificationList.size() > 0) {
			for (Classification c : selectedClassificationList) {
				Classification managedClassification = classificationFacade.find(c.getClassificationCod(), "userphoneList");
				String valor = "";
				for (Userphone u : managedClassification.getUserphoneList()) {
					valor += u.getUserphoneCod().toString() + ",";
				}
				if(valor.length() > 0) {
	                valor = valor.substring(0, valor.length()-1);
	                sql = sql.concat(" AND U.USERPHONE_COD IN (" + valor + ")");
	             }
			}

		}
		return sql;
	}

	@Override
	public String getOrderByReport() {
		return null;
	}

	@Override
	public List<Userphone> getUserphoneList() {
		if (selectedPeriod != null) {
			userphoneList = clientgoalFacade.getUserphoneListByClientGoal(selectedPeriod.getClientGoalCod());
		} else
			userphoneList = new ArrayList<Userphone>();
		return userphoneList;
	}

	@Override
	public void setUserphoneList(List<Userphone> userphoneList) {
		this.userphoneList = userphoneList;
	}

	public Boolean getAllRanges() {
		return allRanges;
	}

	public void setAllRanges(Boolean allRanges) {
		this.allRanges = allRanges;
	}

	public List<String> getRangeList() {
		if (selectedPeriod != null) {
			rangeList = clientgoalFacade.getDateRanges(selectedPeriod.getDayFrom().toString(), selectedPeriod.getDayTo().toString(), selectedPeriod.getValidityDateFrom(), selectedPeriod.getValidityDateTo() != null ? selectedPeriod.getValidityDateTo() : new Date());
			System.out.println();
		}
		return rangeList;
	}

	public void setRangeList(List<String> rangeList) {
		this.rangeList = rangeList;
	}

	public void selectAllRanges() {
		if (allRanges)
			selectedRangeList = null;
	}

	public String getSelectedRangeList() {
		return selectedRangeList;
	}

	public void setSelectedRangeList(String selectedRangeList) {
		this.selectedRangeList = selectedRangeList;
	}

}
