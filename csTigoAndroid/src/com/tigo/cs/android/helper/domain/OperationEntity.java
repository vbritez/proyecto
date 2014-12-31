package com.tigo.cs.android.helper.domain;

public class OperationEntity extends BaseEntity {

    private String operationName;
    private String mcc;
    private String mnc;
    private String production;
    private String shortNumber;
    private String restWsLocation;
    private Integer restWsPort;

    public OperationEntity() {

    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getShortNumber() {
        return shortNumber;
    }

    public void setShortNumber(String shortNumber) {
        this.shortNumber = shortNumber;
    }

    public String getRestWsLocation() {
        return restWsLocation;
    }

    public void setRestWsLocation(String restWsLocation) {
        this.restWsLocation = restWsLocation;
    }

    public Integer getRestWsPort() {
        return restWsPort;
    }

    public void setRestWsPort(Integer restWsPort) {
        this.restWsPort = restWsPort;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mcc == null) ? 0 : mcc.hashCode());
        result = prime * result + ((mnc == null) ? 0 : mnc.hashCode());
        result = prime * result
            + ((production == null) ? 0 : production.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OperationEntity other = (OperationEntity) obj;
        if (mcc == null) {
            if (other.mcc != null) {
                return false;
            }
        } else if (!mcc.equals(other.mcc)) {
            return false;
        }
        if (mnc == null) {
            if (other.mnc != null) {
                return false;
            }
        } else if (!mnc.equals(other.mnc)) {
            return false;
        }
        if (production == null) {
            if (other.production != null) {
                return false;
            }
        } else if (!production.equals(other.production)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
