package com.paydayr.moneymanager.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;

public class AddBudgetActivity extends Activity implements OnItemSelectedListener, OnClickListener {
	
	private MoneyManagerFacade moneyManagerFacade;
	private String categorySelected;
	private String subCategorySelected;
	private List<Category> categories;
	private int renovacao;
	private Integer day;
    private Integer month;
    private Integer year;
    
    private SimpleDateFormat sdf;
    private static String DATETIME_ISO_STRMASK = "%d-%d-%d %d:%d:%d";
    
	private Spinner subcatSpinner;
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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.add_budget_page);
	    
	    moneyManagerFacade = new MoneyManagerFacade();
	    
	    sdf = new SimpleDateFormat(Constants.DATE_PATTERN);
	    
	    //DECLARANDO BINDING
	    final Spinner catSpinner = (Spinner) findViewById(R.id.add_budget_spinner_cat);
	    catSpinner.setOnItemSelectedListener(this);
	    
	    subcatSpinner = (Spinner) findViewById(R.id.add_budget_spinner_subcat);
	    subcatSpinner.setOnItemSelectedListener(this);
	    
	    //DECLARANDO BINDING 
	    final Button concluidoButton = (Button) findViewById(R.id.add_budget_button_done);	    
	    final EditText budgetName = (EditText) findViewById(R.id.add_budget_editText_name_value);
	    final EditText budgetValue = (EditText) findViewById(R.id.add_budget_editText_value);
	    renovacaoNenhum = (RadioButton) findViewById(R.id.add_budget_RadioButton0);
	    renovacaoDiaria = (RadioButton) findViewById(R.id.add_budget_RadioButton1);
	    renovacaoSemanal = (RadioButton) findViewById(R.id.add_budget_RadioButton2);
	    renovacaoMensal = (RadioButton) findViewById(R.id.add_budget_RadioButton3);
	    renovacaoAnual = (RadioButton) findViewById(R.id.add_budget_RadioButton4);
	    renovacaoTv0 = (TextView) findViewById(R.id.add_budget_TextView_renew0);
	    renovacaoTv1 = (TextView) findViewById(R.id.add_budget_TextView_renew1);
	    renovacaoTv2 = (TextView) findViewById(R.id.add_budget_TextView_renew2);
	    renovacaoTv3 = (TextView) findViewById(R.id.add_budget_TextView_renew3);
	    renovacaoTv4 = (TextView) findViewById(R.id.add_budget_TextView_renew4);
	    
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
	    
	    //ADICIONANDO LISTENER
	    concluidoButton.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
				if( budgetValue.getText().toString().trim().length() == 0 || budgetName.getText().toString().trim().length() == 0 || categorySelected.trim().length() == 0 ){
					showDialog(Constants.DIALOG_ADD_BUDGET_EMPTY);
				} else{

					try {
						Date dtOcurr = null;
						if( day != null && month != null && year != null ){
							 sdf = new SimpleDateFormat("HH");
							 int hour = Integer.parseInt(sdf.format(new Date()));
							 sdf = new SimpleDateFormat("mm");
							 int minute = Integer.parseInt(sdf.format(new Date()));
							 sdf = new SimpleDateFormat(Constants.DATETIME_PATTERN);
							 dtOcurr =  sdf.parse(String.format(DATETIME_ISO_STRMASK, year, month, day, hour, minute, 0));
						 } else{
							 dtOcurr = new Date();
						 }
						
						moneyManagerFacade.addBudget(v.getContext(), budgetName.getText().toString(), Double.parseDouble(budgetValue.getText().toString()), categorySelected, subCategorySelected, dtOcurr, renovacao);
					} catch (NumberFormatException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						Toast.makeText(v.getContext(), "Error writing Budget. Contact the system administrator.", Toast.LENGTH_LONG).show();
					} catch (BusinessException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						Toast.makeText(v.getContext(), "Error writing Budget. Contact the system administrator.", Toast.LENGTH_LONG).show();
					} catch (ParseException e) {
						 Log.e(this.getClass().getName(), e.getMessage());
						 Toast.makeText(v.getContext(), "Error converting date. Contact the system administrator.", Toast.LENGTH_LONG).show();
					}						
					
					MainActivity.LAST_ABA = 2;
					finish();
				}				
			}
		});
	    
		//BINDING BOTAO PICK DATE
		Button dateButton = (Button) findViewById(R.id.add_budget_button_date);
		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDate();
			}
		});
		
	    categories = moneyManagerFacade.getCategoryList(this);
	    
		if( categories.size() > 0 ){
	    	
	        Collections.sort(categories, new Comparator<Category>() {
				@Override
				public int compare(Category lhs, Category rhs) {
					return lhs.getCategoryName().compareTo(rhs.getCategoryName());
				}
			});
	        
	        List<String> categoriesNames = new ArrayList<String>();
	        
	        for( Category category : categories ){
	        	categoriesNames.add(category.getCategoryName());
	        }
	        
	        categorySelected = categories.get(0).getCategoryName();
	        subCategorySelected = "None";
	        
	        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(AddBudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, categoriesNames.toArray(new String [categoriesNames.size()]));
	        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	
	        // Set this blank adapter to the list view
	    	catSpinner.setAdapter(catAdapter);
	    	
	    	if( categories.get(0).getSubCategories() != null && categories.get(0).getSubCategories().size() > 0 ){
	    		
	    		List<String> subCategoriesNames = new ArrayList<String>();
		        //ADICIONANDO ELEMENTO VAZIO
	    		subCategoriesNames.add("None");
	    		
		        for( SubCategory subCategory : categories.get(0).getSubCategories() ){
		        	subCategoriesNames.add(subCategory.getSubCategoryName());
		        }
		        
		        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<String>(AddBudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, subCategoriesNames.toArray(new String [subCategoriesNames.size()]));
		        subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    	
		        // Set this blank adapter to the list view
		    	subcatSpinner.setAdapter(subCatAdapter);	
	    	}
		} else{
			Toast.makeText(this, "You must add at least one category before creating a budget.", Toast.LENGTH_LONG).show();
			this.finish();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alert;
		AlertDialog.Builder builder;
	    switch(id) {
	    case Constants.DIALOG_ADD_BUDGET_EMPTY:
	    	builder = new AlertDialog.Builder(this);
	    	builder.setMessage("A name, value and category for the Budget is required. The Subcategory is optional. Want to go back to the home screen?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   //setanto a aba de categorias p/ voltar corretamente
	    	        	   MainActivity.LAST_ABA = 1;
	    	        	   finish();
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
	    default:
	        alert = null;
	    }
	    return alert;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if( arg0.getId() == R.id.add_budget_spinner_cat ){
			categorySelected = (String) arg0.getItemAtPosition(arg2);
			Category category = null;
			
			for( Category categoryTmp : categories ){
				if( categoryTmp.getCategoryName().equals(categorySelected) ){
					category = categoryTmp;
				}
			}
			
			List<String> subCategoriesNames = new ArrayList<String>();
			subCategoriesNames.add("None");
			
	        for( SubCategory subCategory : category.getSubCategories() ){
	        	subCategoriesNames.add(subCategory.getSubCategoryName());
	        }
	        
	        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<String>(AddBudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, subCategoriesNames.toArray(new String [subCategoriesNames.size()]));
	        subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	
	        // Set this blank adapter to the list view
	    	subcatSpinner.setAdapter(subCatAdapter);
		} else if( arg0.getId() == R.id.add_budget_spinner_subcat ){
			subCategorySelected = (String) arg0.getItemAtPosition(arg2);	
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	@Override
	public void onClick(View v) {
		// CHECK SELECTED
		switch (v.getId()) {
		case R.id.add_budget_RadioButton0:
			renovacaoNenhum.setChecked(true);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 0;
			break;
		case R.id.add_budget_RadioButton1:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(true);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 1;
			break;
		case R.id.add_budget_RadioButton2:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(true);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 2;
			break;
		case R.id.add_budget_RadioButton3:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(true);
			renovacaoAnual.setChecked(false);
			renovacao = 3;
			break;
		case R.id.add_budget_RadioButton4:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(true);
			renovacao = 4;
			break;
		case R.id.add_budget_TextView_renew0:
			renovacaoNenhum.setChecked(true);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 0;
			break;
		case R.id.add_budget_TextView_renew1:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(true);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 1;
			break;
		case R.id.add_budget_TextView_renew2:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(true);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(false);
			renovacao = 2;
			break;
		case R.id.add_budget_TextView_renew3:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(true);
			renovacaoAnual.setChecked(false);
			renovacao = 3;
			break;
		case R.id.add_budget_TextView_renew4:
			renovacaoNenhum.setChecked(false);
			renovacaoDiaria.setChecked(false);
			renovacaoSemanal.setChecked(false);
			renovacaoMensal.setChecked(false);
			renovacaoAnual.setChecked(true);
			renovacao = 4;
			break;
		}		
	}
	
	private void dialogDate() {
	    final Dialog dialog = new Dialog(this);
	    dialog.setContentView(R.layout.date_layout);
	    dialog.setCancelable(true);
	    dialog.setTitle("Select a date");
	    dialog.show();
	    
	    final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker1);
	    Button btn = (Button) dialog.findViewById(R.id.picker_time_button_ok);
	    btn.setOnClickListener(new OnClickListener()
	    {

	        public void onClick(View arg0)
	        {
	            day = datePicker.getDayOfMonth(); 
	            month = datePicker.getMonth()+1;
	            year = datePicker.getYear();

	            Toast.makeText(AddBudgetActivity.this, "Selected date: " + day + "/" + month + "/" + year, Toast.LENGTH_SHORT).show();
	            dialog.cancel();
	        }

	    });	    
	}
}
