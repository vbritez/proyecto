package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "process_status")
public class ProcessStatus implements Serializable {

    private static final long serialVersionUID = -8160537580576113885L;
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "processStatusGen",
            sequenceName = "PROCESS_STATUS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "processStatusGen")
    @Column(name = "PROCESSSTATUS_COD")
    private Long processstatusCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "processStatus")
    private List<Process> processList;

    public ProcessStatus() {
    }

    public ProcessStatus(Long processstatusCod) {
        this.processstatusCod = processstatusCod;
    }

    public ProcessStatus(Long processstatusCod, String descriptionChr) {
        this.processstatusCod = processstatusCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getProcessstatusCod() {
        return processstatusCod;
    }

    public void setProcessstatusCod(Long processstatusCod) {
        this.processstatusCod = processstatusCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessStatus other = (ProcessStatus) obj;
        if (this.processstatusCod != other.processstatusCod
            && (this.processstatusCod == null || !this.processstatusCod.equals(other.processstatusCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23
            * hash
            + (this.processstatusCod != null ? this.processstatusCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ProcessStatus[processstatusCod=" + processstatusCod + "]";
    }
}
