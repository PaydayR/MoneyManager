package com.paydayr.moneymanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String categoryName;
	private List<SubCategory> subCategories;
	
	public Category(){
		subCategories = new ArrayList<SubCategory>();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public List<SubCategory> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
	}
	
	@Override
	public String toString() {
		return categoryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((subCategories == null) ? 0 : subCategories.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (id != other.id)
			return false;
		if (subCategories == null) {
			if (other.subCategories != null)
				return false;
		} else if (!subCategories.equals(other.subCategories))
			return false;
		return true;
	}
	
}
