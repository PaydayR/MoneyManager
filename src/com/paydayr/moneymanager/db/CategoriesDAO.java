package com.paydayr.moneymanager.db;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.paydayr.moneymanager.helper.DataHelper;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;

public class CategoriesDAO {

	// Database fields
	private SQLiteDatabase database;
	private DataHelper dbHelper;
	private String[] allCategoryColumns = { Constants.COLUMN_ID,
		"category_name" };
	private String[] allSubCategoryColumns = { Constants.COLUMN_ID,
		"subcategory_name", 
		"category_id"};
	
	public CategoriesDAO(Context context) {
		dbHelper = new DataHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Category createCategory(String category) {
		open();
		ContentValues values = new ContentValues();
		values.put("category_name", category);
		long insertId = database.insert(Constants.TABLE_CATEGORIES, null, values);
		Cursor cursor = database.query(Constants.TABLE_CATEGORIES,
				allCategoryColumns, Constants.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Category newCategory = cursorToCategory(cursor);
		cursor.close();
		close();
		return newCategory;
	}
	
	public SubCategory addSubCategory(Category category, String subCategory){
		open();
		ContentValues values = new ContentValues();
		values.put("subcategory_name", subCategory);
		values.put("category_id", category.getId());
		long insertId = database.insert(Constants.TABLE_SUBCATEGORIES, null, values);
		Cursor cursor = database.query(Constants.TABLE_SUBCATEGORIES,
				allSubCategoryColumns, Constants.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		SubCategory newSubCategory = cursorToSubCategory(category, cursor);
		cursor.close();
		close();
		return newSubCategory; 
	}

	public void deleteCategory(Category category) {
		open();
		long id = category.getId();
		System.out.println("Category deleted with id: " + id);
		database.delete(Constants.TABLE_CATEGORIES, Constants.COLUMN_ID
				+ " = " + id, null);
		close();
	}
	
	public Category getCategoryById(long id) {
		open();
		Cursor cursor = database.query(Constants.TABLE_CATEGORIES,
				allCategoryColumns, Constants.COLUMN_ID + " = " + id, null, null, null, null);
		Category category = null;
		cursor.moveToFirst();
		if( cursor.getCount() > 0 ){
			category = cursorToCategory(cursor);
		}
		cursor.close();
		close();
		return category;
	}
	
	public Category getCategoryByName(String name) {
		open();
		Cursor cursor = database.query(Constants.TABLE_CATEGORIES,
				allCategoryColumns, "category_name = '" + name + "'", null, null, null, null);
		Category category = null;
		cursor.moveToFirst();
		if( cursor.getCount() > 0 ){
			category = cursorToCategory(cursor);
		}
		cursor.close();
		close();
		return category;
	}
	
	public List<SubCategory> getSubCategoriesByCategory(Category category) {
		open();
		List<SubCategory> subCategories = new ArrayList<SubCategory>();
		Cursor cursor = database.query(Constants.TABLE_SUBCATEGORIES,
				allSubCategoryColumns, "category_id = " + category.getId(), null, null, null, null);
		cursor.moveToFirst();
		SubCategory subCategory = null;
		while( !cursor.isAfterLast() ){
			subCategory = cursorToSubCategory(category, cursor);
			subCategories.add(subCategory);
			cursor.moveToNext();
		}		
		cursor.close();
		close();
		return subCategories;
	}

	public List<Category> getAllCategories() {
		open();
		List<Category> categories = new ArrayList<Category>();
		Cursor cursor = database.query(Constants.TABLE_CATEGORIES,
				allCategoryColumns, null, null, null, null, null);

		cursor.moveToFirst();
		Category category = null;
		while (!cursor.isAfterLast()) {
			category = cursorToCategory(cursor);
			categories.add(category);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return categories;
	}

	private Category cursorToCategory(Cursor cursor) {
		Category category = new Category();
		category.setId(cursor.getLong(0));
		category.setCategoryName(cursor.getString(1));
		List<SubCategory> subCategories = getSubCategoriesByCategory(category);
		if( subCategories != null && subCategories.size() > 0 ){
			category.setSubCategories(subCategories);
		}
		return category;
	}
	
	private SubCategory cursorToSubCategory(Category category, Cursor cursor) {
		SubCategory subCategory = new SubCategory();
		subCategory.setId(cursor.getLong(0));
		subCategory.setSubCategoryName(cursor.getString(1));
		subCategory.setCategory(category);
		return subCategory;
	}
}