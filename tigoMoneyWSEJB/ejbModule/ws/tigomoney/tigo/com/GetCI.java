package ws.tigomoney.tigo.com;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "getCI", propOrder = {
//    "consumerID",
//    "consumerPWD",
//    "transactionID",
//    "source",
//    "ci"
//})
public class GetCI extends BaseRequest{
    
    @XmlElement(required = true)
    protected String ci;

    
    public String getCi() {
        return ci;
    }
    
    public void setCi(String value) {
        this.ci = value;
    }   

	@Override
	public String toString() {
		return "[ci=" + ci + ", consumerID=" + consumerID
				+ ", consumerPWD=" + consumerPWD + ", transactionID="
				+ transactionID + ", source=" + source + "]";
	}

	
}