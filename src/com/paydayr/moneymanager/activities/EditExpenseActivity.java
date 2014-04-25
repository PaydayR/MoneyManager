package com.paydayr.moneymanager.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.holders.ExpensesListHolder;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;

public class EditExpenseActivity extends Activity implements OnItemSelectedListener, OnClickListener {
	
	private Spinner subcatSpinner;
	private String categorySelected;
	private String subCategorySelected;
	private String budgetSelected;
	private List<Budget> budgets;
	private List<Category> categories;
	
    private int renovacao;
	private RadioButton renovacaoNenhum;
	private RadioButton renovacaoDiaria;
	private RadioButton renovacaoSemanal;
	private RadioButton renovacaoMensal;
	private RadioButton renovacaoAnual;
	private TextView renovacaoTv0;
	private TextView renovacaoTv1;
	private TextView renovacaoTv2;
	private TextView renovacaoTv3;
	private TextView renovacaoTv4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_expenses_page);
		
		// DECLARANDO BINDING
		final Spinner catSpinner = (Spinner) findViewById(R.id.edit_expense_spinner_cat);
		catSpinner.setOnItemSelectedListener(this);

		subcatSpinner = (Spinner) findViewById(R.id.edit_expense_spinner_subcat);
		subcatSpinner.setOnItemSelectedListener(this);

		final Spinner budgetSpinner = (Spinner) findViewById(R.id.edit_expense_spinner_bud);
		budgetSpinner.setOnItemSelectedListener(this);
		
		renovacaoNenhum = (RadioButton) findViewById(R.id.edit_expense_RadioButton0);
	    renovacaoDiaria = (RadioButton) findViewById(R.id.edit_expense_RadioButton1);
	    renovacaoSemanal = (RadioButton) findViewById(R.id.edit_expense_RadioButton2);
	    renovacaoMensal = (RadioButton) findViewById(R.id.edit_expense_RadioButton3);
	    renovacaoAnual = (RadioButton) findViewById(R.id.edit_expense_RadioButton4);
	    renovacaoTv0 = (TextView) findViewById(R.id.edit_expense_TextView_renew0);
	    renovacaoTv1 = (TextView) findViewById(R.id.edit_expense_TextView_renew1);
	    renovacaoTv2 = (TextView) findViewById(R.id.edit_expense_TextView_renew2);
	    renovacaoTv3 = (TextView) findViewById(R.id.edit_expense_TextView_renew3);
	    renovacaoTv4 = (TextView) findViewById(R.id.edit_expense_TextView_renew4);
	    
	    renovacaoNenhum.setOnClickListener(this);
	    renovacaoDiaria.setOnClickListener(this);
	    renovacaoSemanal.setOnClickListener(this);
	    renovacaoMensal.setOnClickListener(this);
	    renovacaoAnual.setOnClickListener(this);
	    renovacaoTv0.setOnClickListener(this);
	    renovacaoTv1.setOnClickListener(this);
	    renovacaoTv2.setOnClickListener(this);
	    renovacaoTv3.setOnClickListener(this);
	    renovacaoTv4.setOnClickListener(this);
	    
	    renovacaoNenhum.setChecked(true);

		// DECLARANDO BINDING
		final Button concluidoButton = (Button) findViewById(R.id.add_expense_button_done);
		
		//INICIALIZANDO SERVICE
		final MoneyManagerFacade moneyManagerFacade = new MoneyManagerFacade();

		// ADICIONANDO LISTENER
		concluidoButton.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				// ADICIONANDO BUDGET
				EditText novaCategoriaTv = (EditText) findViewById(R.id.edit_expense_EditText_novaCat);

				if ( (categorySelected == null || categorySelected.equals("None")) && (budgetSelected == null || budgetSelected.equals("None")) && novaCategoriaTv.getText().toString().trim().length() == 0 ) {
					showDialog(Constants.DIALOG_ADD_EXPENSE_EMPTY);
				} else {

					 try {
						 ExpensesListHolder holder = (ExpensesListHolder) getIntent().getSerializableExtra(Constants.EXPENSES_EDIT_LIST);
						 moneyManagerFacade.editExpenses(v.getContext(), budgetSelected, categorySelected, subCategorySelected, novaCategoriaTv.getText().toString(), holder.getExpensesList(), renovacao);
					 } catch (NumberFormatException e) {
						 Log.e(this.getClass().getName(), e.getMessage());
						 Toast.makeText(v.getContext(), "Error writing Budget. Contact the system administrator.", Toast.LENGTH_LONG).show();
					 } catch (BusinessException e) {
						 Log.e(this.getClass().getName(), e.getMessage());
						 Toast.makeText(v.getContext(), "Error writing Budget. Contact the system administrator.", Toast.LENGTH_LONG).show();
					 } 

					MainActivity.LAST_ABA = 0;
					finish();
				}				
			}
		});
		
		try {
			budgets = moneyManagerFacade.getBudgetList(this);
		} catch (BusinessException e) {
			 Log.e(this.getClass().getName(), e.getMessage());
			 Toast.makeText(this, "Error getting Budgets. Contact the system administrator.", Toast.LENGTH_LONG).show();
		}
		categories = moneyManagerFacade.getCategoryList(this);
		
		if (budgets.size() > 0) {
			
			List<String> budgetsNames = new ArrayList<String>();			
			for( Budget budget : budgets ){
				budgetsNames.add(budget.getName());
			}
			
			Collections.sort(budgetsNames);
			
			budgetsNames.add(0, "None");
			
			ArrayAdapter<String> budgetAdapter = new ArrayAdapter<String>(EditExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, budgetsNames.toArray(new String[budgetsNames.size()]));
			budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Set this blank adapter to the list view
			budgetSpinner.setAdapter(budgetAdapter);
		}

		if (categories.size() > 0) {

			Collections.sort(categories, new Comparator<Category>() {
				@Override
				public int compare(Category lhs, Category rhs) {
					return lhs.getCategoryName().compareTo(
							rhs.getCategoryName());
				}
			});

			List<String> categoriesNames = new ArrayList<String>();
			categoriesNames.add("None");
			for (Category category : categories) {
				categoriesNames.add(category.getCategoryName());
			}

			categorySelected = "None";
			subCategorySelected = "None";
			budgetSelected = "None";

			ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(EditExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, categoriesNames.toArray(new String[categoriesNames.size()]));
			catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// Set this blank adapter to the list view
			catSpinner.setAdapter(catAdapter);

			if (categories.get(0).getSubCategories() != null
					&& categories.get(0).getSubCategories().size() > 0) {

				List<String> subCategoriesNames = new ArrayList<String>();
				// ADICIONANDO ELEMENTO VAZIO
				subCategoriesNames.add("None");

				for (SubCategory subCategory : categories.get(0).getSubCategories()) {
					subCategoriesNames.add(subCategory.getSubCategoryName());
				}

				ArrayAdapter<String> subCatAdapter = new ArrayAdapter<String>(EditExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, subCategoriesNames.toArray(new String[subCategoriesNames.size()]));
				subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// Set this blank adapter to the list view
				subcatSpinner.setAdapter(subCatAdapter);
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg0.getId() == R.id.edit_expense_spinner_cat) {
			categorySelected = (String) arg0.getItemAtPosition(arg2);
			Category category = null;

			for (Category categoryTmp : categories) {
				if (categoryTmp.getCategoryName().equals(categorySelected)) {
					category = categoryTmp;
				}
			}

			List<String> subCategoriesNames = new ArrayList<String>();
			subCategoriesNames.add("None");

			if( category != null ){
				for (SubCategory subCategory : category.getSubCategories()) {
					subCategoriesNames.add(subCategory.getSubCategoryName());
				}	
			}			

			ArrayAdapter<String> subCatAdapter = new ArrayAdapter<String>(EditExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, subCategoriesNames.toArray(new String[subCategoriesNames.size()]));
			subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// Set this blank adapter to the list view
			subcatSpinner.setAdapter(subCatAdapter);
		} else if (arg0.getId() == R.id.edit_expense_spinner_subcat) {
			subCategorySelected = (String) arg0.getItemAtPosition(arg2);
		} else if (arg0.getId() == R.id.edit_expense_spinner_bud){
			budgetSelected = (String) arg0.getItemAtPosition(arg2);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alert;
		AlertDialog.Builder builder;
		switch (id) {
		case Constants.DIALOG_ADD_EXPENSE_EMPTY:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"You must choose a category or budget, or add a new category. Subcategory is optional. Want to go back to the home screen?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// setanto a aba de categorias p/ voltar
									// corretamente
									MainActivity.LAST_ABA = 0;
									finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// DO NOTHING
									dialog.cancel();
								}
							});
			alert = builder.create();
			break;
		default:
			alert = null;
		}
		return alert;
	}
	
	@Override
	public void onClick(View v) {
		// CHECK SELECTED
		switch (v.getId()) {
		case R.id.edit_expense_RadioButton0:
			renovacaoNenhum.setChecked(true);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 0;
			break;
		case R.id.edit_expense_RadioButton1:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(true);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 1;
			break;
		case R.id.edit_expense_RadioButton2:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(true);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 2;
			break;
		case R.id.edit_expense_RadioButton3:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(true);
			renovacaoAnual.setChecked(false);
			renovacao = 3;
			break;
		case R.id.edit_expense_RadioButton4:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(true);
			renovacao = 4;
			break;
		case R.id.edit_expense_TextView_renew0:
			renovacaoNenhum.setChecked(true);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 0;
			break;
		case R.id.edit_expense_TextView_renew1:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(true);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 1;
			break;
		case R.id.edit_expense_TextView_renew2:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(true);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 2;
			break;
		case R.id.edit_expense_TextView_renew3:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(true);
			renovacaoAnual.setChecked(false);
			renovacao = 3;
			break;
		case R.id.edit_expense_TextView_renew4:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(true);
			renovacao = 4;
			break;
		}		
	}
}
