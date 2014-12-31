package com.tigo.cs.view.service;

import java.util.Date;

import com.tigo.cs.commons.util.DateUtil;

public class ConfigurationData {
	private String dayOfWeekString;
	private Boolean checked = false;
	private Integer dayOfWeek;
	private Date startTime;
	private Date duration;
	private Long interval;
	private Long tolerance;
	private Boolean added = false;
	private String days;

	public ConfigurationData() {
	}

	public ConfigurationData(String dayOfWeekString, Integer dayOfWeek, Date startTime, Date duration,
			Long interval, Long tolerance, Boolean added, Boolean checked) {
		this.dayOfWeekString = dayOfWeekString;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.duration = duration;
		this.interval = interval;
		this.tolerance = tolerance;
		this.added = false;
		this.checked = checked;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getDuration() {
		return duration;
	}

	public void setDuration(Date duration) {
		this.duration = duration;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public Long getTolerance() {
		return tolerance;
	}

	public void setTolerance(Long tolerance) {
		this.tolerance = tolerance;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getDayOfWeekString() {
		return dayOfWeekString;
	}

	public void setDayOfWeekString(String dayOfWeekString) {
		this.dayOfWeekString = dayOfWeekString;
	}	
	
	
	public Boolean getAdded() {
		return added;
	}

	public void setAdded(Boolean added) {
		this.added = added;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public boolean equals(ConfigurationData other){
		if (this.getTolerance() == other.getTolerance() && 
				DateUtil.getFormattedDate(this.getDuration(), "HH:mm").equals(DateUtil.getFormattedDate(other.getDuration(), "HH:mm")) &&
				this.getInterval() == other.getInterval() &&
				DateUtil.getFormattedDate(this.getStartTime(), "HH:mm").equals(DateUtil.getFormattedDate(other.getStartTime(), "HH:mm")))
			return true;
		else
			return false;
		
	}

}