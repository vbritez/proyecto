package com.tigo.cs.android.helper.domain;

public class ServiceOperationDataEntity extends BaseEntity {

    private Long codServiceOperation;
    private String servicemsg;
    private Integer pos;

    public ServiceOperationDataEntity() {
    }

    public Long getCodServiceOperation() {
        return codServiceOperation;
    }

    public void setCodServiceOperation(Long codServiceOperation) {
        this.codServiceOperation = codServiceOperation;
    }

    public String getServicemsg() {
        return servicemsg;
    }

    public void setServicemsg(String servicemsg) {
        this.servicemsg = servicemsg;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
            * result
            + ((codServiceOperation == null) ? 0 : codServiceOperation.hashCode());
        result = prime * result
            + ((servicemsg == null) ? 0 : servicemsg.hashCode());
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
        ServiceOperationDataEntity other = (ServiceOperationDataEntity) obj;
        if (codServiceOperation == null) {
            if (other.codServiceOperation != null)
                return false;
        } else if (!codServiceOperation.equals(other.codServiceOperation))
            return false;
        if (servicemsg == null) {
            if (other.servicemsg != null)
                return false;
        } else if (!servicemsg.equals(other.servicemsg))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
