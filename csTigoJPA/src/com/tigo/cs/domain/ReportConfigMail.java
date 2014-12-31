package com.tigo.cs.domain;

import java.io.Serializable;

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
@Table(name = "REPORT_CONFIG_MAIL")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ReportConfigMail implements Serializable {

    private static final long serialVersionUID = -4479076714284386774L;

    @Id
    @SequenceGenerator(name = "REPORT_CONFIG_MAIL_MAILCOD_GENERATOR",
            sequenceName = "REPORT_CONFIG_MAIL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "REPORT_CONFIG_MAIL_MAILCOD_GENERATOR")
    @Column(name = "MAIL_COD")
    private Long mailCod;

    @Column(name = "MAIL_CHR")
    private String mailChr;

    // bi-directional many-to-one association to ReportConfig
    @ManyToOne
    @JoinColumn(name = "COD_REPORT_CONFIG")
    private ReportConfig reportConfig;

    public ReportConfigMail() {
    }

    public Long getMailCod() {
        return this.mailCod;
    }

    public void setMailCod(Long mailCod) {
        this.mailCod = mailCod;
    }

    public String getMailChr() {
        return this.mailChr;
    }

    public void setMailChr(String mailChr) {
        this.mailChr = mailChr;
    }

    public ReportConfig getReportConfig() {
        return this.reportConfig;
    }

    public void setReportConfig(ReportConfig reportConfig) {
        this.reportConfig = reportConfig;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mailCod == null) ? 0 : mailCod.hashCode());
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
        ReportConfigMail other = (ReportConfigMail) obj;
        if (mailCod == null) {
            if (other.mailCod != null)
                return false;
        } else if (!mailCod.equals(other.mailCod))
            return false;
        return true;
    }

}