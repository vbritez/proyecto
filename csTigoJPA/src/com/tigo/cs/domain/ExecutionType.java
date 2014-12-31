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
@Table(name = "execution_type")
public class ExecutionType implements Serializable {

    private static final long serialVersionUID = 9212342692537923681L;
    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "executionTypeGen",
            sequenceName = "EXECUTION_TYPE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "executionTypeGen")
    @Column(name = "EXECUTIONTYPE_COD")
    private Long executiontypeCod;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "SYNTAX_CHR")
    private String syntaxChr;
    @OneToMany(
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            mappedBy = "executionType")
    private List<ProcessExecution> processExecutionList;

    public ExecutionType() {
    }

    public ExecutionType(Long executiontypeCod) {
        this.executiontypeCod = executiontypeCod;
    }

    public ExecutionType(Long executiontypeCod, String descriptionChr,
            String syntaxChr) {
        this.executiontypeCod = executiontypeCod;
        this.descriptionChr = descriptionChr;
        this.syntaxChr = syntaxChr;
    }

    public Long getExecutiontypeCod() {
        return executiontypeCod;
    }

    public void setExecutiontypeCod(Long executiontypeCod) {
        this.executiontypeCod = executiontypeCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getSyntaxChr() {
        return syntaxChr;
    }

    public void setSyntaxChr(String syntaxChr) {
        this.syntaxChr = syntaxChr;
    }

    public List<ProcessExecution> getProcessExecutionList() {
        return processExecutionList;
    }

    public void setProcessExecutionList(List<ProcessExecution> processExecutionList) {
        this.processExecutionList = processExecutionList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionType other = (ExecutionType) obj;
        if (this.executiontypeCod != other.executiontypeCod
            && (this.executiontypeCod == null || !this.executiontypeCod.equals(other.executiontypeCod))) {
            return false;
        }
        if ((this.descriptionChr == null) ? (other.descriptionChr != null) : !this.descriptionChr.equals(other.descriptionChr)) {
            return false;
        }
        if ((this.syntaxChr == null) ? (other.syntaxChr != null) : !this.syntaxChr.equals(other.syntaxChr)) {
            return false;
        }
        if (this.processExecutionList != other.processExecutionList
            && (this.processExecutionList == null || !this.processExecutionList.equals(other.processExecutionList))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79
            * hash
            + (this.executiontypeCod != null ? this.executiontypeCod.hashCode() : 0);
        hash = 79
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        hash = 79 * hash
            + (this.syntaxChr != null ? this.syntaxChr.hashCode() : 0);
        hash = 79
            * hash
            + (this.processExecutionList != null ? this.processExecutionList.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ExecutionType[executiontypeCod=" + executiontypeCod + "]";
    }
}
