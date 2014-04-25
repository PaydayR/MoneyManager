package com.paydayr.moneymanager.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.adapter.BudgetsListAdapter;
import com.paydayr.moneymanager.adapter.CategoriesListAdapter;
import com.paydayr.moneymanager.adapter.ExpensesListAdapter;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.holders.ExpensesListHolder;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.util.Constants;
import com.paydayr.moneymanager.util.Converter;

public class MainActivity extends Activity implements OnClickListener{
	
	public static int LAST_ABA = 0;
	public static int PERIODO = 1;
	private MoneyManagerFacade moneyManagerFacade;
	private List<Expense> expensesList;
	private List<Category> categoriesList;
	private List<Expense> expensesEditList;
	private List<Budget> budgetsList;
	private ViewFlipper vf;
	
    /** Called when the activity is first created. */
	@Override
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
        
        //DECLARANDO OS OBJETOS BINDING DOS LAYOUTS
        final Button addExpenseButton = (Button) findViewById(R.id.expenses_button_add_id);
        final Button addCategButton = (Button) findViewById(R.id.categories_button_add_cat);
        final Button addSubcatButton = (Button) findViewById(R.id.categories_button_add_subcat);
        final Button addBudgetButton = (Button) findViewById(R.id.budget_button_add);
        final Button setPeriodExpButton = (Button) findViewById(R.id.expenses_button_period_id);
        final Button setPeriodCatButton = (Button) findViewById(R.id.categories_Button_period);
        final Button setPeriodBudButton = (Button) findViewById(R.id.budgets_Button_period);
        
        TextView mainPage_expensesTitle = (TextView) findViewById(R.id.mainpage_textView_expTittle);
        TextView mainPage_categoriesTitle = (TextView) findViewById(R.id.mainpage_TextView_catTittle);
        TextView mainPage_budgetsTitle = (TextView) findViewById(R.id.mainpage_TextView_budTittle);
        
        //SETANDO ONCLICK LISTENER
        mainPage_expensesTitle.setOnClickListener(this);
        mainPage_categoriesTitle.setOnClickListener(this);
        mainPage_budgetsTitle.setOnClickListener(this);
        
        //ADICIONANDO LISTENERS AOS BOTOES DE TODAS AS TELAS
        setPeriodExpButton.setOnClickListener(this);
        setPeriodCatButton.setOnClickListener(this);
        setPeriodBudButton.setOnClickListener(this);
        addExpenseButton.setOnClickListener(this);
        addCategButton.setOnClickListener(this);
        addSubcatButton.setOnClickListener(this);
        addBudgetButton.setOnClickListener(this);

        //SETANDO A ABA CORRETA
        vf = (ViewFlipper) findViewById(R.id.flipper);
        for( int i = 0; i<LAST_ABA; i++ ){
        	vf.showNext();
        }
        
        //CRIANDO O DAO DE CATEGORY
        moneyManagerFacade = new MoneyManagerFacade();
        
