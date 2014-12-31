//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.21 at 10:11:54 AM PYT 
//


package ws.tigomoney.tigo.com;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ws.tigomoney.tigo.com package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _UpdateCIBackPhoto_QNAME = new QName("", "backPhoto");
    private final static QName _UpdateCIFrontPhoto_QNAME = new QName("", "frontPhoto");
    private final static QName _ExistsCI_QNAME = new QName("http://ws.tigomoney.tigo.com/", "existsCI");
    private final static QName _ExistsCIResponse_QNAME = new QName("http://ws.tigomoney.tigo.com/", "existsCIResponse");
    private final static QName _RegisterCIResponse_QNAME = new QName("http://ws.tigomoney.tigo.com/", "registerCIResponse");
    private final static QName _RegisterCI_QNAME = new QName("http://ws.tigomoney.tigo.com/", "registerCI");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ws.tigomoney.tigo.com
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetCIResponse }
     * 
     */
    public GetCIResponse createGetCIResponse() {
        return new GetCIResponse();
    }

    /**
     * Create an instance of {@link UpdateCI }
     * 
     */
    public UpdateCI createUpdateCI() {
        return new UpdateCI();
    }

    /**
     * Create an instance of {@link RegisterCI }
     * 
     */
    public RegisterCI createRegisterCI() {
        return new RegisterCI();
    }

    /**
     * Create an instance of {@link RegisterCIResponse }
     * 
     */
    public RegisterCIResponse createRegisterCIResponse() {
        return new RegisterCIResponse();
    }

    /**
     * Create an instance of {@link ExistsCIResponse }
     * 
     */
    public ExistsCIResponse createExistsCIResponse() {
        return new ExistsCIResponse();
    }

    /**
     * Create an instance of {@link UpdateCIResponse }
     * 
     */
    public UpdateCIResponse createUpdateCIResponse() {
        return new UpdateCIResponse();
    }

    /**
     * Create an instance of {@link ExistsCI }
     * 
     */
    public ExistsCI createExistsCI() {
        return new ExistsCI();
    }

    /**
     * Create an instance of {@link GetCI }
     * 
     */
    public GetCI createGetCI() {
        return new GetCI();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "backPhoto", scope = UpdateCI.class)
    public JAXBElement<byte[]> createUpdateCIBackPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIBackPhoto_QNAME, byte[].class, UpdateCI.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "frontPhoto", scope = UpdateCI.class)
    public JAXBElement<byte[]> createUpdateCIFrontPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIFrontPhoto_QNAME, byte[].class, UpdateCI.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "backPhoto", scope = GetCIResponse.class)
    public JAXBElement<byte[]> createGetCIResponseBackPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIBackPhoto_QNAME, byte[].class, GetCIResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "frontPhoto", scope = GetCIResponse.class)
    public JAXBElement<byte[]> createGetCIResponseFrontPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIFrontPhoto_QNAME, byte[].class, GetCIResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsCI }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.tigomoney.tigo.com/", name = "existsCI")
    public JAXBElement<ExistsCI> createExistsCI(ExistsCI value) {
        return new JAXBElement<ExistsCI>(_ExistsCI_QNAME, ExistsCI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExistsCIResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.tigomoney.tigo.com/", name = "existsCIResponse")
    public JAXBElement<ExistsCIResponse> createExistsCIResponse(ExistsCIResponse value) {
        return new JAXBElement<ExistsCIResponse>(_ExistsCIResponse_QNAME, ExistsCIResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterCIResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.tigomoney.tigo.com/", name = "registerCIResponse")
    public JAXBElement<RegisterCIResponse> createRegisterCIResponse(RegisterCIResponse value) {
        return new JAXBElement<RegisterCIResponse>(_RegisterCIResponse_QNAME, RegisterCIResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterCI }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.tigomoney.tigo.com/", name = "registerCI")
    public JAXBElement<RegisterCI> createRegisterCI(RegisterCI value) {
        return new JAXBElement<RegisterCI>(_RegisterCI_QNAME, RegisterCI.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "backPhoto", scope = RegisterCI.class)
    public JAXBElement<byte[]> createRegisterCIBackPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIBackPhoto_QNAME, byte[].class, RegisterCI.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "frontPhoto", scope = RegisterCI.class)
    public JAXBElement<byte[]> createRegisterCIFrontPhoto(byte[] value) {
        return new JAXBElement<byte[]>(_UpdateCIFrontPhoto_QNAME, byte[].class, RegisterCI.class, ((byte[]) value));
    }

}
