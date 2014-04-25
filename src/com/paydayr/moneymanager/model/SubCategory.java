package com.paydayr.moneymanager.model;

import java.io.Serializable;

public class SubCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	private String subCategoryName;
	private Category category;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSubCategoryName() {
		return subCategoryName;
	}
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return subCategoryName;
	}
	
}
