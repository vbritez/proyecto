package com.tigo.cs.api.entities;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ARPEnrollmentService extends ServiceBean {

    private static final long serialVersionUID = -6407390993650595119L;
    private String enrollment;

    public ARPEnrollmentService() {
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public String toString() {
        String pattern = "\"enrollment\":\"{0}\"";
        return MessageFormat.format(pattern, enrollment);
    }

}
