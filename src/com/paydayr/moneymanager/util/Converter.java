package com.paydayr.moneymanager.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;

public class Converter {
  	
	public static DecimalFormat getDecimalFormat(){
		Currency currency = Currency.getInstance(Constants.CURRENCY);
		return new DecimalFormat(currency.getSymbol() + "#,##0.00", new DecimalFormatSymbols(Constants.LOCAL));
	}
}
