package com.tigo.cs.android.helper.domain;

import java.util.List;

public class ServiceOperationEntity extends BaseEntity {

    private Integer servicecod;
    private String operationname;
    private List<ServiceOperationDataEntity> dataList;

    public ServiceOperationEntity() {
    }

    public Integer getServicecod() {
        return servicecod;
    }

    public void setServicecod(Integer servicecod) {
        this.servicecod = servicecod;
    }

    public String getOperationname() {
        return operationname;
    }

    public void setOperationname(String operationname) {
        this.operationname = operationname;
    }

    public List<ServiceOperationDataEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<ServiceOperationDataEntity> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((operationname == null) ? 0 : operationname.hashCode());
        result = prime * result
            + ((servicecod == null) ? 0 : servicecod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceOperationEntity other = (ServiceOperationEntity) obj;
        if (operationname == null) {
            if (other.operationname != null)
                return false;
        } else if (!operationname.equals(other.operationname))
            return false;
        if (servicecod == null) {
            if (other.servicecod != null)
                return false;
        } else if (!servicecod.equals(other.servicecod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