        //INICIANDO RENOVACOES
        try {
			moneyManagerFacade.executarRenovacoes(this);
		} catch (BusinessException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

        //INICIALIZANDO PERIODO DEFAULT
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		PERIODO = sharedPreferences.getInt(Constants.PERIODO, 1);
        
        //INICIANDO LISTAS PELA 1A VEZ
        try {
			expensesList = moneyManagerFacade.getExpenseList(this, PERIODO);
			categoriesList = moneyManagerFacade.getCategoryList(this);
	        budgetsList = moneyManagerFacade.getBudgetList(this);
		} catch (BusinessException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
        
		TextView totalValue = (TextView) findViewById(R.id.expenses_TextView_total_value);
		totalValue.setText(Converter.getDecimalFormat().format(getTotal()));
		
		//INICIANDO COR DA TAB
		inicializarTab();
    }
    
    
    
    @Override
	protected void onResume() {
		super.onResume();
		//INICIANDO RENOVACOES
        try {
			moneyManagerFacade.executarRenovacoes(this);
		} catch (BusinessException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		try {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			PERIODO = sharedPreferences.getInt(Constants.PERIODO, 1);
			initialize();
		} catch (BusinessException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void initialize() throws BusinessException{
		//INICIANDO COR DA TAB
		inicializarTab();
		
		initializeExpenses();
		initializeCategories();
		initializeBudgets();		
    }
	
	private void initializeExpenses() throws BusinessException{
		//INICIALIZANDO ADAPTER

		expensesList = moneyManagerFacade.getExpenseList(this, PERIODO);
		ExpensesListAdapter adapter = new ExpensesListAdapter(this, R.layout.list_expenses, expensesList);
    	
    	//INICIALIZANDO CATEGORIAS
    	ListView expensesListView = (ListView) findViewById(R.id.expenses_listView);
//    	expensesListView.setOnTouchListener(onTouchListener);
    	
        // Set this blank adapter to the list view	    	
    	expensesListView.setAdapter(adapter);
    	
    	TextView totalValue = (TextView) findViewById(R.id.expenses_TextView_total_value);
		totalValue.setText(Converter.getDecimalFormat().format(getTotal()));
	}
	
	private void initializeCategories(){
		//INICIALIZANDO ADAPTER
		categoriesList = moneyManagerFacade.getCategoryList(this);
		CategoriesListAdapter adapter = new CategoriesListAdapter(this, R.layout.list_categories, categoriesList);
		        	
    	//INICIALIZANDO CATEGORIAS
    	ListView categoriesListView = (ListView) findViewById(R.id.categories_listView);
    	
        // Set this blank adapter to the list view	    	
    	categoriesListView.setAdapter(adapter);
	}
	
	private void initializeBudgets() throws BusinessException{
		//INICIALIZANDO ADAPTER
		budgetsList = moneyManagerFacade.getBudgetList(this);
		BudgetsListAdapter adapter = new BudgetsListAdapter(this, R.layout.list_budgets, budgetsList);
    	
    	//INICIALIZANDO CATEGORIAS
    	ListView budgetsListView = (ListView) findViewById(R.id.budgets_listView);
//    	budgetsListView.setOnTouchListener(onTouchListener);
    	
        // Set this blank adapter to the list view	    	
    	budgetsListView.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
	}

	// This method is called once the menu is selected
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		    	       		
			case R.id.menu_delete_item:
				showDialog(Constants.DIALOG_CONFIRM_DEL);
				break;
			case R.id.menu_update_item:
				editItems();
				break;
			case R.id.menu_show_chart:
				Intent i = new Intent(this, ChartActivity.class);
				startActivity(i);
				break;
		}
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog alert;
		AlertDialog.Builder builder;
		
	    switch(id) {
		    case Constants.DIALOG_CONFIRM_DEL:
		    	
		    	builder = new AlertDialog.Builder(this);
		    	builder.setMessage("Remove the selected items?")
		    	       .setCancelable(false)
		    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	           @SuppressWarnings("deprecation")
		    	           public void onClick(DialogInterface dialog, int id) {		    	        	   
								showDialog(Constants.DIALOG_CONFIRM_DEL_RELATIONS);
		    	           }
		    	       })
		    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   //DO NOTHING
		    	        	   dialog.cancel();
		    	           }
		    	       });
		    	alert = builder.create();	    
		    	break;
		    case Constants.DIALOG_CONFIRM_DEL_RELATIONS:
		    	builder = new AlertDialog.Builder(this);
		    	builder.setMessage("This operation may take some time depending on the amount of existing expenditures. Would you like to ontinue?")
		    	       .setCancelable(false)
		    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   switch(vf.getCurrentView().getId()){
		    	        	   		case R.id.expense_view:
		    	        	   			for( Expense expense : expensesList ){
											CheckBox checkbox = (CheckBox) MainActivity.this.findViewById(Double.valueOf(expense.getId()).intValue()+1000);
											if( checkbox != null && checkbox.isChecked() ){
												try {
//													Toast.makeText(MainActivity.this, "Selected: " + expense.getAmmount(), Toast.LENGTH_SHORT).show();
													moneyManagerFacade.removeExpense(MainActivity.this, expense);
												} catch (BusinessException e) {
													Log.e(this.getClass().getName(), e.getMessage());
													Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
												}
											}	
										}
		    	        	   			Toast.makeText(MainActivity.this, "Expenses successfully removed.", Toast.LENGTH_SHORT).show();
		    	        	   			break;
		    	        	   		case R.id.categories_view:
		    	        	   			for( Category category : categoriesList ){
											CheckBox checkbox = (CheckBox) MainActivity.this.findViewById(Double.valueOf(category.getId()).intValue()+2000);
											if( checkbox != null && checkbox.isChecked() ){
												try {
//													Toast.makeText(MainActivity.this, "Selected: " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
													moneyManagerFacade.removeCategory(MainActivity.this, category);
												} catch (BusinessException e) {
													Log.e(this.getClass().getName(), e.getMessage());
													Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
												}
											}	
										}
		    	        	   			Toast.makeText(MainActivity.this, "Categories successfully removed.", Toast.LENGTH_SHORT).show();
		    	        	   			break;
		    	        	   		case R.id.budgets_view:
		    	        	   			for( Budget budget : budgetsList ){
											CheckBox checkbox = (CheckBox) MainActivity.this.findViewById(Double.valueOf(budget.getId()).intValue()+3000);
											if( checkbox != null && checkbox.isChecked() ){
												try {
//													Toast.makeText(MainActivity.this, "Selected: " + budget.getTotal(), Toast.LENGTH_SHORT).show();
													moneyManagerFacade.removeBudget(MainActivity.this, budget);
												} catch (BusinessException e) {
													Log.e(this.getClass().getName(), e.getMessage());
													Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
												}
											}	
										}
		    	        	   			Toast.makeText(MainActivity.this, "Budgets successfully removed.", Toast.LENGTH_SHORT).show();
		    	        	   			break;
		    	        	   }
								try {
									initialize();
								} catch (BusinessException e) {
									Log.e(this.getClass().getName(), e.getMessage());
									Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
								}
		    	           }
		    	       })
		    	       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   //DO NOTHING
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
		Intent i = null;
		switch(v.getId()){
			case R.id.mainpage_textView_expTittle:
				vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
				while( LAST_ABA > 0 ){
					LAST_ABA--;
					vf.showPrevious();
				}

				break;
			case R.id.mainpage_TextView_catTittle:
				vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
				while( LAST_ABA > 1 ){
					LAST_ABA--;					
					vf.showPrevious();
				}
				while( LAST_ABA < 1 ){
					LAST_ABA++;
					vf.showNext();
				}

				break;
			case R.id.mainpage_TextView_budTittle:
				vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
				while( LAST_ABA < 2 ){
					LAST_ABA++;
					vf.showNext();
				}

				break;
			case R.id.expenses_button_add_id:
				i = new Intent(MainActivity.this, AddExpenseActivity.class);
				startActivity(i);
				break;
			case R.id.expenses_button_period_id:
				i = new Intent(MainActivity.this, SetPeriodActivity.class);
				startActivity(i);
				break;
			case R.id.categories_Button_period:
				i = new Intent(MainActivity.this, SetPeriodActivity.class);
				startActivity(i);
				break;
			case R.id.budgets_Button_period:
				i = new Intent(MainActivity.this, SetPeriodActivity.class);
				startActivity(i);
				break;
			case R.id.categories_button_add_cat:
				i = new Intent(MainActivity.this, AddCategoryActivity.class);
				startActivity(i);
				break;
			case R.id.categories_button_add_subcat:
				i = new Intent(MainActivity.this, AddSubCatActivity.class);
				startActivity(i);
				break;
			case R.id.budget_button_add:
				i = new Intent(MainActivity.this, AddBudgetActivity.class);
				startActivity(i);
				break;
		}
	}
	
	private void editItems(){
		switch(vf.getCurrentView().getId()){
	   		case R.id.expense_view:
	   			expensesEditList = new ArrayList<Expense>();
	   			for( Expense expense : expensesList ){
					CheckBox checkbox = (CheckBox) MainActivity.this.findViewById(Double.valueOf(expense.getId()).intValue()+1000);
					if( checkbox != null && checkbox.isChecked() ){
						expensesEditList.add(expense);
					}
	   			}
	   			
	   			if( expensesEditList.size() > 0 ){
	   				Intent i = new Intent(MainActivity.this, EditExpenseActivity.class);
	   				ExpensesListHolder holder = new ExpensesListHolder();
	   				holder.setExpensesList(expensesEditList);
	   				i.putExtra(Constants.EXPENSES_EDIT_LIST, holder);
	   				startActivity(i);
	   			} else{
	   				Toast.makeText(this, "You must select at least one expense for editing.", Toast.LENGTH_LONG).show();
	   			}
	   			break;
	   		case R.id.categories_view:
	   			Toast.makeText(this, "Function not implemented.", Toast.LENGTH_SHORT).show();
	   			break;
	   		case R.id.budgets_view:
	   			Toast.makeText(this, "Function not implemented.", Toast.LENGTH_SHORT).show();
	   			break;
		}
	}
	
	private double getTotal(){
		double total = 0.0;
		for( Expense expense: expensesList ){
			total += expense.getAmmount();
		}
		return total;
	}
	
	private void inicializarTab(){
		switch(LAST_ABA){
			case 0:
				//TextView tv = (TextView) findViewById(R.id.mainpage_textView_expTittle);
				//tv.setBackgroundColor(getResources().getColor(R.color.list_second_color));
				break;
			case 1:
				//tv = (TextView) findViewById(R.id.mainpage_TextView_catTittle);
				//tv.setBackgroundColor(getResources().getColor(R.color.list_second_color));
				break;
			case 2:
				//tv = (TextView) findViewById(R.id.mainpage_TextView_budTittle);
				//tv.setBackgroundColor(getResources().getColor(R.color.list_second_color));
				break;
		}		
	}
}