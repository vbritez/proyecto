package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = -4447382962493483096L;
    @Id
    @SequenceGenerator(name = "messageGen", sequenceName = "message_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "messageGen")
    @Basic(optional = false)
    @Column(name = "message_cod")
    private Long messageCod;
    @Basic(optional = false)
    @Column(name = "datein_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateinDat;
    @Basic(optional = false)
    @Column(name = "dateout_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateoutDat;
    @Basic(optional = false)
    @Column(name = "destination_chr")
    private String destinationChr;
    @Basic(optional = false)
    @Column(name = "lengthin_num")
    private int lengthinNum;
    @Basic(optional = false)
    @Column(name = "lengthout_num")
    private int lengthoutNum;
    @Basic(optional = false)
    @Column(name = "messagein_chr")
    private String messageinChr;
    @Basic(optional = false)
    @Column(name = "messageout_chr")
    private String messageoutChr;
    @Basic(optional = false)
    @Column(name = "origin_chr")
    private String originChr;

    @Column(name = "LATITUDE")
    private Double latitude;
    @Column(name = "LONGITUDE")
    private Double longitude;

    @JoinColumn(name = "cod_userweb", referencedColumnName = "userweb_cod")
    @ManyToOne
    private Userweb userweb;
    @JoinColumn(name = "cod_userphone", referencedColumnName = "userphone_cod")
    @ManyToOne(optional = true)
    private Userphone userphone;
    @JoinColumn(name = "cod_useradmin", referencedColumnName = "useradmin_cod")
    @ManyToOne
    private Useradmin useradmin;
    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    private Client client;
    @JoinColumn(name = "cod_cell", referencedColumnName = "cell_cod")
    @ManyToOne
    private Cell cell;
    @OneToMany(mappedBy = "message")
    private Collection<ServiceValue> serviceValueCollection;
    @OneToMany(mappedBy = "message")
    private Collection<ServiceValueDetail> serviceValueDetailCollection;
    @OneToMany(mappedBy = "message")
    private Collection<MessageNotSent> messageNotSentList;
    @JoinColumn(name = "cod_application",
            referencedColumnName = "application_cod")
    @ManyToOne
    private Application application;

    @Column(name = "version_name")
    private String versionName;

    @JoinColumn(name = "cod_service", referencedColumnName = "service_cod")
    @ManyToOne
    private Service service;

    public Message() {
    }

    public Message(Long messageCod) {
        this.messageCod = messageCod;
    }

    public Message(Long messageCod, Date dateinDat, Date dateoutDat,
            String destinationChr, int lengthinNum, int lengthoutNum,
            String messageinChr, String messageoutChr, String originChr,
            double azimuthNum, int timingadvanceNum) {
        this.messageCod = messageCod;
        this.dateinDat = dateinDat;
        this.dateoutDat = dateoutDat;
        this.destinationChr = destinationChr;
        this.lengthinNum = lengthinNum;
        this.lengthoutNum = lengthoutNum;
        this.messageinChr = messageinChr;
        this.messageoutChr = messageoutChr;
        this.originChr = originChr;
    }

    public Long getMessageCod() {
        return messageCod;
    }

    public void setMessageCod(Long messageCod) {
        this.messageCod = messageCod;
    }

    public Date getDateinDat() {
        return dateinDat;
    }

    public void setDateinDat(Date dateinDat) {
        this.dateinDat = dateinDat;
    }

    public Date getDateoutDat() {
        return dateoutDat;
    }

    public void setDateoutDat(Date dateoutDat) {
        this.dateoutDat = dateoutDat;
    }

    public String getDestinationChr() {
        return destinationChr;
    }

    public void setDestinationChr(String destinationChr) {
        this.destinationChr = destinationChr;
    }

    public int getLengthinNum() {
        return lengthinNum;
    }

    public void setLengthinNum(int lengthinNum) {
        this.lengthinNum = lengthinNum;
    }

    public int getLengthoutNum() {
        return lengthoutNum;
    }

    public void setLengthoutNum(int lengthoutNum) {
        this.lengthoutNum = lengthoutNum;
    }

    public String getMessageinChr() {
        return messageinChr;
    }

    public void setMessageinChr(String messageinChr) {
        this.messageinChr = messageinChr;
    }

    public String getMessageoutChr() {
        return messageoutChr;
    }

    public void setMessageoutChr(String messageoutChr) {
        this.messageoutChr = messageoutChr;
    }

    public String getOriginChr() {
        return originChr;
    }

    public void setOriginChr(String originChr) {
        this.originChr = originChr;
    }

    public Userweb getUserweb() {
        return userweb;
    }

    public void setUserweb(Userweb userweb) {
        this.userweb = userweb;
    }

    public Userphone getUserphone() {
        return userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    public Useradmin getUseradmin() {
        return useradmin;
    }

    public void setUseradmin(Useradmin useradmin) {
        this.useradmin = useradmin;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Collection<ServiceValue> getServiceValueCollection() {
        return serviceValueCollection;
    }

    public void setServiceValueCollection(Collection<ServiceValue> serviceValueCollection) {
        this.serviceValueCollection = serviceValueCollection;
    }

    public Collection<ServiceValueDetail> getServiceValueDetailCollection() {
        return serviceValueDetailCollection;
    }

    public void setServiceValueDetailCollection(Collection<ServiceValueDetail> serviceValueDetailCollection) {
        this.serviceValueDetailCollection = serviceValueDetailCollection;
    }

    public Collection<MessageNotSent> getMessageNotSentList() {
        return messageNotSentList;
    }

    public void setMessageNotSentList(Collection<MessageNotSent> messageNotSentList) {
        this.messageNotSentList = messageNotSentList;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (this.messageCod != other.messageCod
            && (this.messageCod == null || !this.messageCod.equals(other.messageCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash
            + (this.messageCod != null ? this.messageCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return messageinChr.concat(" (Cod.: ").concat(messageCod.toString()).concat(
                ")");
    }
}
