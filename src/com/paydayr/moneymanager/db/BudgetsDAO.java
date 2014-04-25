package com.paydayr.moneymanager.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.helper.DataHelper;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;

public class BudgetsDAO {
	
	private Context context;
	// Database fields
	private SQLiteDatabase database;
	private DataHelper dbHelper;
	private SimpleDateFormat sdf;
	private String[] allBudgetColumns = { Constants.COLUMN_ID,
			"name",
			"total",
			"ammount",
			"category_id",
			"subcategory_id",
			"dt_start",
			"renew_id",
			"latitude",
			"longitude",
			"raio"};

	public BudgetsDAO(Context context) {
		dbHelper = new DataHelper(context);
		this.context = context;
		sdf = new SimpleDateFormat(Constants.DATE_PATTERN);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Budget createBudget(Budget budget)
			throws BusinessException {
		open();
		// CAMPOS OBRIGATORIOS
		if ( budget.getTotal() == 0 || budget.getCategory() == null ) {
			throw new BusinessException("The value of the budget and category are required.");
		}

		ContentValues values = new ContentValues();
		values.put("name", budget.getName());
		values.put("total", budget.getTotal());
		values.put("ammount", budget.getAmmount());
		values.put("category_id", budget.getCategory().getId());
		values.put("subcategory_id", budget.getCategory().getSubCategories() != null && budget.getCategory().getSubCategories().size() > 0 ? budget.getCategory().getSubCategories().get(0).getId() : null);
		values.put("dt_start", sdf.format(budget.getDtStart()));
		values.put("renew_id", budget.getRenewId());
		values.put("latitude", budget.getLatitude());
		values.put("longitude", budget.getLongitude());
		values.put("raio", budget.getRaio());

		long insertId = database.insert(Constants.TABLE_BUDGETS, null, values);
		Cursor cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, Constants.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		budget = cursorToBudget(cursor, budget.getCategory());
		cursor.close();
		close();
		return budget;
	}

	public void deleteBudget(Budget budget) {
		open();
		long id = budget.getId();
		System.out.println("Budget deleted with id: " + id);
		database.delete(Constants.TABLE_BUDGETS, Constants.COLUMN_ID + " = "
				+ id, null);
		close();
	}

	public void deleteBudgetsByCategory(Category category) {
		open();
		System.out.println("Budgets deleted by category id: " + category.getId());
		database.delete(Constants.TABLE_BUDGETS, "category_id = " + category.getId(), null);
		close();
	}
	
	public void updateBudget(Budget budget){
		open();
		ContentValues values = new ContentValues();
		values.put("ammount", budget.getAmmount());
		database.update(Constants.TABLE_BUDGETS, values, Constants.COLUMN_ID + " = " + budget.getId(), null);
		close();
	}

	public Budget getBudgetById(long id) throws BusinessException {
		open();
		Cursor cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, Constants.COLUMN_ID + " = " + id, null, null,
				null, null);
		cursor.moveToFirst();
		if( cursor.getCount() == 0 ){
			return null;
		}
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		Category category = categoriesDao.getCategoryById(cursor.getLong(4));
		Budget budget = cursorToBudget(cursor, category);
		cursor.close();
		close();
		return budget;
	}
	
	public Budget getBudgetByName(String name) throws BusinessException {
		open();
		Cursor cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, "name = '" + name + "'", null, null, null, null);
		Budget budget = null;
		cursor.moveToFirst();
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		Category category = categoriesDao.getCategoryById(cursor.getLong(4));
		if( cursor.getCount() > 0 ){
			budget = cursorToBudget(cursor, category);
		}
		cursor.close();
		close();
		return budget;
	}
	
	public List<Budget> getBudgetsByCategory(Category category) throws BusinessException {
		open();
		List<Budget> budgets = new ArrayList<Budget>();
		Cursor cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, "category_id = " + category.getId(), null, null, null, null);
		Budget budget = null;
		cursor.moveToFirst();
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		while (!cursor.isAfterLast()) {
			category = categoriesDao.getCategoryById(cursor.getLong(4));
			if( category == null ){
				budget = new Budget();
				budget.setId(cursor.getLong(0));
				deleteBudget(budget);
			} else{
				budget = cursorToBudget(cursor, category);
				budgets.add(budget);
				cursor.moveToNext();
			}
		}
		cursor.close();
		close();
		return budgets;
	}
	
	public List<Budget> getBudgetsToRenew(String dataInicio, int renewId) throws BusinessException {
		open();
		List<Budget> budgets = new ArrayList<Budget>();
		Cursor cursor = null;
		cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, "dt_start < datetime('" + dataInicio + "') AND renew_id = " + renewId, null, null, null, null);

		Budget budget = null;
		Category category = null;
		cursor.moveToFirst();
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		while (!cursor.isAfterLast()) {
			category = categoriesDao.getCategoryById(cursor.getLong(4));
			if( category == null ){
				budget = new Budget();
				budget.setId(cursor.getLong(0));
				deleteBudget(budget);
			} else{
				budget = cursorToBudget(cursor, category);
				budgets.add(budget);
				cursor.moveToNext();
			}
		}
		cursor.close();
		close();
		return budgets;
	}
	
	public List<Budget> getAllBudgets() throws BusinessException {
		open();
		List<Budget> budgets = new ArrayList<Budget>();
		Cursor cursor = database.query(Constants.TABLE_BUDGETS,
				allBudgetColumns, null, null, null, null, null);

		cursor.moveToFirst();
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		Category category = null;
		while (!cursor.isAfterLast()) {
			category = categoriesDao.getCategoryById(cursor.getLong(4));
			if( category == null ){
				budget = new Budget();
				budget.setId(cursor.getLong(0));
				deleteBudget(budget);
			} else{
				budget = cursorToBudget(cursor, category);
				budgets.add(budget);
				cursor.moveToNext();
			}
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return budgets;
	}

	private Budget cursorToBudget(Cursor cursor, Category category) throws BusinessException {
		if( category == null ){
			throw new BusinessException("Failure to retrieve budget. Category null.");
		}
		Budget budget = new Budget();
		budget.setId(cursor.getLong(0));
		budget.setName(cursor.getString(1));
		budget.setTotal(cursor.getDouble(2));
		budget.setAmmount(cursor.getDouble(3));
		budget.setCategory(category);
		Long subCategoryId = !cursor.isNull(5) ? cursor.getLong(5) : null;
		if( subCategoryId != null ){
			SubCategory subCategory = null;
			for( SubCategory subCat : category.getSubCategories() ){
				if( subCat.getId() == subCategoryId ){
					subCategory = subCat;
				}
			}	
			List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
			if( subCategory != null ) {
				subCategoryList.add(subCategory);	
			}
			category.setSubCategories(subCategoryList);
		} else{
			//REMOVE TODAS AS SUBCATEGORIAS
			List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
			category.setSubCategories(subCategoryList);
		}
		try {
			budget.setDtStart(sdf.parse(cursor.getString(6)));
		} catch (ParseException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			throw new BusinessException("Parser error when retrieving the start date of budget. Contact the system administrator.");
		}
		if( !cursor.isNull(7) ){
			budget.setRenewId(cursor.getInt(7));
		}
		if( !cursor.isNull(8) ){
			budget.setLatitude(cursor.getDouble(8));
		}
		if( !cursor.isNull(9) ){
			budget.setLongitude(cursor.getDouble(9));
		}
		if( !cursor.isNull(10) ){
			budget.setRaio(cursor.getInt(10));
		}
		
		return budget;
	}

}