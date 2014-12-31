/**
 * 
 */
package com.tigo.cs.view.service;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.domain.view.AbstractEntity;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.ClientParameterValueFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;

/**
 * Abstract class para la construcci�n de backing beans con el Primefaces como
 * front end.
 * 
 * @author Miguel Zorrilla
 * @since CS Fase 7
 */

public abstract class AbstractBackingBean<T extends AbstractEntity, F extends AbstractAPI<T>> extends SMBaseBean {

	// Statics AND/OR final fields
	private static final long serialVersionUID = 7797834109988351786L;
	private final Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
	private final Client client = userweb.getClient();

	protected LazyDataModel<T> tableModel;
	private Long codService = 0L;
	protected String whereCriteria = "";
	private String pdfReportName;
	private String xlsReportName;

	// __________________________________________________________________________________________
	// Managed Beans
	@EJB
	private ClassificationFacade classificationFacade;
	@EJB
	protected Notifier notifier;
	@EJB
    protected ClientFileFacade fileFacade;
	@EJB
    private ClientParameterValueFacade clientParameterValueFacade;
	@EJB
	private UserphoneFacade userphoneFacade;
	@EJB
    protected I18nBean i18n;

	// __________________________________________________________________________________________
	// class members
	@SuppressWarnings("unused")
	private F facade;

	/**
	 * Default constructor for AbstractBackingBean inherited classes.
	 */
	public AbstractBackingBean() {
	}

	@PostConstruct
	public void init() {
		tableModel = new LazyEntityDataModel();
	}

	// __________________________________________________________________________________________
	// GETTERS
	public LazyDataModel<T> getTableModel() {
		return tableModel;
	}

	public Client getClient() {
		return client;
	}

	public Userweb getUserweb() {
		return userweb;
	}

//	public Long getServiceCod() throws ServiceIdNotFoundException {
//		ServiceIdentifier service = this.getClass().getAnnotation(ServiceIdentifier.class);
//		if (service != null) {
//			serviceCod = service.id().getValue();
//		} else {
//			throw new ServiceIdNotFoundException(this.getClass());
//		}
//		return serviceCod;
//	}	

	public String getPdfReportName() {
        return pdfReportName;
    }

    public void setPdfReportName(String pdfReportName) {
        this.pdfReportName = pdfReportName;
    }    

    public String getXlsReportName() {
        return xlsReportName;
    }

    public void setXlsReportName(String xlsReportName) {
        this.xlsReportName = xlsReportName;
    }
    
    public Long getCodService() {
        return codService;
    }

    public void setCodService(Long codService) {
        this.codService = codService;
    }

    // __________________________________________________________________________________________
	// IMPLEMENTACI�N DE M�TODOS ABSTRACTOS
	/**
	 * Este m�todo encuentra el criterio de filtros para la cl�usula sql WHERE.
	 * Es requerido para la carga de datos en el tablemodel.
	 * */
	public abstract String getCabWhereCriteria(Map<String, String> filters);

	/**
	 * Este m�todo obtiene la instancia EJB asociado al AbstractEntity dado.
	 * 
	 * */
	public abstract F getFacade();
	
	public abstract String getDataModelQuery();
	

	// __________________________________________________________________________________________
	// INNER CLASS - DATA MODEL

	/**
	 * Generic TableDataModel para el datamodel del Prime Maneja entidades que
	 * extienden de AbstractEntity, las cuales fueron creadas con el prop�sito
	 * especial de los JPA Entities mapeados a vistas de la base de datos.
	 * */
	class LazyEntityDataModel extends LazyDataModel<T> {
		private static final long serialVersionUID = 8013643597308553534L;

		private List<T> datasource;
		private Integer count = null;

		LazyEntityDataModel() {
		}
		
		
		@Override
		public T getRowData(String rowKey) {
			for (T e : datasource) {
				if (e.getPrimaryKey().toString().equals(rowKey))
					return e;
			}

			return null;
		}

		@Override
		public Object getRowKey(T e) {
			return e.getPrimaryKey();
		}

