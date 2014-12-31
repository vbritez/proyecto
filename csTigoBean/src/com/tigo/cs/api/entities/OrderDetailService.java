package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDetailService extends ServiceBean {

    private static final long serialVersionUID = 2874748929388102887L;

    private Integer id;

    @MetaColumn(metaname = MetaNames.PRODUCT)
    private String productCode;

    private String quantity;
    private String discount;
    private String unitPrice;

    public OrderDetailService() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        String pattern = "\"productCode\":\"{0}\",\"quantity\":\"{1}\",\"discount\":\"{2}\",\"unitPrice\":\"{3}\"";
        return MessageFormat.format(pattern, productCode, quantity, discount,
                unitPrice);
    }

}
