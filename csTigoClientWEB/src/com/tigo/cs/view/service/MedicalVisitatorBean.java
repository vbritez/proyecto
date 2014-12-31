package com.tigo.cs.view.service;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;

import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Miguel Maciel
 * @version $Id: MedicalVisitatorBean.java 1634 2012-07-26 15:09:25Z
 *          miguel.maciel $
 */
@ManagedBean(name = "medicalVisitatorBean")
@ViewScoped
public class MedicalVisitatorBean extends AbstractServiceBean {

	/**
     * 
     */
	private static final long serialVersionUID = -8320333554281402974L;
	public static final int COD_SERVICE = 17;
	private ServiceValue currentServiceVale;
	private ServiceValue previousServiceVale;
	private ServiceValue productServiceValue;
	private DataModel<ServiceValue> currentDataModel;
	private DataModel<ServiceValueDetail> currentDetailDataModel;
	private DataModel<ServiceValueDetail> productDataModel;
	protected PaginationHelper currentPaginationHelper;
	protected PaginationHelper currentDetailPaginationHelper;
	protected PaginationHelper productPaginationHelper;
	protected SortHelper currentSortHelper;
	protected SortHelper currentDetailSortHelper;
	protected SortHelper productSortHelper;
	protected Map<Object, Boolean> currentDataModelItems;
	protected Map<Object, Boolean> currentDetailDataModelItems;
	private Map<String, String> mapClinic = new HashMap<String, String>();
	private Map<String, String> mapDuration = new HashMap<String, String>();
	private Map<String, String> mapMedic = new HashMap<String, String>();
	private Map<String, String> mapMotive = new HashMap<String, String>();
	private Map<String, String> mapProduct = new HashMap<String, String>();
	private final Map<String, String> mapEncodingEvents = new HashMap<String, String>();
	private String durationFormat;

	MetaClient metaClientClinic = null;
	MetaClient metaClientMedic = null;
	MetaClient metaClientProduct = null;
	MetaClient metaClientMotive = null;

	public MedicalVisitatorBean() {
		setCodService(COD_SERVICE);
		setShowMapOnDetail(true);
	}

	@PostConstruct
	public void init() {
		mapEncodingEvents.put("ENTSAL",
				i18n.iValue("web.client.backingBean.visit.message.QuickVisit"));
		mapEncodingEvents.put("ENT",
				i18n.iValue("web.client.backingBean.visit.message.Entrance"));
		mapEncodingEvents.put("SAL",
				i18n.iValue("web.client.backingBean.visit.message.Exit"));
	}

	/*
	 * sobreescribimos para recuperar los registros del servicio
	 */
	@Override
	public PaginationHelper getPaginationHelper() {
		if (paginationHelper == null) {
			int pageSize = getRowQuantSelected().length() > 0 ? Integer
					.valueOf(getRowQuantSelected()).intValue() : 0;

			paginationHelper = new PaginationHelper(pageSize) {
				Integer count = null;

				@Override
				public int getItemsCount() {
					if (count == null) {
						Long clientCod = SecurityBean.getInstance()
								.getLoggedInUserClient().getClient()
								.getClientCod();
						// List<Classification> classifications =
						// classificationFacade.findByUserweb(getUserweb());

						List<Classification> classifications = classificationFacade
								.findByUserwebWithChilds(getUserweb());
						String where = " WHERE o.enabledChr = true AND o.userphone.client.clientCod = {0} AND o.column1Chr = {2} AND o.service.serviceCod = {1}  AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ";
						where = MessageFormat.format(where,
								clientCod.toString(),
								String.valueOf(getCodService()), "'MV'");
						where = where.concat(getCabDetailWhereCriteria());
						count = facade.count(where, classifications);
					}

					return count.intValue();
				}

				@Override
				public DataModel createPageDataModel() {
					Long clientCod = SecurityBean.getInstance()
							.getLoggedInUserClient().getClient().getClientCod();
					// List<Classification> classifications =
					// classificationFacade.findByUserweb(getUserweb());

					List<Classification> classifications = classificationFacade
							.findByUserwebWithChilds(getUserweb());
					String where = " WHERE o.enabledChr = true AND o.userphone.client.clientCod = {0} AND o.column1Chr = {2} AND o.service.serviceCod = {1}  AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphone.userphoneCod AND u.client = o.userphone.client AND cl.codClient = o.userphone.client AND cl in (:classifications)) ";
					where = MessageFormat.format(where, clientCod.toString(),
							String.valueOf(getCodService()), "'MV'");
					where = where.concat(getCabDetailWhereCriteria());

					String orderby = "o.".concat(sortHelper.getField()).concat(
							sortHelper.isAscending() ? " ASC" : " DESC");
					return new ListDataModelViewCsTigo(facade.findRange(
							new int[] { getPageFirstItem(),
									getPageFirstItem() + getPageSize() },
							where, orderby, classifications));
				}
			};
		}

		return paginationHelper;
	}

