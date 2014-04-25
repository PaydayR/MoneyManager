package com.paydayr.moneymanager.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.paydayr.moneymanager.MoneyManager;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.util.Constants;
import com.paydayr.moneymanager.util.RegexpUtil;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "SMSBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Intent recieved: " + intent.getAction());

		if (intent.getAction().equals(SMS_RECEIVED)) {
			
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				if (messages.length > -1) {
					Log.i(TAG, "SMS Message recieved: " + messages[0].getMessageBody());
				}
				String body = null;
				for( SmsMessage sms : messages ){
					body = sms.getMessageBody();
					analiseSms(body);
				}				
			}
		}
	}
	
	private void analiseSms(String text){
		if (RegexpUtil.isMatch(Constants.PATTERN_VALOR, text) &&
				RegexpUtil.isMatch(Constants.PATTERN_DATA, text) &&
				RegexpUtil.isMatch(Constants.PATTERN_ESTABELECIMENTO, text) ){
			
			try {
			
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				SimpleDateFormat sdf = null;
				
				//SE ENCONTROU ENTAO OBTEM VALORES PARA MONTAR BEAN
				String estabelecimento = RegexpUtil.getMatch(Constants.PATTERN_ESTABELECIMENTO, text);
				String data = RegexpUtil.getMatch(Constants.PATTERN_DATA, text);
				String valor = RegexpUtil.getMatch(Constants.PATTERN_VALOR, text);
				String hora = RegexpUtil.getMatch(Constants.PATTERN_HORA, text);
				
				if( estabelecimento != null && data != null && valor != null && hora != null ){
					//PARSER
					//SE TIVER PONTO E VIRGULA RETIRA A VIRGULA, SE TIVER SOH PONTO MANTEM E SE TIVER SOH VIRGULA TROCA POR PONTO
					Double ammount = null;
					if( valor.contains(",") && valor.contains(".") ){
						ammount = Double.parseDouble(valor.trim().replaceAll(",", ""));
					} else if( valor.contains(",") ){
						ammount = Double.parseDouble(valor.trim().replaceAll(",", "."));
					} else {
						ammount = Double.parseDouble(valor.trim());
					}
					
					if( data.trim().length() == 8 ){
						sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
					} else if( data.trim().length() == 10 ){
						sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					}
					Date dtOcurr = null;
					if( hora != null && hora.contains("h") ){
						hora = hora.replace("h", ":");
					}
//					if( hora != null ) {
						dtOcurr = sdf.parse(data.trim() + " " + hora.trim() + ":00");	
//					} else{
//						dtOcurr = sdf.parse(data + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":00");	
//					}								
					
					MoneyManagerFacade facade = new MoneyManagerFacade();
					
					//OBTEM CONTEXTO ESTATICO, CASO NAO EXISTA SERA NECESSARIA AGUARDAR E POSTERIORMENTE SYNCRONIZAR.
					Context context = MoneyManager.getAppContext();
					
					if( context != null ){
						
						//Verifica se existe outros lancamentos para o estabelecimento 
						List<Expense> expenseList = facade.getExpenseListByName(context, estabelecimento);
						
						if( expenseList != null && expenseList.size() > 0 ){
							String budgetName = null;
							String categoryName = null;
							// pega o primeiro que eh o ultimo a ser inserido 
							if( expenseList.get(0).getCategory() != null ){
								categoryName = expenseList.get(expenseList.size()-1).getCategory().getCategoryName();
							}
							if ( expenseList.get(expenseList.size()-1).getBudget() != null ){
								budgetName = expenseList.get(expenseList.size()-1).getBudget().getName();	
							} else{
								// nao tem budget mas tem se tiver categoria e algum budget desta categoria adiciona no budget
								if( categoryName != null ){
									List<Budget> budgets = facade.getBudgetList(context);
									for( Budget budget : budgets ){
										if( budget.getCategory() != null && budget.getCategory().getCategoryName().equals(categoryName) ){
											budgetName = budget.getName();
											break;
										}
										budgetName = "None";
									}
								} else{
									budgetName = "None";
									categoryName = "None";	
								}
							}
							String subCategory = null;
							if( expenseList.get(expenseList.size()-1).getCategory() != null && expenseList.get(expenseList.size()-1).getCategory().getSubCategories() != null && expenseList.get(expenseList.size()-1).getCategory().getSubCategories().size() > 0 ){
								subCategory = expenseList.get(expenseList.size()-1).getCategory().getSubCategories().get(0).getSubCategoryName();
							} else{
								subCategory = "None";
							}							
							facade.addExpense(context, estabelecimento, ammount, budgetName, categoryName, subCategory, null, dtOcurr, 0);
						} else{
							Category category = facade.getCategory(context, "SMS");
							String budgetName = null;
							if( category == null ){
								facade.addExpense(context, estabelecimento, ammount, null, null, null, "SMS", dtOcurr, 0);	
							} else{
								// se existe categoria entao pode existir um budget p/ a categoria SMS. Se existir seta o budget na nova despesa
								List<Budget> budgets = facade.getBudgetList(context);
								for( Budget budget : budgets ){
									if( budget.getCategory() != null && budget.getCategory().getCategoryName().equals("SMS") ){
										budgetName = budget.getName();
										break;
									}
								}
								facade.addExpense(context, estabelecimento, ammount, budgetName, category.getCategoryName(), "None", null, dtOcurr, 0);
							}							
						}
					}
									
				}
			} catch (ParseException e) {
				Log.e(this.getClass().getName(), e.getMessage());
			} catch (BusinessException e) {
				Log.e(this.getClass().getName(), e.getMessage());
			}
		}
	}
	
}