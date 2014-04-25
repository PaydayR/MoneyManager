package com.paydayr.moneymanager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.util.Constants;

public class AddCategoryActivity extends Activity {
	
	private MoneyManagerFacade moneyManagerFacade;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.add_categ_layout);
	    
	    moneyManagerFacade = new MoneyManagerFacade();
	    
	    //DECLARANDO BINDING 
	    final Button concluidoButton = (Button) findViewById(R.id.add_cat_button_done);
	    
	    //ADICIONANDO LISTENER
	    concluidoButton.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
				EditText catEditText = (EditText) findViewById(R.id.add_cat_editText_catValue);
				
				if( catEditText.getText().toString().trim().length() == 0 ){
					showDialog(Constants.DIALOG_ADD_CAT_EMPTY);
				} else{
					moneyManagerFacade.addCategory(v.getContext(), catEditText.getText().toString());
					
					MainActivity.LAST_ABA = 1;
					finish();
				}
			}
		});
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alert;
		AlertDialog.Builder builder;
	    switch(id) {
	    case Constants.DIALOG_ADD_CAT_EMPTY:
	    	builder = new AlertDialog.Builder(this);
	    	builder.setMessage("A category name is required. Want to go back to the home screen?")
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
}
