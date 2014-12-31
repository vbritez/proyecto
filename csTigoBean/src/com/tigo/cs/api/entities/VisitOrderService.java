package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VisitOrderService extends BaseServiceBean {

    private static final long serialVersionUID = -8358932712945635998L;
    private VisitService visit;
    private OrderService order;

    public VisitOrderService() {
        super();
        setServiceCod(3);
    }

    public VisitService getVisit() {
        return visit;
    }

    public void setVisit(VisitService visit) {
        this.visit = visit;
    }

    public OrderService getOrder() {
        return order;
    }

    public void setOrder(OrderService order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
