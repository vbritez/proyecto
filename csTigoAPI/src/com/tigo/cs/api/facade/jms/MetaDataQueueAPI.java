package com.tigo.cs.api.facade.jms;

public abstract class MetaDataQueueAPI extends AbstractQueueAPI<MetaDataMessage> {

    private static final long serialVersionUID = 319384360629160487L;

    @Override
    protected String getConnectionFactory() {
        return "jms/MetaDataQueueConnectionFactory";
    }

    @Override
    protected String getDestinyResource() {
        return "jms/MetaDataQueue";
    }

}
