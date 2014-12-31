package com.tigo.cs.view.service;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "informconfBean")
@ViewScoped
public class InformconfBean extends AbstractServiceBean {

    private static final long serialVersionUID = 2734872159421727465L;
    public static final int COD_SERVICE = 13;

    public InformconfBean() {
        setCodService(COD_SERVICE);
        setShowMapOnHeader(true);
        getSortHelper().setField("servicevalueCod");
        setPdfReportCabDetailName("rep_informconf_full");
        setXlsReportCabDetailName("rep_informconf_full_xls");
        getMapModel();
    }

    @Override
    public String getDetailWhereCriteria() {
        return "";
    }

    @Override
    public String getDetailReportWhereCriteria() {
        return "";
    }

}
