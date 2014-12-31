package com.tigo.cs.android.asynctask.response;

import android.os.AsyncTask;

import com.tigo.cs.api.entities.BaseServiceBean;

public abstract class AbstractAsyncTask<T extends BaseServiceBean> extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        execute();
        return null;
    }

    protected abstract void execute();

    public abstract T getResponseEntity();

    public abstract void setResponseEntity(T entity);

}
