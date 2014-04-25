package com.paydayr.moneymanager.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.util.Constants;

public class AddSubCatActivity extends Activity implements OnItemSelectedListener  {
	
	private MoneyManagerFacade moneyManagerFacade;
	private String categorySelected;
	
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.add_subcat_page);
	    
	    moneyManagerFacade = new MoneyManagerFacade();
	    
	    //DECLARANDO BINDING
	    final Spinner spinner = (Spinner) findViewById(R.id.add_subcat_categories_spinner);
	    spinner.setOnItemSelectedListener(this);
	    
	    //DECLARANDO BINDING 
	    final Button concluidoButton = (Button) findViewById(R.id.add_subcat_button_done);
	    
	    //ADICIONANDO LISTENER
	    concluidoButton.setOnClickListener(new View.OnClickListener() {
			//TODO retirar/ajustar detalhes de compatibilidade abaixo
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
				if( categorySelected.trim().length() > 0 ){
					
					EditText subCatEditText = (EditText) findViewById(R.id.add_subcat_editText_subcatValue);
					
					if( subCatEditText.getText().toString().trim().length() == 0 ){
						showDialog(Constants.DIALOG_ADD_SUBCAT_EMPTY);
					} else{
						moneyManagerFacade.addSubCategory(v.getContext(), moneyManagerFacade.getCategory(v.getContext(), categorySelected), subCatEditText.getText().toString());
						
						MainActivity.LAST_ABA = 1;
						finish();
					}	
				} else{
					Toast.makeText(v.getContext(), "You must select a category", Toast.LENGTH_LONG).show();
				}
			}
		});
	    
	    List<Category> categories = moneyManagerFacade.getCategoryList(this);
			
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
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddSubCatActivity.this, android.R.layout.simple_spinner_dropdown_item, categoriesNames.toArray(new String [categoriesNames.size()]));
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	
	        // Set this blank adapter to the list view
	    	spinner.setAdapter(adapter);	
		} else{
			Toast.makeText(this, "You must add at least one category before you can add subcategories.", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alert;
		AlertDialog.Builder builder;
	    switch(id) {
	    case Constants.DIALOG_ADD_SUBCAT_EMPTY:
	    	builder = new AlertDialog.Builder(this);
	    	builder.setMessage("a subcategory name is required. Want to go back to the home screen?")
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
		categorySelected = (String) arg0.getItemAtPosition(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		categorySelected = (String) arg0.getItemAtPosition(0);
	}
}
