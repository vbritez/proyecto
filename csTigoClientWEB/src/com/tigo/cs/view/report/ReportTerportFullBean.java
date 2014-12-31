package com.tigo.cs.view.report;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Miguel Maciel
 * @version $Id: ReportAttendanceFullBean.java 233 2011-12-26 12:47:58Z
 *          miguel.maciel $
 */
@ManagedBean(name = "reportTerportFullBean")
@ViewScoped
public class ReportTerportFullBean extends AbstractFullReportBean {

    private static final long serialVersionUID = -49322126130802045L;
    @EJB
    private MetaDataFacade metadataFacade;
    @EJB
    private Notifier notifier;
    private List<MetaData> attendantMetadataList;
    private List<MetaData> selectedAttendantsList;
    private Boolean validatedAllAttendants = true;
    private String container;
    private String newUbication;
    private String numChapa;
    private List<MetaData> operatorMetadataList;
    private List<MetaData> selectedOperatorList;
    private Boolean validatedAllOperators = true;
    private List<MetaData> machineMetadataList;
    private List<MetaData> selectedMachineList;
    private Boolean validatedAllMachine = true;

    public ReportTerportFullBean() {
    }

    @Override
    public String getWhereReport() {
        Long clientCod = SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod();
        String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());
        String sqlWhere = "";
        String valor = "";
        sqlWhere = String.format(sqlWhere.concat(" AND U.COD_CLIENT = %s"), clientCod.toString());
        
        if(validatedAllUsers){
            sqlWhere += MessageFormat.format(" and EXISTS " + "(select * from USERPHONE_CLASSIFICATION uc " + "where uc.cod_userphone = u.userphone_cod " + "and uc.cod_classification in ({0})) ", classifications);
        }
        
        if (dateFrom != null && dateTo != null) {
            DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sqlWhere = String.format(sqlWhere.concat(" and sv.ENABLED_CHR = '1' "
                + "and svd.ENABLED_CHR = '1'"
                + " AND SVD.RECORDDATE_DAT BETWEEN TO_TIMESTAMP('%s', 'yyyy-mm-dd hh24:mi:ss') AND TO_TIMESTAMP('%s', 'yyyy-mm-dd hh24:mi:ss')"), sqlSdf.format(dateFrom), sqlSdf.format(dateTo));
        }
                 
        if (selectedUserphoneList != null && selectedUserphoneList.size() > 0 && !validatedAllUsers) {
            for (Userphone u: selectedUserphoneList){
                 valor += u.getUserphoneCod().toString() + ","; 
             }
             if(valor.length() > 0) {
                valor = valor.substring(0, valor.length()-1);
                sqlWhere = sqlWhere.concat(" AND U.USERPHONE_COD IN (" + valor + ")");
            }
        }
        
        if(validatedAllClassification && selectedClassificationList == null) {
            try {
                selectedClassificationList = new ArrayList<Classification>();
                selectedClassificationList = classificationFacade.findListByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
            } catch (GenericFacadeException e) {
                e.printStackTrace();
            }  
        }
        
        if(parametersValidatedChart) {
            if (selectedClassificationList != null && selectedClassificationList.size() > 0) {
                String classificationsCod ="";
                for(Classification c : selectedClassificationList) {
                    classificationsCod += c.getClassificationCod().toString()+",";
                }   
                if(classificationsCod.length() > 0) {
                    classificationsCod =  classificationsCod.substring(0, classificationsCod.length()-1);
                    sqlWhere += MessageFormat.format(" and EXISTS " + "(select * from USERPHONE_CLASSIFICATION uc " + "where uc.cod_userphone = u.userphone_cod " + "and uc.cod_classification in ({0})) ", classificationsCod);
                }
            }
        }
        
        if(!parametersValidatedChart) {
            if (selectedClassificationList != null && selectedClassificationList.size() > 0) {
                for(Classification c : selectedClassificationList) {
                     valor += c.getClassificationCod().toString() + ","; 
                }
               if(valor.length() > 0) {
                    valor = valor.substring(0, valor.length()-1);
                    sqlWhere = sqlWhere.concat(" AND CL.CLASSIFICATION_COD IN (" + valor + ")");
                }
            }
        }
        if(validatedAllClassification) {
            selectedClassificationList = null;
        }
        
