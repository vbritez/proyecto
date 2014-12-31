package com.tigo.cs.api.facade;

import java.util.List;

import com.tigo.cs.domain.WnOperadora;

public class WnOperadoraAPI extends AbstractAPI<WnOperadora> {

    public WnOperadoraAPI() {
        super(WnOperadora.class);
    }

    public List<WnOperadora> findAllEnabled() {
        return findListWithNamedQuery("WnOperadora.findAllEnabled");
    }

}
