//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.19 at 02:43:54 PM PYT 
//


package ws.tigomoney.tigo.com;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
public class GetCIResponse extends BaseResponse{

    @XmlElementRef(name = "frontPhoto", type = JAXBElement.class)
    protected JAXBElement<byte[]> frontPhoto;
    @XmlElementRef(name = "backPhoto", type = JAXBElement.class)
    protected JAXBElement<byte[]> backPhoto;
    protected String ci;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    protected String address;
    protected String city;
    protected String source;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar insertedOnDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar updatedOnDate;
    
    public JAXBElement<byte[]> getFrontPhoto() {
        return frontPhoto;
    }

    public void setFrontPhoto(JAXBElement<byte[]> value) {
        this.frontPhoto = ((JAXBElement<byte[]> ) value);
    }

    public JAXBElement<byte[]> getBackPhoto() {
        return backPhoto;
    }
  
    public void setBackPhoto(JAXBElement<byte[]> value) {
        this.backPhoto = ((JAXBElement<byte[]> ) value);
    }

	public String getCi() {
		return ci;
	}

	public XMLGregorianCalendar getBirthDate() {
		return birthDate;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getSource() {
		return source;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public void setBirthDate(XMLGregorianCalendar birthDate) {
		this.birthDate = birthDate;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public XMLGregorianCalendar getInsertedOnDate() {
		return insertedOnDate;
	}

	public XMLGregorianCalendar getUpdatedOnDate() {
		return updatedOnDate;
	}

	public void setInsertedOnDate(XMLGregorianCalendar insertedOnDate) {
		this.insertedOnDate = insertedOnDate;
	}

	public void setUpdatedOnDate(XMLGregorianCalendar updatedOnDate) {
		this.updatedOnDate = updatedOnDate;
	}

	@Override
	public String toString() {
		return "[frontPhoto=" + frontPhoto + ", backPhoto="
				+ backPhoto + ", ci=" + ci + ", birthDate=" + birthDate
				+ ", address=" + address + ", city=" + city + ", source="
				+ source + ", insertedOnDate=" + insertedOnDate
				+ ", updatedOnDate=" + updatedOnDate + ", transactionID="
				+ transactionID + ", code=" + code + ", description="
				+ description + "]";
	}
	
	

}
