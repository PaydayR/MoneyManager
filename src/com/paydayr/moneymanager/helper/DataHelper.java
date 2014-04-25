package com.paydayr.moneymanager.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.paydayr.moneymanager.util.Constants;

public class DataHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "moneymanager.db";
	// DEIXAR BD NUMERO 13 PARA COMPATIBILIDADE COM USUARIOS, SOMENTE EM CASO DE ADICAO DE CAMPO SOMAR VERSAO MAS 
	// NOTIFICAR QUE BASE DE DADOS SERA PERDIDA ENTAO SOMENTE FAZER ISTO QUANDO OPERACAO DE BACKUP ESTIVER IMPLEMENTADA.
	private static final int DATABASE_VERSION = 13;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(Constants.DDL_CREATE_EXPENSES);
		database.execSQL(Constants.DDL_CREATE_CATEGORIES);
		database.execSQL(Constants.DDL_CREATE_SUBCATEGORIES);
		database.execSQL(Constants.DDL_CREATE_BUDGETS);
		database.execSQL(Constants.DDL_CREATE_ACCOUNTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DataHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_EXPENSES);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SUBCATEGORIES);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_BUDGETS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ACCOUNTS);
		onCreate(db);
	}

}