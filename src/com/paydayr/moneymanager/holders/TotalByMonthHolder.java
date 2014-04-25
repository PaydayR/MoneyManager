package com.paydayr.moneymanager.holders;

import java.io.Serializable;
import java.util.Locale;

public class TotalByMonthHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double total;
	private int mes;
	
	public TotalByMonthHolder(int mes, double total){
		this.mes = mes;
		this.total = total;
	}

	public double getTotal() {
		return Double.parseDouble(String.format(Locale.US, "%.2f", total));
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}
	
}
