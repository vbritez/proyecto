package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "process_execution")
public class ProcessExecution implements Serializable {

    private static final long serialVersionUID = -7522170901554379648L;
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "processExecutionGen",
            sequenceName = "PROCESS_EXECUTION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "processExecutionGen")
    @Column(name = "PROCESSEXECUTION_COD")
    private Long processexecutionCod;
    @Basic(optional = false)
    @Column(name = "VALUE_CHR")
    private String valueChr;
    @JoinColumn(name = "COD_PROCESS", referencedColumnName = "PROCESS_COD")
    @ManyToOne(optional = false)
    private Process process;
    @JoinColumn(name = "COD_EXECUTIONTYPE",
            referencedColumnName = "EXECUTIONTYPE_COD")
    @ManyToOne(optional = false)
    private ExecutionType executionType;

    public ProcessExecution() {
    }

    public ProcessExecution(Long processexecutionCod) {
        this.processexecutionCod = processexecutionCod;
    }

    public ProcessExecution(Long processexecutionCod, String valueChr) {
        this.processexecutionCod = processexecutionCod;
        this.valueChr = valueChr;
    }

    public Long getProcessexecutionCod() {
        return processexecutionCod;
    }

    public void setProcessexecutionCod(Long processexecutionCod) {
        this.processexecutionCod = processexecutionCod;
    }

    public String getValueChr() {
        return valueChr;
    }

    public void setValueChr(String valueChr) {
        this.valueChr = valueChr;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessExecution other = (ProcessExecution) obj;
        if (this.processexecutionCod != other.processexecutionCod
            && (this.processexecutionCod == null || !this.processexecutionCod.equals(other.processexecutionCod))) {
            return false;
        }
        if ((this.valueChr == null) ? (other.valueChr != null) : !this.valueChr.equals(other.valueChr)) {
            return false;
        }
        if (this.process != other.process
            && (this.process == null || !this.process.equals(other.process))) {
            return false;
        }
        if (this.executionType != other.executionType
            && (this.executionType == null || !this.executionType.equals(other.executionType))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53
            * hash
            + (this.processexecutionCod != null ? this.processexecutionCod.hashCode() : 0);
        hash = 53 * hash
            + (this.valueChr != null ? this.valueChr.hashCode() : 0);
        hash = 53 * hash + (this.process != null ? this.process.hashCode() : 0);
        hash = 53 * hash
            + (this.executionType != null ? this.executionType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ProcessExecution[processexecutionCod=" + processexecutionCod
            + "]";
    }
}
