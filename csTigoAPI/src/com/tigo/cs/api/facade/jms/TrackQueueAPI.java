package com.tigo.cs.api.facade.jms;

import com.tigo.cs.api.entities.TrackingService;

public abstract class TrackQueueAPI extends AbstractQueueAPI<TrackingService> {

    private static final long serialVersionUID = -2392886169036426987L;

    @Override
    protected String getConnectionFactory() {
        return "jms/TrackQueueConnectionFactory";
    }

    @Override
    protected String getDestinyResource() {
        return "jms/TrackQueue";
    }

}