        if (selectedAttendantsList != null && selectedAttendantsList.size() > 0) {
            String aux = "";
            for (MetaData md : selectedAttendantsList) {
                aux = aux.concat("'"+md.getMetaDataPK().getCodeChr()+"'".concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            sqlWhere = String.format(sqlWhere.concat(" AND mdE.CODE_CHR in (%s) "),
                    aux);
        }

        if (selectedOperatorList != null && selectedOperatorList.size() > 0) {
            String aux = "";
            for (MetaData md : selectedOperatorList) {
                aux = aux.concat("'"+md.getMetaDataPK().getCodeChr()+"'".concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            sqlWhere = String.format(sqlWhere.concat(" AND mdO.CODE_CHR in (%s) "),
                    aux);
        }

        if (selectedMachineList != null && selectedMachineList.size() > 0) {
            String aux = "";
            for (MetaData md : selectedMachineList) {
                aux = aux.concat("'"+md.getMetaDataPK().getCodeChr()+"'".concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            sqlWhere = String.format(sqlWhere.concat(" AND mdM.CODE_CHR in (%s) "),
                    aux);
        }

        if (container != null && !container.equals("")) {
            sqlWhere = sqlWhere.concat(String.format(" AND SVD.COLUMN1_CHR like '%s' ",
                    container));
        }

        if (newUbication != null && !newUbication.equals("")) {
            sqlWhere = sqlWhere.concat(String.format(" AND SVD.COLUMN2_CHR like '%s' ",
                    newUbication));
        }
        
        if (numChapa != null && !numChapa.equals("")) {
            sqlWhere = sqlWhere.concat(String.format(" AND SVD.COLUMN3_CHR like '%s' ",
                    numChapa));
        }

        return sqlWhere;
    }

    @Override
    public String getOrderByReport() {
        String sqlOrderBy = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD ASC,q.FECHAHORA_DETALLE DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD,q.FECHAHORA_DETALLE";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY q.CLASSIFICATION_COD,q.NAME_CHR, q.FECHAHORA_DETALLE";
            }

        } else {
            if (selectedOrderby.equals("FD")) {
                sqlOrderBy = " ORDER BY q.FECHAHORA_DETALLE DESC";
            } else if (selectedOrderby.equals("FA")) {
                sqlOrderBy = " ORDER BY q.FECHAHORA_DETALLE";
            } else if (selectedOrderby.equals("US")) {
                sqlOrderBy = " ORDER BY q.NAME_CHR, q.FECHAHORA_DETALLE";
            }
        }

        return sqlOrderBy;
    }

    @Override
    public String getParameterDescriptionReport() {
        String parameterDesc = super.getParameterDescriptionReport();
        if (getSelectedAttendantsList() != null
            && getSelectedAttendantsList().size() > 0) {
            String aux = "";
            for (MetaData md : selectedAttendantsList) {
                aux = aux.concat(md.getValueChr().concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescAttendant")),
                    aux.toString());
        }

        if (getSelectedOperatorList() != null
            && getSelectedOperatorList().size() > 0) {
            String aux = "";
            for (MetaData md : selectedOperatorList) {
                aux = aux.concat(md.getValueChr().concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescOperator")),
                    aux.toString());
        }

        if (getSelectedMachineList() != null
            && getSelectedMachineList().size() > 0) {
            String aux = "";
            for (MetaData md : selectedMachineList) {
                aux = aux.concat(md.getValueChr().concat(","));
            }

            if (aux.length() > 0) {
                aux = aux.substring(0, aux.length() - 1);
            }

            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescMachine")),
                    aux.toString());
        }

        if (container != null && !container.equals("")) {
            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescContainer")),
                    container);
        }

        if (newUbication != null && !newUbication.equals("")) {
            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescNewUbication")),
                    newUbication);
        }
        
        if (numChapa != null && !numChapa.equals("")) {
            parameterDesc = String.format(
                    parameterDesc.concat(i18n.iValue("web.client.backingBean.report.message.ParameterDescNumChapa")),
                    numChapa);
        }
        return parameterDesc;
    }

    @Override
    public String getReportName() {
        String reportName = "";
        if ((getSelectedClassificationList() != null
            && getSelectedClassificationList().size() > 0 && !parametersValidatedChart)
            || validatedAllClassification) {
            reportName = "rep_terport_full_classification";
        } else {
            reportName = "rep_terport_full";
        }
        return reportName;
    }

    @Override
    public String generateReport() {
        return super.generateReport();
    }

    @Override
    public String validateParameters() {
        super.validateParameters();
        if(parametersValidated) {
            if (!validatedAllAttendants
                && (selectedAttendantsList == null || selectedAttendantsList.size() == 0)) {
                parametersValidated = false;
                setWarnMessage(i18n.iValue("web.client.backingBean.report.terport.AttendantNull"));
                return null;
            }

            if (!validatedAllOperators
                && (selectedOperatorList == null || selectedOperatorList.size() == 0)) {
                parametersValidated = false;
                setWarnMessage(i18n.iValue("web.client.backingBean.report.terport.OperatorNull"));
                return null;
            }

            if (!validatedAllMachine
                && (selectedMachineList == null || selectedMachineList.size() == 0)) {
                parametersValidated = false;
                setWarnMessage(i18n.iValue("web.client.backingBean.report.terport.MachineNull"));
                return null;
            }
        }
        
        return null;
    }

    public List<MetaData> getAttendantMetadataList() {
        if (attendantMetadataList == null) {
            attendantMetadataList = metadataFacade.findByClientMetaMemberEnabled(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),18L,1L,4L,i18n.iValue("web.client.backingBean.metadata.attendant.yes"));
        }
        return attendantMetadataList;
    }

    public void setAttendantMetadataList(List<MetaData> attendantMetadataList) {
        this.attendantMetadataList = attendantMetadataList;
    }

    public void selectAllAttendant() {
        if (validatedAllAttendants) {
            setSelectedAttendantsList(null);
        }

    }
    
    public void selectAllOperator() {
        if (validatedAllOperators) {
            selectedOperatorList = null;
        } 

    }
    
    public void selectAllMachine() {
        if (validatedAllMachine) {
            selectedMachineList = null;
        } 

    }

    public List<MetaData> getSelectedAttendantsList() {
        if (selectedAttendantsList == null) {
            selectedAttendantsList = new ArrayList<MetaData>();
        }
        return selectedAttendantsList;
    }

    public void setSelectedAttendantsList(List<MetaData> selectedAttendantsList) {
        this.selectedAttendantsList = selectedAttendantsList;
    }

    public Boolean getValidatedAllAttendants() {
        return validatedAllAttendants;
    }

    public void setValidatedAllAttendants(Boolean validatedAllAttendants) {
        this.validatedAllAttendants = validatedAllAttendants;
    }

    public List<MetaData> getOperatorMetadataList() {
        if (operatorMetadataList == null) {
            operatorMetadataList = metadataFacade.findByClientMetaMemberEnabled(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),20L,1L,2L,i18n.iValue("web.client.backingBean.metadata.operator.yes"));
        }
        return operatorMetadataList;
    }

