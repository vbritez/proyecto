package py.com.lothar.menumovil.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "lothar")
public class SendMessageResponse {

    private int code;
    private String description;
    private Long refCode;
    private int availableQuota;

    public SendMessageResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRefCode() {
        return refCode;
    }

    public void setRefCode(Long refCode) {
        this.refCode = refCode;
    }

    public int getAvailableQuota() {
        return availableQuota;
    }

    public void setAvailableQuota(int availableQuota) {
        this.availableQuota = availableQuota;
    }

}
