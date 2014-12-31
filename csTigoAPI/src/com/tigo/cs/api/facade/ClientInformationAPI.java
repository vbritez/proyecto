package com.tigo.cs.api.facade;

import com.tigo.cs.domain.ClientInformation;

/**
 *
 * @author Miguel Zorrilla
 * @version $Id$
 */

public abstract class ClientInformationAPI extends AbstractAPI<ClientInformation> {

    public ClientInformationAPI() {
        super(ClientInformation.class);
    }
}
