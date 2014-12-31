package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TigoMoneyPhotoService extends BaseServiceBean {

    private static final long serialVersionUID = 1073292207301903393L;

    private byte[] frontPhoto;
    private byte[] backPhoto;

    public TigoMoneyPhotoService() {
        super();
        setServiceCod(-3);
    }    

	public byte[] getFrontPhoto() {
		return frontPhoto;
	}

	public void setFrontPhoto(byte[] frontPhoto) {
		this.frontPhoto = frontPhoto;
	}

	public byte[] getBackPhoto() {
		return backPhoto;
	}

	public void setBackPhoto(byte[] backPhoto) {
		this.backPhoto = backPhoto;
	}	

	@Override
    public String toString() {
        return super.toString();
    }

}
