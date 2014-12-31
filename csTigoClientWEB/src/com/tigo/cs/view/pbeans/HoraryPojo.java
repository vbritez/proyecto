package com.tigo.cs.view.pbeans;

import java.io.Serializable;
import java.util.Date;

public class HoraryPojo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6552638840114141987L;

    private Date startTime;
    private Date endTime;
    private String description;
    private String code;
    private boolean checked = false;
   
    public HoraryPojo( Date startTime,
            Date endTime,String description,String code) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.setDescription(description);
        this.setCode(code);
    }

    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    
    

}