    public void setOperatorMetadataList(List<MetaData> operatorMetadataList) {
        this.operatorMetadataList = operatorMetadataList;
    }

    public List<MetaData> getSelectedOperatorList() {
        if (selectedOperatorList == null) {
            selectedOperatorList = new ArrayList<MetaData>();
        }
        return selectedOperatorList;
    }

    public void setSelectedOperatorList(List<MetaData> selectedOperatorList) {
        this.selectedOperatorList = selectedOperatorList;
    }

    public Boolean getValidatedAllOperators() {
        return validatedAllOperators;
    }

    public void setValidatedAllOperators(Boolean validatedAllOperators) {
        this.validatedAllOperators = validatedAllOperators;
    }

    public List<MetaData> getMachineMetadataList() {
        if (machineMetadataList == null) {
            machineMetadataList = metadataFacade.findByClientMetaMemberEnabled(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),19L,1L,2L,i18n.iValue("web.client.backingBean.metadata.machine.yes"));
        }
        return machineMetadataList;
    }

    public void setMachineMetadataList(List<MetaData> machineMetadataList) {
        this.machineMetadataList = machineMetadataList;
    }

    public List<MetaData> getSelectedMachineList() {
        if (selectedMachineList == null) {
            selectedMachineList = new ArrayList<MetaData>();
        }
        return selectedMachineList;
    }

    public void setSelectedMachineList(List<MetaData> selectedMachineList) {
        this.selectedMachineList = selectedMachineList;
    }

    public Boolean getValidatedAllMachine() {
        return validatedAllMachine;
    }

    public void setValidatedAllMachine(Boolean validatedAllMachine) {
        this.validatedAllMachine = validatedAllMachine;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getNewUbication() {
        return newUbication;
    }

    public void setNewUbication(String newUbication) {
        this.newUbication = newUbication;
    }

    public String getNumChapa() {
        return numChapa;
    }

    public void setNumChapa(String numChapa) {
        this.numChapa = numChapa;
    }

}
