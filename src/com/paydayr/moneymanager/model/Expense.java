package com.paydayr.moneymanager.model;

import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	private String local;
	private double ammount;
	private Budget budget;
	private Category category;
	private Date dtOcurr;
	private int renewId;
	private boolean renewed;
	private Double latitude;
	private Double longitude;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public double getAmmount() {
		return ammount;
	}
	public void setAmmount(double ammount) {
		this.ammount = ammount;
	}
	public Budget getBudget() {
		return budget;
	}
	public void setBudget(Budget budget) {
		this.budget = budget;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Date getDtOcurr() {
		return dtOcurr;
	}
	public void setDtOcurr(Date dtOcurr) {
		this.dtOcurr = dtOcurr;
	}
	public int getRenewId() {
		return renewId;
	}
	public void setRenewId(int renewId) {
		this.renewId = renewId;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public boolean isRenewed() {
		return renewed;
	}
	public void setRenewed(boolean renewed) {
		this.renewed = renewed;
	}
			
}
