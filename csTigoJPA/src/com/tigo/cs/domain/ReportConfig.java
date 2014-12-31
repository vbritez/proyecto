package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "REPORT_CONFIG")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "ReportConfig.getReportConfigByStatus",
        query = "SELECT rc FROM ReportConfig rc WHERE rc.statusChr = :status AND rc.client.enabledChr = true") })
public class ReportConfig implements Serializable {

    private static final long serialVersionUID = 565629440122320887L;

    @Id
    @SequenceGenerator(name = "REPORT_CONFIG_REPORTCONFIGCOD_GENERATOR",
            sequenceName = "REPORT_CONFIG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "REPORT_CONFIG_REPORTCONFIGCOD_GENERATOR")
    @Column(name = "REPORT_CONFIG_COD")
    @PrimarySortedField
    @Searchable(label = "entity.reportconfig.searchable.configCod")
    private Long reportConfigCod;

    @Column(name = "CONFIG_TYPE_CHR")
    @Searchable(label = "entity.reportconfig.searchable.configType")
    private String configTypeChr;

    @Column(name = "DAY_NUM")
    private Integer dayNum;

    @Searchable(label = "entity.reportconfig.searchable.description")
    @Column(name = "DESCRIPTION_CHR")
    private String descriptionChr;

    @Column(name = "NEXT_EXECUTION_TIME_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextExecutionTimeDat;

    @Column(name = "REPORT_TYPE_CHR")
    private String reportTypeChr;

    @Column(name = "START_TIME_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTimeDat;

    @Column(name = "STATUS_CHR")
    private Boolean statusChr;

    @Column(name = "TIME_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeDat;

    // bi-directional many-to-one association to Userweb
    @ManyToOne
    @JoinColumn(name = "COD_USERWEB")
    private Userweb userweb;

    // bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name = "COD_CLIENT")
    private Client client;

    // bi-directional many-to-many association to Classification
    @ManyToMany
    @JoinTable(name = "REPORT_CONFIG_CLASSIF", joinColumns = { @JoinColumn(
            name = "COD_REPORT_CONFIG") }, inverseJoinColumns = { @JoinColumn(
            name = "COD_CLASSIFICATION") })
    private List<Classification> classifications;

    // bi-directional many-to-one association to ReportConfigMail
    @OneToMany(mappedBy = "reportConfig")
    private List<ReportConfigMail> reportConfigMails;

    // bi-directional many-to-many association to Screenclient
    @ManyToMany
    @JoinTable(name = "REPORT_CONFIG_SCREENCLIENT",
            joinColumns = { @JoinColumn(name = "COD_REPORT_CONFIG") },
            inverseJoinColumns = { @JoinColumn(name = "COD_SCREENCLIENT") })
    private List<Screenclient> screenclients;

    public ReportConfig() {
    }

    public Long getReportConfigCod() {
        return this.reportConfigCod;
    }

    public void setReportConfigCod(Long reportConfigCod) {
        this.reportConfigCod = reportConfigCod;
    }

    public String getConfigTypeChr() {
        return this.configTypeChr;
    }

    public void setConfigTypeChr(String configTypeChr) {
        this.configTypeChr = configTypeChr;
    }

    public Integer getDayNum() {
        return this.dayNum;
    }

    public void setDayNum(Integer dayNum) {
        this.dayNum = dayNum;
    }

    public String getDescriptionChr() {
        return this.descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Date getNextExecutionTimeDat() {
        return this.nextExecutionTimeDat;
    }

    public void setNextExecutionTimeDat(Date nextExecutionTimeDat) {
        this.nextExecutionTimeDat = nextExecutionTimeDat;
    }

    public String getReportTypeChr() {
        return this.reportTypeChr;
    }

    public void setReportTypeChr(String reportTypeChr) {
        this.reportTypeChr = reportTypeChr;
    }

    public Date getStartTimeDat() {
        return this.startTimeDat;
    }

    public void setStartTimeDat(Date startTimeDat) {
        this.startTimeDat = startTimeDat;
    }

    public Boolean getStatusChr() {
        return this.statusChr;
    }

    public void setStatusChr(Boolean statusChr) {
        this.statusChr = statusChr;
    }

    public Date getTimeDat() {
        return this.timeDat;
    }

    public void setTimeDat(Date timeDat) {
        this.timeDat = timeDat;
    }

    public Userweb getUserweb() {
        return this.userweb;
    }

    public void setUserweb(Userweb userweb) {
        this.userweb = userweb;
    }

    public List<Classification> getClassifications() {
        return this.classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public List<ReportConfigMail> getReportConfigMails() {
        return this.reportConfigMails;
    }

    public void setReportConfigMails(List<ReportConfigMail> reportConfigMails) {
        this.reportConfigMails = reportConfigMails;
    }

    public List<Screenclient> getScreenclients() {
        return this.screenclients;
    }

    public void setScreenclients(List<Screenclient> screenclients) {
        this.screenclients = screenclients;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((reportConfigCod == null) ? 0 : reportConfigCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReportConfig other = (ReportConfig) obj;
        if (reportConfigCod == null) {
            if (other.reportConfigCod != null)
                return false;
        } else if (reportConfigCod.longValue() != other.reportConfigCod.longValue())
            return false;
        return true;
    }

}