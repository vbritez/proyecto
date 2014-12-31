package ws.tigomoney.tigo.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BaseRequest {

	@XmlElement(required = true)
    protected String consumerID;
    @XmlElement(required = true)
    protected String consumerPWD;
    @XmlElement(required = true)
    protected String transactionID;
    @XmlElement(required = true)
    protected String source;
    

    public String getConsumerID() {
        return consumerID;
    }

    public void setConsumerID(String value) {
        this.consumerID = value;
    }

    public String getConsumerPWD() {
        return consumerPWD;
    }
    
    public void setConsumerPWD(String value) {
        this.consumerPWD = value;
    }
    public String getTransactionID() {
        return transactionID;
    }
    
    public void setTransactionID(String value) {
        this.transactionID = value;
    }
    

    public String getSource() {
        return source;
    }
    public void setSource(String value) {
        this.source = value;
    }
}
