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
import android.widget.Toast;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.helper.DataHelper;
import com.paydayr.moneymanager.holders.TotalByMonthHolder;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;

public class ExpensesDAO{
	
	private Context context;
	// Database fields
	private SQLiteDatabase database;
	private DataHelper dbHelper;
	private SimpleDateFormat sdf;
	private String[] allExpensesColumns = { Constants.COLUMN_ID,
			"local",
			"ammount",
			"budget_id",
			"category_id",
			"subcategory_id",
			"dtOcurr",
			"renew_id",
			"renewed",
			"latitude",
			"longitude"};

	public ExpensesDAO(Context context) {
		dbHelper = new DataHelper(context);
		this.context = context;
		sdf = new SimpleDateFormat(Constants.DATETIME_PATTERN);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Expense createExpense(Expense expense)
			throws BusinessException {
		open();
		// CAMPOS OBRIGATORIOS
		if ( expense.getAmmount() == 0 || (expense.getCategory() == null && expense.getBudget() == null) ) {
			throw new BusinessException("The amount of the expense, or budget category are required.");
		}

		ContentValues values = new ContentValues();
		values.put("local", expense.getLocal());
		values.put("ammount", expense.getAmmount());
		values.put("budget_id", expense.getBudget() != null ? expense.getBudget().getId() : null);
		values.put("category_id", expense.getCategory() != null ? expense.getCategory().getId() : null);
		if( expense.getCategory() != null ){
			values.put("subcategory_id", expense.getCategory().getSubCategories() != null && expense.getCategory().getSubCategories().size() > 0 ? expense.getCategory().getSubCategories().get(0).getId() : null);	
		}
		values.put("dtOcurr", sdf.format(expense.getDtOcurr()));
		values.put("renew_id", expense.getRenewId());
		values.put("latitude", expense.getLatitude());
		values.put("longitude", expense.getLongitude());
		
		long insertId = database.insert(Constants.TABLE_EXPENSES, null, values);
		Cursor cursor = database.query(Constants.TABLE_EXPENSES,
				allExpensesColumns, Constants.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		expense = cursorToExpense(cursor, expense.getCategory(), expense.getBudget());
		cursor.close();
		close();
		return expense;
	}
	
	public void updateExpense(Expense expense)
			throws BusinessException {
		open();
		// CAMPOS OBRIGATORIOS
//		if ( expense.getAmmount() == 0 || (expense.getCategory() == null && expense.getBudget() == null) ) {
//			throw new BusinessException("The amount of the expense, or budget category are required.");
//		}

		ContentValues values = new ContentValues();
		values.put("budget_id", expense.getBudget() != null ? expense.getBudget().getId() : null);
		values.put("category_id", expense.getCategory() != null ? expense.getCategory().getId() : 0);
		values.put("renew_id", expense.getRenewId());
		values.put("renewed", expense.isRenewed() ? 1 : 0);
		if( expense.getCategory() != null ){
			values.put("subcategory_id", expense.getCategory().getSubCategories() != null && expense.getCategory().getSubCategories().size() > 0 ? expense.getCategory().getSubCategories().get(0).getId() : null);	
		} else{
			values.put("subcategory_id", 0);
		}

		database.update(Constants.TABLE_EXPENSES, values, Constants.COLUMN_ID + " = " + expense.getId(), null);
		close();
	}

	public void deleteExpense(Expense expense) {
		open();
		long id = expense.getId();
		System.out.println("Expense deleted with id: " + id);
		database.delete(Constants.TABLE_EXPENSES, Constants.COLUMN_ID + " = "
				+ id, null);
		close();
	}

	public void deleteExpensesByCategory(Category category) {
		open();
		System.out.println("Expenses deleted by category id: " + category.getId());
		database.delete(Constants.TABLE_EXPENSES, "category_id = " + category.getId(), null);
		close();
	}
	
	public void deleteExpensesByBudget(Budget budget) {
		open();
		System.out.println("Expenses deleted by budget id: " + budget.getId());
		database.delete(Constants.TABLE_EXPENSES, "budget_id = " + budget.getId(), null);
		close();
	}
	
	public Expense getExpenseById(long id) throws BusinessException {
		open();
		Cursor cursor = database.query(Constants.TABLE_EXPENSES,
				allExpensesColumns, Constants.COLUMN_ID + " = " + id, null, null,
				null, null);
		cursor.moveToFirst();
		Category category = null;
		Budget budget = null;
		if( cursor != null && !cursor.isNull(4) && cursor.getLong(4) > 0 ){
			CategoriesDAO categoriesDao = new CategoriesDAO(context);
			category = categoriesDao.getCategoryById(cursor.getLong(4));	
		} 
		if( cursor != null && !cursor.isNull(3) && cursor.getLong(3) > 0 ){
			BudgetsDAO budgetDao = new BudgetsDAO(context);
			budget = budgetDao.getBudgetById(cursor.getLong(3));
		}
		
		Expense expense = cursorToExpense(cursor, category, budget);
		cursor.close();
		close();
		return expense;
	}
	
	public List<Expense> getExpenseByLocal(String local) throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = database.query(Constants.TABLE_EXPENSES,
				allExpensesColumns, "local = '" + local + "'", null, null,
				null, null);
		cursor.moveToFirst();
		Category category = null;
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			} 
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}

