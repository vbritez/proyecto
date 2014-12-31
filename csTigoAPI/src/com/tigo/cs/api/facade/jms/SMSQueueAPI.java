package com.tigo.cs.api.facade.jms;

import java.util.List;

import com.tigo.cs.domain.Application;

public abstract class SMSQueueAPI extends AbstractQueueAPI<SMSJmsBean> {

    private static final long serialVersionUID = 6030222110589880404L;

    @Override
    protected String getConnectionFactory() {
        return "jms/SMSQueueConnectionFactory";
    }

    @Override
    protected String getDestinyResource() {
        return "jms/SMSQueue";
    }

    public void sendToJMS(String cellphoneNum, Application application, String message) {

        SMSJmsBean smsJmsBean = new SMSJmsBean();
        smsJmsBean.setApplication(application);
        smsJmsBean.setCellphoneNum(cellphoneNum);
        smsJmsBean.setMessage(message);
        sendToJMS(smsJmsBean);
    }

    public void sendToJMS(List<String> cellphoneNumList, Application application, String message) {

        SMSJmsBean smsJmsBean = new SMSJmsBean();
        smsJmsBean.setApplication(application);
        smsJmsBean.setCellphoneNumList(cellphoneNumList);
        smsJmsBean.setMessage(message);
        sendToJMS(smsJmsBean);
    }

}
