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
@Table(name = "process_dependency")
public class ProcessDependency implements Serializable {

    private static final long serialVersionUID = 7774727989136702503L;
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "processDependencyGen",
            sequenceName = "PROCESS_DEPENDENCY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "processDependencyGen")
    @Column(name = "PROCESSDEPENDENCY_COD")
    private Long processdependencyCod;
    @JoinColumn(name = "COD_PROCESS", referencedColumnName = "PROCESS_COD")
    @ManyToOne(optional = false)
    private Process process;
    @JoinColumn(name = "COD_PROCESSDEPENDENCY",
            referencedColumnName = "PROCESS_COD")
    @ManyToOne(optional = false)
    private Process processDependency;

    public ProcessDependency() {
    }

    public ProcessDependency(Long processdependencyCod) {
        this.processdependencyCod = processdependencyCod;
    }

    public Long getProcessdependencyCod() {
        return processdependencyCod;
    }

    public void setProcessdependencyCod(Long processdependencyCod) {
        this.processdependencyCod = processdependencyCod;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Process getProcessDependency() {
        return processDependency;
    }

    public void setProcessDependency(Process processDependency) {
        this.processDependency = processDependency;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessDependency other = (ProcessDependency) obj;
        if (this.processdependencyCod != other.processdependencyCod
            && (this.processdependencyCod == null || !this.processdependencyCod.equals(other.processdependencyCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61
            * hash
            + (this.processdependencyCod != null ? this.processdependencyCod.hashCode() : 0);
        hash = 61 * hash + (this.process != null ? this.process.hashCode() : 0);
        hash = 61
            * hash
            + (this.processDependency != null ? this.processDependency.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ProcessDependency[processdependencyCod=" + processdependencyCod
            + "]";
    }
}
