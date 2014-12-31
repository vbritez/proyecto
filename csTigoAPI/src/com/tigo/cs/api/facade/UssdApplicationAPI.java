package com.tigo.cs.api.facade;

import com.tigo.cs.domain.ussd.UssdApplication;

public abstract class UssdApplicationAPI extends AbstractAPI<UssdApplication> {

    public UssdApplicationAPI() {
        super(UssdApplication.class);
    }
}
