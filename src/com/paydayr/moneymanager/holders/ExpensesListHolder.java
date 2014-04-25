package com.paydayr.moneymanager.holders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.paydayr.moneymanager.model.Expense;

public class ExpensesListHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8065342703764422019L;
	private List<Expense> expensesList;
	
	public ExpensesListHolder(){
		expensesList = new ArrayList<Expense>();
	}

	public List<Expense> getExpensesList() {
		return expensesList;
	}

	public void setExpensesList(List<Expense> expensesList) {
		this.expensesList = expensesList;
	}
	
}