	public PaginationHelper getCurrentPaginationHelper() {
		if (currentPaginationHelper == null) {
			int pageSize = getRowQuantSelected().length() > 0 ? Integer
					.valueOf(getRowQuantSelected()).intValue() : 0;

			currentPaginationHelper = new PaginationHelper(pageSize) {
				Integer count = null;

				@Override
				public int getItemsCount() {
					if (count == null) {
						String where = " WHERE o.enabledChr = true AND o.column1Chr = {0}";
						where = MessageFormat.format(where, "'"
								+ getServiceValue().getServicevalueCod()
										.toString())
								+ "'";
						count = facade.count(where);
					}

					return count.intValue();
				}

				@Override
				public DataModel createPageDataModel() {
					String where = " WHERE o.enabledChr = true AND o.column1Chr = {0}";
					where = MessageFormat
							.format(where, "'"
									+ getServiceValue().getServicevalueCod()
											.toString())
							+ "'";

					String orderby = "o.".concat(currentSortHelper.getField())
							.concat(currentSortHelper.isAscending() ? " ASC"
									: " DESC");
					return new ListDataModelViewCsTigo(facade.findRange(
							new int[] { getPageFirstItem(),
									getPageFirstItem() + getPageSize() },
							where, orderby, null));
				}
			};
		}

		return currentPaginationHelper;
	}

	public PaginationHelper getCurrentDetailPaginationHelper() {
		if (currentDetailPaginationHelper == null) {
			int pageSize = getRowQuantSelected().length() > 0 ? Integer
					.valueOf(getRowQuantSelected()).intValue() : 0;

			currentDetailPaginationHelper = new PaginationHelper(pageSize) {

				Integer count = null;

				@Override
				public int getItemsCount() {
					if (count == null) {
						count = facadeDetail
								.count("  WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = "
										.concat(currentServiceVale
												.getServicevalueCod()
												.toString()).concat(
												getDetailWhereCriteria()));
					}
					return count;
				}

				@Override
				public DataModel createPageDataModel() {
					String orderby = "o.".concat(
							currentDetailSortHelper.getField()).concat(
							currentDetailSortHelper.isAscending() ? " ASC"
									: " DESC");
					return new ListDataModelViewCsTigo(
							facadeDetail
									.findRange(
											new int[] {
													getPageFirstItem(),
													getPageFirstItem()
															+ getPageSize() },
											" WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = "
													.concat(currentServiceVale
															.getServicevalueCod()
															.toString())
													.concat(getDetailWhereCriteria()),
											orderby));
				}
			};
		}

		return currentDetailPaginationHelper;
	}

	public PaginationHelper getProductPaginationHelper() {
		if (productPaginationHelper == null) {
			int pageSize = getRowQuantSelected().length() > 0 ? Integer
					.valueOf(getRowQuantSelected()).intValue() : 0;

			productPaginationHelper = new PaginationHelper(pageSize) {

				Integer count = null;

				@Override
				public int getItemsCount() {
					if (count == null) {
						count = facadeDetail
								.count(" WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true AND o.serviceValue.servicevalueCod = "
										.concat(productServiceValue
												.getServicevalueCod()
												.toString()).concat(
												getDetailWhereCriteria()));
					}
					return count;
				}

				@Override
				public DataModel createPageDataModel() {
					String orderby = "o.".concat(productSortHelper.getField())
							.concat(productSortHelper.isAscending() ? " ASC"
									: " DESC");
					return new ListDataModelViewCsTigo(
							facadeDetail
									.findRange(
											new int[] {
													getPageFirstItem(),
													getPageFirstItem()
															+ getPageSize() },
											" WHERE o.enabledChr = true AND o.serviceValue.enabledChr = true  AND  o.serviceValue.servicevalueCod = "
													.concat(productServiceValue
															.getServicevalueCod()
															.toString())
													.concat(getDetailWhereCriteria()),
											orderby));
				}
			};
		}

		return productPaginationHelper;
	}

	public String viewClinicDetails() {
		setServiceValue(null);
		Iterator<ServiceValue> iterator = getDataModelHeader().iterator();
		while (iterator.hasNext()) {
			ServiceValue currServiceValue = iterator.next();
			if (getSelectedItems().get(currServiceValue.getServicevalueCod())) {
				if (getServiceValue() == null) {
					setServiceValue(facade.find(currServiceValue
							.getServicevalueCod()));
				} else {
					setServiceValue(null);
					setWarnMessage(i18n
							.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
					return null;
				}
			}
		}
		if (getServiceValue() == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
			return null;
		}

		// Initialize sort with default values
		currentSortHelper = new SortHelper();
		try {
			currentSortHelper.setField(getPrimarySortedField());
			currentSortHelper.setAscending(primarySortedFieldAsc);
		} catch (PrimarySortedFieldNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					AbstractServiceBean.class.getName()).log(
					Level.SEVERE,
					i18n.iValue("web.client.backingBean.message.Error")
							+ ex.getMessage(), ex);
		}

		currentPaginationHelper = null;
		currentDataModel = getCurrentPaginationHelper().createPageDataModel();
		if (geolocalizationAllowed && showMapOnHeader) {
			showHeaderMap();
			addClientMarker();
		}
		if (geolocalizationAllowed && showMapOnDetail) {
			getMapModel();
			addClientMarker();
		}
		/*
		 * se mapean los metadatos del modelo
		 */
		currentMetaDataFromModel();

		return null;
	}