	public List<Expense> getExpenseByCategory(Category category, String dataInicio, String dataFim) throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = null;
		if( dataFim != null ){
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "category_id = " + category.getId() + " and dtOcurr between datetime('" + dataInicio + "') and datetime('" + dataFim + "')", null, null, null, null);	
		} else{
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "category_id = " + category.getId() + " and dtOcurr >= datetime('" + dataInicio + "')", null, null, null, null);
		}		
		cursor.moveToFirst();
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			} 
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}
	
	public List<Expense> getExpenseByBudget(Budget budget, String dataInicio, String dataFim) throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = null;
		if( dataFim != null ){
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "budget_id = " + budget.getId() + " and dtOcurr between datetime('" + dataInicio + "') and datetime('" + dataFim + "')", null, null, null, null);	
		} else{
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "budget_id = " + budget.getId() + " and dtOcurr >= datetime('" + dataInicio + "')", null, null, null, null);
		}		
		cursor.moveToFirst();
		Category category = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			}
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}
	
	public List<Expense> getAllExpenses() throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = database.query(Constants.TABLE_EXPENSES,
				allExpensesColumns, null, null, null, null, null);

		cursor.moveToFirst();
		Category category = null;
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			}
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}
	
	public List<Expense> getExpensesByPeriod(String dataInicio, String dataFim) throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = null;
		if( dataFim != null ){
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "dtOcurr between datetime('" + dataInicio + "') and datetime('" + dataFim + "')", null, null, null, null);	
		} else{
			cursor = database.query(Constants.TABLE_EXPENSES,
					allExpensesColumns, "dtOcurr >= datetime('" + dataInicio + "')", null, null, null, null);
		}		

		cursor.moveToFirst();
		Category category = null;
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			} 
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}
	
	public List<Expense> getExpensesToRenew(String dataInicio, int renewId) throws BusinessException {
		open();
		List<Expense> expenses = new ArrayList<Expense>();
		Cursor cursor = null;
		cursor = database.query(Constants.TABLE_EXPENSES,
				allExpensesColumns, 
				"dtOcurr < datetime('" + dataInicio + "') AND " +
				"renew_id = " + renewId + " AND " +
				"renewed = 0", null, null, null, null);

		cursor.moveToFirst();
		Category category = null;
		Budget budget = null;
		CategoriesDAO categoriesDao = new CategoriesDAO(context);
		BudgetsDAO budgetDao = new BudgetsDAO(context);
		
		while (!cursor.isAfterLast()) {
			if( !cursor.isNull(4) && cursor.getLong(4) > 0 ){				
				category = categoriesDao.getCategoryById(cursor.getLong(4));	
			} 
			if( !cursor.isNull(3) && cursor.getLong(3) > 0 ){
				budget = budgetDao.getBudgetById(cursor.getLong(3));
			}
			expenses.add(cursorToExpense(cursor, category, budget));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return expenses;
	}
	
	public List<TotalByMonthHolder> getExpensesTotalMonthly(String dataInicio) throws BusinessException {
		open();
		List<TotalByMonthHolder> values = new ArrayList<TotalByMonthHolder>();
		Cursor cursor = null;
		
		String query =  "SELECT " + 
						"	SUM(ammount) as total_mes, " +
						"	strftime('%m', dtOcurr) as mes " +
						"FROM " + Constants.TABLE_EXPENSES + " " +
						"WHERE dtOcurr >= datetime('" + dataInicio + "') " +
						"GROUP BY strftime('%m', dtOcurr)";
		
		cursor = database.rawQuery(query, null);
		
		cursor.moveToFirst();
		TotalByMonthHolder holder = null;
		while (!cursor.isAfterLast()) {
			holder = new TotalByMonthHolder(Integer.parseInt(cursor.getString(1)), cursor.getDouble(0));
			values.add(holder);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return values;
	}

	private Expense cursorToExpense(Cursor cursor, Category category, Budget budget) throws BusinessException {
		
//		if( category == null && budget == null ){
//			throw new BusinessException("Failed to retrieve Expense. Budget and Category null.");
//		}
		
		Expense expense = new Expense();
		expense.setId(cursor.getLong(0));	
		expense.setLocal(!cursor.isNull(1) ? cursor.getString(1) : null);
		expense.setAmmount(cursor.getDouble(2));
		if( !cursor.isNull(3) && cursor.getLong(3) != 0 ){
			expense.setBudget(budget);	
		}
		if( !cursor.isNull(4) && cursor.getLong(4) != 0 ){
			expense.setCategory(category);	
		}
		Long subCategoryId = !cursor.isNull(5) ? cursor.getLong(5) : null;
		if( subCategoryId != null && subCategoryId != 0 ){
			if( category == null ){
				throw new BusinessException("Error - ExpensesDAO. Category null.");
			}
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
		} else if( category != null ){
			//REMOVE TODAS AS SUBCATEGORIAS
			List<SubCategory> subCategoryList = new ArrayList<SubCategory>();
			category.setSubCategories(subCategoryList);
		}
		try {
			expense.setDtOcurr(sdf.parse(cursor.getString(6)));
		} catch (ParseException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			Toast.makeText(context, "Error retrieving the date of the expense. Contact the system administrator.", Toast.LENGTH_LONG).show();
		}
		if( !cursor.isNull(7) ){
			expense.setRenewId(cursor.getInt(7));
		}
		if( !cursor.isNull(8) ){
			expense.setRenewed(cursor.getInt(8) == 1 ? true : false);
		}
		if( !cursor.isNull(9) ){
			expense.setLatitude(cursor.getDouble(9));
		}
		if( !cursor.isNull(10) ){
			expense.setLongitude(cursor.getDouble(10));
		}
		
		return expense;
	}

}