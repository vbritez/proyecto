package py.com.lothar.menumovil.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "lothar")
public class MessageQuotaResponse {

    private int code;
    private String description;
    private int usedQuota;
    private int availableQuota;

    public MessageQuotaResponse() {
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

    public int getUsedQuota() {
        return usedQuota;
    }

    public void setUsedQuota(int usedQuota) {
        this.usedQuota = usedQuota;
    }

    public int getAvailableQuota() {
        return availableQuota;
    }

    public void setAvailableQuota(int availableQuota) {
        this.availableQuota = availableQuota;
    }

}
