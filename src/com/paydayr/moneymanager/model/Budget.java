package com.paydayr.moneymanager.model;

import java.io.Serializable;
import java.util.Date;

public class Budget implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private double ammount;
	private double total;
	private Category category;
//	private SubCategory subCategory;
	private Date dtStart;
	private int renewId;
	private Double latitude;
	private Double longitude;
	private int raio;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getAmmount() {
		return ammount;
	}
	public void setAmmount(double ammount) {
		this.ammount = ammount;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}	
	
//	public SubCategory getSubCategory() {
//		return subCategory;
//	}
//	public void setSubCategory(SubCategory subCategory) {
//		this.subCategory = subCategory;
//	}
	
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
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
	public int getRaio() {
		return raio;
	}
	public void setRaio(int raio) {
		this.raio = raio;
	}
	public Date getDtStart() {
		return dtStart;
	}
	public void setDtStart(Date dtStart) {
		this.dtStart = dtStart;
	}
	
}
