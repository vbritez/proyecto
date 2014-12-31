package com.tigo.cs.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.chart.ChartModel;

import com.tigo.cs.commons.web.view.SMBaseBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: RequestParametersBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "requestParametersBean")
@RequestScoped
public class RequestParametersBean extends SMBaseBean {

    private static final long serialVersionUID = 1925883140177871625L;
    private ChartModel chartModel;
    private String chartType;
    private Integer maxValue;
    private String chartReportOption;
    private String reportTitle;

    public RequestParametersBean() {
    }

    public static RequestParametersBean getInstance() {
        return (RequestParametersBean) getBean("requestParametersBean");
    }

	public ChartModel getChartModel() {
		return chartModel;
	}

	public void setChartModel(ChartModel chartModel) {
		this.chartModel = chartModel;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public String getChartReportOption() {
		return chartReportOption;
	}

	public void setChartReportOption(String chartReportOption) {
		this.chartReportOption = chartReportOption;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
}