	public String viewClinicVisitDetails() {
		setCurrentServiceVale(null);
		Iterator<ServiceValue> iterator = getCurrentDataModel().iterator();
		while (iterator.hasNext()) {
			ServiceValue currServiceValue = iterator.next();
			if (getCurrentDataModelItems().get(
					currServiceValue.getServicevalueCod())) {
				if (getCurrentServiceVale() == null) {
					setCurrentServiceVale(facade.find(currServiceValue
							.getServicevalueCod()));
				} else {
					setCurrentServiceVale(null);
					setWarnMessage(i18n
							.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
					return null;
				}
			}
		}
		if (getCurrentServiceVale() == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
			return null;
		}

		// Initialize sort with default values
		currentDetailSortHelper = new SortHelper();
		try {
			currentDetailSortHelper.setField(getPrimarySortedFieldDetail());
			currentDetailSortHelper.setAscending(primarySortedFieldDetailAsc);
		} catch (PrimarySortedFieldNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					AbstractServiceBean.class.getName()).log(
					Level.SEVERE,
					i18n.iValue("web.client.backingBean.message.Error")
							+ ex.getMessage(), ex);
		}

		currentDetailPaginationHelper = null;
		currentDetailDataModel = getCurrentDetailPaginationHelper()
				.createPageDataModel();
		if (geolocalizationAllowed && showMapOnHeader) {
			showHeaderMap();
			addClientMarker();
		}
		if (geolocalizationAllowed && showMapOnDetail) {
			getMapModel();
			addClientMarker();
		}
		/*
		 * se mapean los metadatos del modelo
		 */
		currentDetailMetaDataFromModel();
		showCurrentDetailMap();

		return null;
	}

	public String backClinicDetails() {
		setServiceValue(getPreviousServiceVale());
		setPreviousServiceVale(null);

		// Initialize sort with default values
		currentSortHelper = new SortHelper();
		try {
			currentSortHelper.setField(getPrimarySortedField());
			currentSortHelper.setAscending(primarySortedFieldAsc);
		} catch (PrimarySortedFieldNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					AbstractServiceBean.class.getName()).log(
					Level.SEVERE,
					i18n.iValue("web.client.backingBean.message.Error")
							+ ex.getMessage(), ex);
		}

		currentPaginationHelper = null;
		currentDataModel = getCurrentPaginationHelper().createPageDataModel();
		if (geolocalizationAllowed && showMapOnHeader) {
			showHeaderMap();
			addClientMarker();
		}
		if (geolocalizationAllowed && showMapOnDetail) {
			getMapModel();
			addClientMarker();
		}
		/*
		 * se mapean los metadatos del modelo
		 */
		currentMetaDataFromModel();

