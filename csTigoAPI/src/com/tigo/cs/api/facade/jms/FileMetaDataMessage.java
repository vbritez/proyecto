package com.tigo.cs.api.facade.jms;

import java.io.Serializable;
import java.util.Arrays;

import com.tigo.cs.domain.Application;

public class FileMetaDataMessage implements Serializable {

    private static final long serialVersionUID = -1045178884439152341L;

    private byte[] bytes;
    private Long metaCod;
    private Long clientCod;
    private String userwebCellphoneNum;
    private Application application;
    private Boolean processUserphone;

    public FileMetaDataMessage() {

    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Long getMetaCod() {
        return metaCod;
    }

    public void setMetaCod(Long metaCod) {
        this.metaCod = metaCod;
    }

    public Long getClientCod() {
        return clientCod;
    }

    public void setClientCod(Long clientCod) {
        this.clientCod = clientCod;
    }

    public String getUserwebCellphoneNum() {
        return userwebCellphoneNum;
    }

    public void setUserwebCellphoneNum(String userwebCellphoneNum) {
        this.userwebCellphoneNum = userwebCellphoneNum;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Boolean getProcessUserphone() {
        return processUserphone;
    }

    public void setProcessUserphone(Boolean processUserphone) {
        this.processUserphone = processUserphone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        result = prime * result
            + ((clientCod == null) ? 0 : clientCod.hashCode());
        result = prime * result + ((metaCod == null) ? 0 : metaCod.hashCode());

        result = prime * result
            + ((processUserphone == null) ? 0 : processUserphone.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileMetaDataMessage other = (FileMetaDataMessage) obj;
        if (!Arrays.equals(bytes, other.bytes))
            return false;
        if (clientCod == null) {
            if (other.clientCod != null)
                return false;
        } else if (!clientCod.equals(other.clientCod))
            return false;
        if (metaCod == null) {
            if (other.metaCod != null)
                return false;
        } else if (!metaCod.equals(other.metaCod))
            return false;

        if (processUserphone == null) {
            if (other.processUserphone != null)
                return false;
        } else if (!processUserphone.equals(other.processUserphone))
            return false;
        return true;
    }

}
