package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "process")
@NamedQueries({ @NamedQuery(
        name = "Process.findByStatusId",
        query = "SELECT p FROM Process p WHERE p.processStatus.processstatusCod = :processstatusCod") })
public class Process implements Serializable {

    private static final long serialVersionUID = 295387858399491111L;
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "processGen", sequenceName = "PROCESS_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "processGen")
    @Column(name = "PROCESS_COD")
    private Long processCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "CLASS_CHR")
    private String classChr;
    @Basic(optional = false)
    @Column(name = "LASTEXEC_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastexecDat;
    @Column(name = "PARAMETERS_CHR")
    private String parametersChr;
    @JoinColumn(name = "COD_PROCESSSTATUS",
            referencedColumnName = "PROCESSSTATUS_COD")
    @ManyToOne(optional = false)
    private ProcessStatus processStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "process")
    private List<ProcessExecution> processExecutionList;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "process")
    private List<ProcessDependency> dependentProcessList;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "processDependency")
    private List<ProcessDependency> dependentOnThisProcessList;

    public Process() {
    }

    public Process(Long processCod) {
        this.processCod = processCod;
    }

    public Process(Long processCod, String descriptionChr, String classChr,
            Date lastexecDat) {
        this.processCod = processCod;
        this.descriptionChr = descriptionChr;
        this.classChr = classChr;
        this.lastexecDat = lastexecDat;
    }

    public Long getProcessCod() {
        return processCod;
    }

    public void setProcessCod(Long processCod) {
        this.processCod = processCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getClassChr() {
        return classChr;
    }

    public void setClassChr(String classChr) {
        this.classChr = classChr;
    }

    public Date getLastexecDat() {
        return lastexecDat;
    }

    public void setLastexecDat(Date lastexecDat) {
        this.lastexecDat = lastexecDat;
    }

    public String getParametersChr() {
        return parametersChr;
    }

    public void setParametersChr(String parametersChr) {
        this.parametersChr = parametersChr;
    }

    public List<ProcessExecution> getProcessExecutionList() {
        return processExecutionList;
    }

    public void setProcessExecutionList(List<ProcessExecution> processExecutionList) {
        this.processExecutionList = processExecutionList;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public List<ProcessDependency> getDependentOnThisProcessList() {
        return dependentOnThisProcessList;
    }

    public void setDependentOnThisProcessList(List<ProcessDependency> dependentOnThisProcessList) {
        this.dependentOnThisProcessList = dependentOnThisProcessList;
    }

    public List<ProcessDependency> getDependentProcessList() {
        return dependentProcessList;
    }

    public void setDependentProcessList(List<ProcessDependency> dependentProcessList) {
        this.dependentProcessList = dependentProcessList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Process other = (Process) obj;
        if (this.processCod != other.processCod
            && (this.processCod == null || !this.processCod.equals(other.processCod))) {
            return false;
        }
        if ((this.descriptionChr == null) ? (other.descriptionChr != null) : !this.descriptionChr.equals(other.descriptionChr)) {
            return false;
        }
        if ((this.classChr == null) ? (other.classChr != null) : !this.classChr.equals(other.classChr)) {
            return false;
        }
        if (this.lastexecDat != other.lastexecDat
            && (this.lastexecDat == null || !this.lastexecDat.equals(other.lastexecDat))) {
            return false;
        }
        if ((this.parametersChr == null) ? (other.parametersChr != null) : !this.parametersChr.equals(other.parametersChr)) {
            return false;
        }
        if (this.processStatus != other.processStatus
            && (this.processStatus == null || !this.processStatus.equals(other.processStatus))) {
            return false;
        }
        if (this.processExecutionList != other.processExecutionList
            && (this.processExecutionList == null || !this.processExecutionList.equals(other.processExecutionList))) {
            return false;
        }
        if (this.dependentProcessList != other.dependentProcessList
            && (this.dependentProcessList == null || !this.dependentProcessList.equals(other.dependentProcessList))) {
            return false;
        }
        if (this.dependentOnThisProcessList != other.dependentOnThisProcessList
            && (this.dependentOnThisProcessList == null || !this.dependentOnThisProcessList.equals(other.dependentOnThisProcessList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash
            + (this.processCod != null ? this.processCod.hashCode() : 0);
        hash = 89
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 89 * hash
            + (this.classChr != null ? this.classChr.hashCode() : 0);
        hash = 89 * hash
            + (this.lastexecDat != null ? this.lastexecDat.hashCode() : 0);
        hash = 89 * hash
            + (this.parametersChr != null ? this.parametersChr.hashCode() : 0);
        hash = 89 * hash
            + (this.processStatus != null ? this.processStatus.hashCode() : 0);
        hash = 89
            * hash
            + (this.processExecutionList != null ? this.processExecutionList.hashCode() : 0);
        hash = 89
            * hash
            + (this.dependentProcessList != null ? this.dependentProcessList.hashCode() : 0);
        hash = 89
            * hash
            + (this.dependentOnThisProcessList != null ? this.dependentOnThisProcessList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Process[processCod=" + processCod + "]";
    }
}