		return null;
	}

	public String viewMedicDetails() {
		setPreviousServiceVale(getServiceValue());
		setServiceValue(null);
		Iterator<ServiceValue> iterator = getCurrentDataModel().iterator();
		while (iterator.hasNext()) {
			ServiceValue currServiceValue = iterator.next();
			if (getCurrentDataModelItems().get(
					currServiceValue.getServicevalueCod())) {
				if (getServiceValue() == null) {
					setServiceValue(facade.find(currServiceValue
							.getServicevalueCod()));
				} else {
					setServiceValue(null);
					setWarnMessage(i18n
							.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
					return null;
				}
			}
		}
		if (getServiceValue() == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
			return null;
		}

		// Initialize sort with default values
		currentSortHelper = new SortHelper();
		try {
			currentSortHelper.setField(getPrimarySortedField());
			currentSortHelper.setAscending(primarySortedFieldAsc);
		} catch (PrimarySortedFieldNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					AbstractServiceBean.class.getName()).log(
					Level.SEVERE,
					i18n.iValue("web.client.backingBean.message.Error")
							+ ex.getMessage(), ex);
		}

		currentPaginationHelper = null;
		currentDataModel = getCurrentPaginationHelper().createPageDataModel();
		if (geolocalizationAllowed && showMapOnHeader) {
			showHeaderMap();
			addClientMarker();
		}
		if (geolocalizationAllowed && showMapOnDetail) {
			getMapModel();
			addClientMarker();
		}
		/*
		 * se mapean los metadatos del modelo
		 */
		currentMetaDataFromModel();
		return null;
	}

	public String viewProductDetails() {

		Iterator<ServiceValue> iterator = getCurrentDataModel().iterator();
		while (iterator.hasNext()) {
			ServiceValue currServiceValue = iterator.next();
			if (getCurrentDataModelItems().get(
					currServiceValue.getServicevalueCod())) {
				if (getProductServiceValue() == null) {

					String where = " WHERE o.enabledChr = true AND o.column1Chr = {0}";
					where = MessageFormat.format(where, "'"
							+ currServiceValue.getServicevalueCod().toString())
							+ "'";
					;

					setProductServiceValue(facade.findByQuery(where));

				} else {
					setProductServiceValue(null);
					setWarnMessage(i18n
							.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectJustOne"));
					return null;
				}
			}
		}
		if (getProductServiceValue() == null) {
			setWarnMessage(i18n
					.iValue("web.client.backingBean.abstractServiceBean.message.MustSelectOne"));
			return null;
		}

		// Initialize sort with default values
		productSortHelper = new SortHelper();
		try {
			productSortHelper.setField(getPrimarySortedFieldDetail());
			productSortHelper.setAscending(primarySortedFieldDetailAsc);
		} catch (PrimarySortedFieldNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					AbstractServiceBean.class.getName()).log(
					Level.SEVERE,
					i18n.iValue("web.client.backingBean.message.Error")
							+ ex.getMessage(), ex);
		}

		productPaginationHelper = null;
		productDataModel = getProductPaginationHelper().createPageDataModel();
		if (geolocalizationAllowed && showMapOnHeader) {
			showHeaderMap();
			addClientMarker();
		}
		if (geolocalizationAllowed && showMapOnDetail) {
			getMapModel();
			addClientMarker();
		}
		/*
		 * se mapean los metadatos del modelo
		 */
		currentDetailMetaDataFromModel();

		return null;
	}

	public String previousCurrentPage() {
		getCurrentPaginationHelper().previousPage();
		currentDataModel = getPaginationHelper().createPageDataModel(); // For
																		// data
																		// model
																		// recreation
		currentDataModelItems = null; // For clearing selection
		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentMetaDataFromModel();
		return null;
	}

	public String nextCurrentPage() {
		getCurrentPaginationHelper().nextPage();
		currentDataModel = getPaginationHelper().createPageDataModel(); // For
																		// data
																		// model
																		// recreation
		currentDataModelItems = null; // For clearing selection
		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentMetaDataFromModel();
		return null;
	}

	public String previousCurrentDetailPage() {
		getCurrentDetailPaginationHelper().previousPage();
		currentDetailDataModel = getCurrentDetailPaginationHelper()
				.createPageDataModel(); // For
		// data
		// model
		// recreation
		currentDetailDataModelItems = null; // For clearing selection
		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentDetailMetaDataFromModel();
		return null;
	}

	public String nextCurrentDetailPage() {
		getCurrentDetailPaginationHelper().nextPage();
		currentDetailDataModel = getPaginationHelper().createPageDataModel(); // For
		// data
		// model
		// recreation
		currentDetailDataModelItems = null; // For clearing selection
		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentDetailMetaDataFromModel();
		return null;
	}

	public String previousProductPage() {
		getProductPaginationHelper().previousPage();
		productDataModel = getProductPaginationHelper().createPageDataModel(); // For
		// data
		// model
		// recreation

		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentDetailMetaDataFromModel();
		return null;
	}

	public String nextProductPage() {
		getProductPaginationHelper().nextPage();
		productDataModel = getProductPaginationHelper().createPageDataModel(); // For
		// data
		// model
		// recreation

		/*
		 * se vuelven a mapear los metadatos al cambiar de pagina
		 */
		currentDetailMetaDataFromModel();
		return null;
	}

	public String applySortCurrent() {
		currentDataModelItems = null; // For clearing selection
		currentDataModel = getCurrentPaginationHelper().createPageDataModel();
		currentMetaDataFromModel();
		return null;
	}

	public String applySortCurrentDetail() {
		currentDetailDataModelItems = null; // For clearing selection
		currentDetailDataModel = getCurrentDetailPaginationHelper()
				.createPageDataModel();
		currentDetailMetaDataFromModel();
		return null;
	}

	public String applySortproduct() {

		productDataModel = getProductPaginationHelper().createPageDataModel();
		currentDetailMetaDataFromModel();
		return null;
	}

	public String cancelCurrent() {

		currentDataModelItems = null;
		currentDataModel = null;
		setServiceValue(null);
		return null;
	}

	public String cancelMedicCurrent() {

		currentDataModelItems = null;
		currentDataModel = null;
		setServiceValue(null);
		viewClinicDetails();
		return null;
	}

	public String cancelCurrentDetail() {
		if (showMapOnDetail) {
			mapModel = null;
			mapCenter = null;
			mapZoom = null;
			mapType = null;
		}
		currentDetailDataModelItems = null;
		currentDetailDataModel = null;
		currentServiceVale = null;
		return null;
	}

	public String cancelProductDetail() {
		if (showMapOnDetail) {
			mapModel = null;
			mapCenter = null;
			mapZoom = null;
			mapType = null;
		}

		productDataModel = null;
		productServiceValue = null;
		return null;
	}

	public DataModel<ServiceValue> getCurrentDataModel() {
		return currentDataModel;
	}

	public void setCurrentDataModel(DataModel<ServiceValue> currentDataModel) {
		this.currentDataModel = currentDataModel;
	}

	public DataModel<ServiceValueDetail> getProductDataModel() {
		return productDataModel;
	}

	public void setProductDataModel(
			DataModel<ServiceValueDetail> productDataModel) {
		this.productDataModel = productDataModel;
	}

	public SortHelper getCurrentSortHelper() {
		return currentSortHelper;
	}

	public void setCurrentSortHelper(SortHelper currentSortHelper) {
		this.currentSortHelper = currentSortHelper;
	}

	public SortHelper getCurrentDetailSortHelper() {
		return currentDetailSortHelper;
	}

	public void setCurrentDetailSortHelper(SortHelper currentDetailSortHelper) {
		this.currentDetailSortHelper = currentDetailSortHelper;
	}

	public SortHelper getProductSortHelper() {
		return productSortHelper;
	}

	public void setProductSortHelper(SortHelper productSortHelper) {
		this.productSortHelper = productSortHelper;
	}

	public void setCurrentPaginationHelper(
			PaginationHelper currentPaginationHelper) {
		this.currentPaginationHelper = currentPaginationHelper;
	}

	public Map<Object, Boolean> getCurrentDataModelItems() {
		if (currentDataModelItems == null) {
			currentDataModelItems = new HashMap<Object, Boolean>();
		}
		return currentDataModelItems;
	}

	public void setCurrentDataModelItems(
			Map<Object, Boolean> currentDataModelItems) {
		this.currentDataModelItems = currentDataModelItems;
	}

	public Map<Object, Boolean> getCurrentDetailDataModelItems() {
		if (currentDetailDataModelItems == null) {
			currentDetailDataModelItems = new HashMap<Object, Boolean>();
		}
		return currentDetailDataModelItems;
	}

	public void setCurrentDetailDataModelItems(
			Map<Object, Boolean> currentDetailDataModelItems) {
		this.currentDetailDataModelItems = currentDetailDataModelItems;
	}

	public DataModel<ServiceValueDetail> getCurrentDetailDataModel() {
		return currentDetailDataModel;
	}

	public void setCurrentDetailDataModel(
			DataModel<ServiceValueDetail> currentDetailDataModel) {
		this.currentDetailDataModel = currentDetailDataModel;
	}

	@Override
	public String getDetailWhereCriteria() {
		return "";
	}

	@Override
	public String getDetailReportWhereCriteria() {
		return "";
	}

	private boolean validateIntegrationMethodForClinic(Long codClient) {

		if (metaClientClinic == null) {
			try {
				metaClientClinic = metaClientFacade.findByMetaAndClient(
						codClient, 14L);
			} catch (Exception e) {
				return false;
			}
		}
		return metaClientClinic.getEnabledChr();

	}

	private boolean validateIntegrationMethodForMedic(Long codClient) {

		if (metaClientMedic == null) {
			try {
				metaClientMedic = metaClientFacade.findByMetaAndClient(
						codClient, 15L);
			} catch (Exception e) {
				return false;
			}
		}
		return metaClientMedic.getEnabledChr();

	}

	private boolean validateIntegrationMethodForProduct(Long codClient) {

		if (metaClientProduct == null) {
			try {
				metaClientProduct = metaClientFacade.findByMetaAndClient(
						codClient, 2L);
				return metaClientProduct.getEnabledChr();
			} catch (Exception e) {
				return false;
			}
		}else
			return metaClientProduct.getEnabledChr();
	}

	private boolean validateIntegrationMethodForMotive(Long codClient) {

		if (metaClientMotive == null) {
			try {
				metaClientMotive = metaClientFacade.findByMetaAndClient(
						codClient, 3L);
			} catch (Exception e) {
				return false;
			}
		}
		return metaClientMotive.getEnabledChr();

	}

	public void currentMetaDataFromModel() {
		Long codClient = SecurityBean.getInstance().getLoggedInUserClient()
				.getClient().getClientCod();
		mapClinic = new HashMap<String, String>();
		mapMedic = new HashMap<String, String>();
		if (validateIntegrationMethodForClinic(codClient)) {
			if (getCurrentDataModel() != null
					&& getCurrentDataModel().getRowCount() > 0) {
				for (ServiceValue sv : getCurrentDataModel()) {

					String key = sv.getColumn2Chr();
					String value = getMetaValue(codClient, key, 14L);
					mapClinic.put(key, value == null ? null : value.trim()
							.equals("") ? null : value.trim());
				}
			}
		}
		if (validateIntegrationMethodForMedic(codClient)) {
			if (getCurrentDataModel() != null
					&& getCurrentDataModel().getRowCount() > 0) {
				for (ServiceValue sv : getCurrentDataModel()) {

					String key = sv.getColumn2Chr();
					String value = getMetaValue(codClient, key, 15L);
					mapMedic.put(key, value == null ? null : value.trim()
							.equals("") ? null : value.trim());
				}
			}

		}
	}

	public Map<String, String> getMapDuration() {
		if (mapDuration == null) {
			mapDuration = new HashMap<String, String>();
		}
		return mapDuration;
	}

	public void setMapDuration(Map<String, String> mapDuration) {
		this.mapDuration = mapDuration;
	}

	public void currentDetailMetaDataFromModel() {
		Long codClient = SecurityBean.getInstance().getLoggedInUserClient()
				.getClient().getClientCod();
		mapMotive = new HashMap<String, String>();
		mapProduct = new HashMap<String, String>();
		mapDuration = new HashMap<String, String>();

		if (getCurrentServiceVale() != null) {
			if (getPreviousServiceVale() == null) {
				for (ServiceValueDetail svd : getCurrentDetailDataModel()) {
					try {
						mapDuration.put(svd.getColumn3Chr(), DateUtil
								.getPeriodWithFormat(getDurationFormat(), 0L,
										new Long(svd.getColumn3Chr())));
					} catch (Exception e) {
					}
				}
			} else {
				for (ServiceValueDetail svd : getCurrentDetailDataModel()) {
					try {
						mapDuration.put(svd.getColumn7Chr(), DateUtil
								.getPeriodWithFormat(getDurationFormat(), 0L,
										new Long(svd.getColumn7Chr())));
					} catch (Exception e) {
					}
				}
			}
		}
		if (validateIntegrationMethodForMotive(codClient)) {
			if (getCurrentServiceVale() != null) {
				for (ServiceValueDetail svd : getCurrentDetailDataModel()) {

					String key = svd.getColumn2Chr();
					String value = getMetaValue(codClient, key, 3L);
					mapMotive.put(key, value == null ? null : value.trim()
							.equals("") ? null : value.trim());

				}
			}
		}
		if (validateIntegrationMethodForProduct(codClient)) {
			if (getProductServiceValue() != null) {
				for (ServiceValueDetail svd : getProductDataModel()) {

					String key = svd.getColumn1Chr();
					String value = getMetaValue(codClient, key, 2L);
					mapProduct.put(key, value == null ? null : value.trim()
							.equals("") ? null : value.trim());

				}
			}
		}
	}

	private String getMetaValue(Long codClient, String code, Long metaCode) {
		String retValue = null;
		MetaData md;
		try {
			md = metaDataFacade.findByClientMetaMemberAndCode(codClient,
					metaCode, 1L, code);
		} catch (Exception e) {
			return null;
		}
		if (md != null) {
			retValue = md.getValueChr();
		}
		return retValue;
	}

	public Map<String, String> getMapClinic() {
		return mapClinic;
	}

	public void setMapClinic(Map<String, String> mapClinic) {
		this.mapClinic = mapClinic;
	}

	public Map<String, String> getMapMedic() {
		return mapMedic;
	}

	public void setMapMedic(Map<String, String> mapMedic) {
		this.mapMedic = mapMedic;
	}

	public Map<String, String> getMapMotive() {
		return mapMotive;
	}

	public void setMapMotive(Map<String, String> mapMotive) {
		this.mapMotive = mapMotive;
	}

	public Map<String, String> getMapProduct() {
		return mapProduct;
	}

	public void setMapProduct(Map<String, String> mapProduct) {
		this.mapProduct = mapProduct;
	}

	public ServiceValue getCurrentServiceVale() {
		return currentServiceVale;
	}

	public void setCurrentServiceVale(ServiceValue currentServiceVale) {
		this.currentServiceVale = currentServiceVale;
	}

	public Map<String, String> getMapEncodingEvents() {
		return mapEncodingEvents;
	}

	public ServiceValue getPreviousServiceVale() {
		return previousServiceVale;
	}

	public void setPreviousServiceVale(ServiceValue previousServiceVale) {
		this.previousServiceVale = previousServiceVale;
	}

	public void setCurrentDetailPaginationHelper(
			PaginationHelper currentDetailPaginationHelper) {
		this.currentDetailPaginationHelper = currentDetailPaginationHelper;
	}

	public ServiceValue getProductServiceValue() {
		return productServiceValue;
	}

	public void setProductServiceValue(ServiceValue productServiceVale) {
		this.productServiceValue = productServiceVale;
	}

	public String showCurrentDetailMap() {
		if (getGeolocalizationAllowed()) {
			Long codClient = SecurityBean.getInstance().getLoggedInUserClient()
					.getClient().getClientCod();

			mapModel = new DefaultMapModel();
			boolean oneInsideBounds = false;

			Iterator<ServiceValueDetail> iterator = getCurrentDetailDataModel()
					.iterator();
			int nextMarkerCounter = 1;
			while (iterator.hasNext()) {
				ServiceValueDetail currServiceDetailValue = iterator.next();
				if (getCurrentDetailDataModelItems().containsKey(
						currServiceDetailValue.getServicevaluedetailCod())
						&& getCurrentDetailDataModelItems().get(
								currServiceDetailValue
										.getServicevaluedetailCod())
						&& (currServiceDetailValue.getMessage().getCell() != null || (currServiceDetailValue
								.getMessage().getLatitude() != null && currServiceDetailValue
								.getMessage().getLongitude() != null))) {
					// Obtain latitude, longitude and azimuth from cell
					// Message for marker
					String messDescrip = getMessageDescriptionDetailMap(currServiceDetailValue);
					Polygon polygonArea = null;
					Marker markerArea = null;
					Double latitude = currServiceDetailValue.getMessage()
							.getLatitude();
					Double longitude = currServiceDetailValue.getMessage()
							.getLongitude();

					if (latitude != null && longitude != null) {
						markerArea = getGPSCellAreaMarker(latitude, longitude,
								messDescrip, String.valueOf(nextMarkerCounter));
					} else {
						latitude = currServiceDetailValue.getMessage()
								.getCell().getLatitudeNum().doubleValue();
						longitude = currServiceDetailValue.getMessage()
								.getCell().getLongitudeNum().doubleValue();
						double azimuth = currServiceDetailValue.getMessage()
								.getCell().getAzimuthNum().doubleValue();
						String siteCell = currServiceDetailValue.getMessage()
								.getCell().getSiteChr();

						if (!siteCell.toUpperCase().endsWith("O")) {
							// SEGMENTED CELL

							// Cell polygon
							polygonArea = getCellAreaPolygon(latitude,
									longitude, azimuth);

							// Cell marker
							markerArea = getCellAreaMarker(latitude, longitude,
									azimuth, messDescrip,
									String.valueOf(nextMarkerCounter));
						} else {
							// OMNIDIRECTIONAL CELL

							// Cell polygon
							polygonArea = getOmniCellAreaPolygon(latitude,
									longitude);

							// Cell marker
							markerArea = getOmniCellAreaMarker(latitude,
									longitude, messDescrip,
									String.valueOf(nextMarkerCounter));
						}

						// Add the polygon
						if (polygonArea != null) {
							// if the polygon already exists, do not add.
							Polygon existingPolygon = null;
							for (int i = 0; i < getMapModel().getPolygons()
									.size(); i++) {
								if (getMapModel().getPolygons().get(i)
										.getPaths()
										.equals(polygonArea.getPaths())) {
									existingPolygon = getMapModel()
											.getPolygons().get(i);
									break;
								}
							}

							if (existingPolygon == null) {
								getMapModel().addOverlay(polygonArea);
							}
						}

					}// FIN DEL IF

					// Add the marker
					if (markerArea != null) {
						// if the marker already exists, do not add, but instead
						// add data to existing one.
						Marker existingMarker = null;
						for (int i = 0; i < getMapModel().getMarkers().size(); i++) {
							if (getMapModel().getMarkers().get(i).getLatlng()
									.equals(markerArea.getLatlng())) {
								existingMarker = getMapModel().getMarkers()
										.get(i);
								break;
							}
						}

						if (existingMarker == null) {
							getMapModel().addOverlay(markerArea);
							nextMarkerCounter++;
							if (getLastBounds() != null) {
								boolean inside = markerArea.getLatlng()
										.getLat() > getLastBounds()
										.getSouthWest().getLat();
								inside = inside
										&& markerArea.getLatlng().getLat() < getLastBounds()
												.getNorthEast().getLat();
								inside = inside
										&& markerArea.getLatlng().getLng() > getLastBounds()
												.getSouthWest().getLng();
								inside = inside
										&& markerArea.getLatlng().getLng() < getLastBounds()
												.getNorthEast().getLng();

								if (inside) {
									oneInsideBounds = true;
								}
							}
						} else {
							existingMarker.setData(((String) existingMarker
									.getData()).concat("<br>").concat(
									messDescrip));
						}
					}
				}
			}
			// Add polyline
			if (getMapModel().getMarkers().size() > 0) {
				if (!oneInsideBounds) {
					mapCenter = mapModel.getMarkers().get(0).getLatlng();
				}
				if (getMapModel().getMarkers().size() > 1) {
					getMapModel().addOverlay(
							getMarkersPolyline(getMapModel().getMarkers()));
				}
			}
		}
		addClientMarker();
		return null;
	}

	public String generateCurrentReport(String reportName, ReportType reportType) {
		// Prepare orderby clause
		String sortAttributeColumnName = getAttributeColumnName(
				ServiceValue.class, getCurrentSortHelper().getField());
		if (getCurrentSortHelper().getField().indexOf(".") < 0) {
			sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
		} else {
			sortAttributeColumnName = "sv.".concat(sortAttributeColumnName);
		}
		String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(
				getCurrentSortHelper().isAscending() ? " ASC" : " DESC");

		// Put params into params map
		Map<Object, Object> params = new HashMap<Object, Object>();

		params.put("WHERE", " AND sv.enabled_chr = '1' AND sv.column1_chr = '"
				.concat(getServiceValue().getServicevalueCod().toString())
				.concat("'"));
		params.put("ORDER_BY", orderBy);
		params.put(
				"USER",
				SecurityBean
						.getInstance()
						.getLoggedInUserClient()
						.getNameChr()
						.concat(" (")
						.concat(SecurityBean.getInstance()
								.getLoggedInUserClient().getClient()
								.getNameChr()).concat(")"));
		params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
		params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

		ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
		try {
			if (cf != null) {
				params.put("CLIENT_LOGO",
						JRImageLoader.loadImage(cf.getFileByt()));
			}
		} catch (JRException ex) {
			Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		Connection conn = SMBaseBean.getDatabaseConnecction();
		JasperReportUtils.respondReport(reportName, params, true, conn,
				reportType);
		return null;
	}

	public String generateCurrentDetailReport(String reportName,
			ReportType reportType) {
		// Prepare orderby clause
		String sortAttributeColumnName = getAttributeColumnName(
				ServiceValueDetail.class, getCurrentSortHelper().getField());
		if (getCurrentSortHelper().getField().indexOf(".") < 0) {
			sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
		} else {
			sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
		}
		String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(
				getCurrentSortHelper().isAscending() ? " ASC" : " DESC");

		// Put params into params map
		Map<Object, Object> params = new HashMap<Object, Object>();

		params.put(
				"WHERE",
				" AND svd.enabled_chr = '1' AND svd.cod_servicevalue = '"
						.concat(currentServiceVale.getServicevalueCod()
								.toString()).concat("'"));
		params.put("ORDER_BY", orderBy);
		params.put(
				"USER",
				SecurityBean
						.getInstance()
						.getLoggedInUserClient()
						.getNameChr()
						.concat(" (")
						.concat(SecurityBean.getInstance()
								.getLoggedInUserClient().getClient()
								.getNameChr()).concat(")"));
		params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
		params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

		ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
		try {
			if (cf != null) {
				params.put("CLIENT_LOGO",
						JRImageLoader.loadImage(cf.getFileByt()));
			}
		} catch (JRException ex) {
			Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		Connection conn = SMBaseBean.getDatabaseConnecction();
		JasperReportUtils.respondReport(reportName, params, true, conn,
				reportType);
		return null;
	}

	public String getDurationFormat() {
		if (durationFormat == null) {

			durationFormat = clientParameterValueFacade.findByCode(SecurityBean
					.getInstance().getLoggedInUserClient().getClient()
					.getClientCod(), "client.duration.format");

			if (durationFormat == null) {
				durationFormat = i18n
						.iValue("web.client.screen.configuration.field.ExtendedDurationFormatValue");
			}
		}
		return durationFormat;
	}

	public void setDurationFormat(String durationFormat) {
		this.durationFormat = durationFormat;
	}

	public String generateProductDetailReport(String reportName,
			ReportType reportType) {
		// Prepare orderby clause
		String sortAttributeColumnName = getAttributeColumnName(
				ServiceValueDetail.class, getCurrentSortHelper().getField());
		if (getCurrentSortHelper().getField().indexOf(".") < 0) {
			sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
		} else {
			sortAttributeColumnName = "svd.".concat(sortAttributeColumnName);
		}
		String orderBy = "ORDER BY ".concat(sortAttributeColumnName).concat(
				getCurrentSortHelper().isAscending() ? " ASC" : " DESC");

		// Put params into params map
		Map<Object, Object> params = new HashMap<Object, Object>();

		params.put(
				"WHERE",
				" AND svd.enabled_chr = '1'  AND svd.cod_servicevalue = '"
						.concat(productServiceValue.getServicevalueCod()
								.toString()).concat("'"));
		params.put("ORDER_BY", orderBy);
		params.put(
				"USER",
				SecurityBean
						.getInstance()
						.getLoggedInUserClient()
						.getNameChr()
						.concat(" (")
						.concat(SecurityBean.getInstance()
								.getLoggedInUserClient().getClient()
								.getNameChr()).concat(")"));
		params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
		params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
		params.put("DURATION_FORMAT", getDurationFormat());

		ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
		try {
			if (cf != null) {
				params.put("CLIENT_LOGO",
						JRImageLoader.loadImage(cf.getFileByt()));
			}
		} catch (JRException ex) {
			Logger.getLogger(VisitBean.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		Connection conn = SMBaseBean.getDatabaseConnecction();
		JasperReportUtils.respondReport(reportName, params, true, conn,
				reportType);
		return null;
	}

	public String viewPDFmedic() {
		signalReport(ReportType.PDF);
		return generateCurrentReport("rep_visitmedic_medico", ReportType.PDF);
	}

	public String viewPDFclinic() {
		signalReport(ReportType.PDF);
		return generateCurrentReport("rep_visitmedic_sanatorio", ReportType.PDF);
	}

	public String viewXLSclinic() {
		signalReport(ReportType.XLS);
		return generateCurrentReport("rep_visitmedic_sanatorio", ReportType.XLS);
	}

	public String viewXLSmedic() {
		signalReport(ReportType.XLS);
		return generateCurrentReport("rep_visitmedic_medico", ReportType.XLS);
	}

	public String viewPDFmedicDetails() {
		signalReport(ReportType.PDF);
		return generateCurrentDetailReport("rep_visitmedic_medico_detalle",
				ReportType.PDF);
	}

	public String viewXLSmedicDetails() {
		signalReport(ReportType.XLS);
		return generateCurrentDetailReport("rep_visitmedic_medico_detalle",
				ReportType.XLS);
	}

	public String viewPDFclinicDetails() {
		signalReport(ReportType.PDF);
		return generateCurrentDetailReport("rep_visitmedic_sanatorio_detalle",
				ReportType.PDF);
	}

	public String viewXLSclinicDetails() {
		signalReport(ReportType.XLS);
		return generateCurrentDetailReport("rep_visitmedic_sanatorio_detalle",
				ReportType.XLS);
	}

	public String viewPDFproductDetails() {
		signalReport(ReportType.PDF);
		return generateProductDetailReport("rep_visitmedic_producto_detalle",
				ReportType.PDF);
	}

	public String viewXLSproductDetails() {
		signalReport(ReportType.XLS);
		return generateProductDetailReport("rep_visitmedic_producto_detalle",
				ReportType.XLS);
	}

}
