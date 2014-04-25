package com.paydayr.moneymanager.util;

import java.util.Locale;

public class Constants {
	
	//CONFIGURACOES GERAIS
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String DATETIME_SCREEN_PATTERN = "dd/MM/yy HH:mm";
	public static final String DATE_SCREEN_PATTERN = "dd/MM/yy";
	public static final String PERIODO = "PERIODO";
	public static final String CATEGORIES_EXPANDED = "CATEGORIES_EXPANDED";
	public static final String BUDGETS_EXPANDED = "BUDGETS_EXPANDED";
	public static final String EXPENSES_EDIT_LIST = "EXPENSES_EDIT_LIST";
	
	//DIALOGOS
	public static final int DIALOG_ADD_CAT_EMPTY = 1;
	public static final int DIALOG_ADD_SUBCAT_EMPTY = 2;
	public static final int DIALOG_ADD_BUDGET_EMPTY = 3;
	public static final int DIALOG_ADD_EXPENSE_EMPTY = 4;
	public static final int DIALOG_CONFIRM_DEL = 5;
	public static final int DIALOG_CONFIRM_DEL_RELATIONS = 6;
		
	// Database creation sql statement
	public static final String TABLE_EXPENSES = "expenses";
	public static final String TABLE_CATEGORIES = "categories";
	public static final String TABLE_SUBCATEGORIES = "sub_categories";
	public static final String TABLE_BUDGETS = "budgets";
	public static final String TABLE_ACCOUNTS = "accounts";
	public static final String COLUMN_ID = "_id";
	
	//PATTERNS PARA RECONHECIMENTO SMS
	public static final String PATTERN_VALOR = "(\\d+[,|.]\\d+[,.]?\\d*)";
	public static final String PATTERN_DATA = "\\d{2}/\\d{2}/\\d{2,4}";
//	public static final String PATTERN_ESTABELECIMENTO = "[A-Z\\s]+[0-9]*(.)?$";
	public static final String PATTERN_ESTABELECIMENTO = "([A-Z\\s]+[0-9]*(.)?$)|([A-Z\\s]+(?= valor))";	
	public static final String PATTERN_HORA = "([0-1][0-9]|[2][0-3])([:h]([0-5][0-9])){1,2}";
	
	//MASKS
	public static final String CURRENCY = "USD";
	public static final Locale LOCAL = new Locale("en","US");
	
	public static final String DDL_CREATE_EXPENSES = 
			"create table " + TABLE_EXPENSES + "(" + COLUMN_ID + " integer primary key autoincrement, " +
			"	local text, " + 
			"	ammount number not null," +
			"	budget_id number, " +
			"	category_id number, " +
			"	subcategory_id number, " +
			" 	dtOcurr datetime not null," +
			"	renew_id integer," +
			"	renewed integer default 0, " +
			"	latitude number," +
			"	longitude number," +
			"	account_id integer);";
	
	public static final String DDL_CREATE_CATEGORIES = 
			"create table " + TABLE_CATEGORIES + "(" + COLUMN_ID + " integer primary key autoincrement, " + 
			"	category_name text not null);";
	
	public static final String DDL_CREATE_SUBCATEGORIES = 
			"create table " + TABLE_SUBCATEGORIES + "(" + COLUMN_ID + " integer primary key autoincrement, " + 
			"	subcategory_name text not null," +
			"	category_id number not null);";
	
	public static final String DDL_CREATE_BUDGETS = 
			"create table " + TABLE_BUDGETS + "(" + COLUMN_ID + " integer primary key autoincrement, " +
			"	name text not null, " +
			"	total number not null, " + 
			"	ammount number not null, " +
			"	category_id number not null, " +
			"	subcategory_id number," +
			" 	dt_start date not null, " +
			"	renew_id integer," +
			"	latitude number," +
			"	longitude number," +
			"	raio integer," +
			"	account_id integer);";
	
	public static final String DDL_CREATE_ACCOUNTS =
			"CREATE TABLE " + TABLE_ACCOUNTS + "(" + COLUMN_ID + " integer primary key autoincrement, " +
			"	name text not null," +
			"	ammount number not null," +
			"	dt_creation date not null," +
			"	dt_expiration date," +
			"	renew_id integer);";
}