		@Override
		public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			List<T> data = new ArrayList<T>();
			try {
			    List<Classification> classifications = null;
			    
				String sql = getDataModelQuery();
				if (sql.contains("classifications")){
                    classifications = classificationFacade.findByUserweb(getUserweb());
                }
				sql = MessageFormat.format(sql, "DISTINCT o", getClient().getClientCod().toString(),"16");
				sql = sql.concat(getCabWhereCriteria(filters));

				String orderBy = "";
				if (!orderBy.equals("")) {
					orderBy = "o.".concat(sortOrder.equals(SortOrder.ASCENDING) ? " ASC" : " DESC");
				}
				//datasource = getFacade().findRange(new int[] { first, first + pageSize }, where, orderBy, null);
				datasource = getFacade().findRange(null, sql, orderBy, classifications);	

				
				//filter  
//		        for(T ds : datasource) {  
//		            boolean match = true;  
//		  
//		            for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {  
//		                try {  
//		                    String filterProperty = it.next();  
//		                    String filterValue = filters.get(filterProperty);  
//		                    String fieldValue = String.valueOf(ds.getClass().getField(filterProperty).get(ds));  
//		  
//		                    if(filterValue == null || fieldValue.startsWith(filterValue)) {  
//		                        match = true;  
//		                    }  
//		                    else {  
//		                        match = false;  
//		                        break;  
//		                    }  
//		                } catch(Exception e) {  
//		                    match = false;  
//		                }   
//		            }  
//		  
//		            if(match) {  
//		                data.add(ds);  
//		            }  
//		        }  
				
				data = datasource;				
				
		        //sort  
		        if(sortField != null) {  
		            try{
		                Collections.sort(data, new EntitySorter(sortField, sortOrder));
		            }catch (Exception e) {
                        //e.printStackTrace();
                    }
		        }  
		  
		        //rowCount  
		        int dataSize = data.size();  
		        this.setRowCount(dataSize);  
		  
		        //paginate  
		        if(dataSize > pageSize) {  
		            try {  
		                return data.subList(first, first + pageSize);  
		            }  
		            catch(IndexOutOfBoundsException e) {  
		                return data.subList(first, first + (dataSize % pageSize));  
		            }  
		        }  
		        else {  
		            return data;  
		        }  
				
				// sort
//				if (sortField != null) {
//					Collections.sort(datasource, new EntitySorter(sortField, sortOrder));
//				}
			} catch (Exception e) {
				notifier.signal(this.getClass(), Action.ERROR, e.getMessage());
				return new ArrayList<T>();
			}
//			return datasource;
		}

		@Override
		public int getRowCount() {
			if (count == null) {
				try {
				    List<Classification> classifications = null;
				    
				    String sql = getDataModelQuery();
				    if (sql.contains("classifications")){
				        classifications = classificationFacade.findByUserweb(getUserweb());
				    }
				    sql = MessageFormat.format(sql, "COUNT(distinct o)", getClient().getClientCod().toString(), "16");
				    sql = sql.concat(getCabWhereCriteria(null));
					count = getFacade().count(sql, classifications);
				} catch (Exception e) {
					notifier.signal(this.getClass(), Action.ERROR, e.getMessage());
					count = 0;
				}
			}
			return count;
		}
		
		@Override
		public void setRowIndex(final int rowIndex) {
			if (rowIndex == -1 || getPageSize() == 0) {
				super.setRowIndex(-1);
			} else {
				super.setRowIndex(rowIndex % getPageSize());
			}
		}	
		
		private String getPartner(Userphone userphone) throws Exception {
	        String enabled = clientParameterValueFacade.findByCode(
	                userphone.getClient().getClientCod(), "ot.integration.enabled");

	        if (enabled != null && enabled.compareTo("1") == 0) {

//	            List<Classification> classifications = userphoneFacade.findClassification(userphone);
	            List<Classification> classifications = userphoneFacade.findClassificationUserweb(userphone);

	            if (classifications == null || (classifications != null && classifications.size() == 0)) {
	                throw new Exception("El usuario no tiene ningun partner asignado.");
	            }

	            if (classifications != null && classifications.size() > 1) {
	                throw new Exception("El usuario tiene mas de un partner asignado.");
	            }

	            Classification classification = classifications.get(0);
	            return classification.getDescriptionChr();
	            
	        }
	        return null;
	    }

	}
	
	
	//-----------------------------------------------------------------------------
	
	public String viewPDFReport() {
        return generateReport(getPdfReportName(), ReportType.PDF);
    }
	
	public String viewXLSReport() {
        return generateReport(getXlsReportName(), ReportType.XLS);
    }
	
	public String generateReport(String reportName, ReportType reportType) {

        /*
         * obtenemos la informacion sobre el usuario que solicita el reporte, y
         * el cliente al que pertenece
         */
        Userweb userweb = SecurityBean.getInstance().getLoggedInUserClient();
        Client client = userweb.getClient();
        String userInformation = MessageFormat.format("{0} ({1})", userweb.getNameChr(), client.getNameChr());

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getDetailReportWhere());
        params.put("ORDER_BY", getDetailReportOrderBy());
        params.put("USER", userInformation);
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO", JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn, reportType);
        return null;
    }
	
	public abstract String getDetailReportWhere();
	
	public String getDetailReportOrderBy() {        
        return "ORDER BY o.CREATED_DATE ASC" ;
    }

}
