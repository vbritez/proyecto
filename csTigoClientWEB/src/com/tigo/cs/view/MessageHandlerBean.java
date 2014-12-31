package com.tigo.cs.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.tigo.cs.commons.web.view.AbstractMessageHandlerBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "messageHandlerBean")
@RequestScoped
public class MessageHandlerBean extends AbstractMessageHandlerBean {

    private static final long serialVersionUID = 8941797173715450831L;

    public static MessageHandlerBean getInstance() {
        return (MessageHandlerBean) getBean("messageHandlerBean");
    }
}
