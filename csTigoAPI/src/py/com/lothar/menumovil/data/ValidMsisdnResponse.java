package py.com.lothar.menumovil.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "lothar")
public class ValidMsisdnResponse {

    private int code;
    private String description;
    private boolean isValid;

    public ValidMsisdnResponse() {
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

}
