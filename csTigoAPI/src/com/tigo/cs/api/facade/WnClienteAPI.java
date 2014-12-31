package com.tigo.cs.api.facade;

import com.tigo.cs.domain.WnCliente;

public class WnClienteAPI extends AbstractAPI<WnCliente> {

    protected WnClienteAPI() {
        super(WnCliente.class);
    }

    public Boolean deleteAll() throws Exception {
        finderParams = prepareParams();
        return executeUpdateWithNamedQuery("WnCliente.deleteAll", finderParams) >= 0 ? true : false;
    }

}
