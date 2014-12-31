package ws.tigomoney.tigo.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class BaseResponse {
	
	protected String transactionID;
    protected String code;
    protected String description;
    
    public String getTransactionID() {
        return transactionID;
    }
   
    public void setTransactionID(String value) {
        this.transactionID = value;
    }
  
    public String getCode() {
        return code;
    }
   
    public void setCode(String value) {
        this.code = value;
    }
   
    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

}
