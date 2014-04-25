package com.paydayr.moneymanager.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.util.Constants;

public class SetPeriodActivity extends Activity implements OnClickListener {

	private RadioButton radio1;
	private RadioButton radio2;
	private RadioButton radio3;
	private RadioButton radio4;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_period_page);

		// BINDINGS
		final TextView last24HoursTv = (TextView) findViewById(R.id.set_period_textview_label1);
		final TextView mesCorrenteTv = (TextView) findViewById(R.id.set_period_textview_label2);
		final TextView mesAnteriorTv = (TextView) findViewById(R.id.set_period_textview_label3);
		final TextView anoCorrenteTv = (TextView) findViewById(R.id.set_period_textview_label4);
		 
		radio1 = (RadioButton) findViewById(R.id.set_period_radioButton1);
		radio2 = (RadioButton) findViewById(R.id.set_period_radioButton2);
		radio3 = (RadioButton) findViewById(R.id.set_period_radioButton3);
		radio4 = (RadioButton) findViewById(R.id.set_period_radioButton4);
		Button doneButton = (Button) findViewById(R.id.set_period_button_done);
		doneButton.setOnClickListener(this);

		// LISTENERS
		last24HoursTv.setOnClickListener(this);
		mesCorrenteTv.setOnClickListener(this);
		mesAnteriorTv.setOnClickListener(this);
		anoCorrenteTv.setOnClickListener(this);
		
		radio1.setOnClickListener(this);
		radio2.setOnClickListener(this);
		radio3.setOnClickListener(this);
		radio4.setOnClickListener(this);

		initializeRadios();

	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeRadios();
	}

	private void initializeRadios() {
		// CHECK SELECTED
		switch (MainActivity.PERIODO) {
		case 1:
			radio1.setChecked(true);
			radio2.setChecked(false);
			radio3.setChecked(false);
			radio4.setChecked(false);
			break;
		case 2:
			radio1.setChecked(false);
			radio2.setChecked(true);
			radio3.setChecked(false);
			radio4.setChecked(false);
			break;
		case 3:
			radio1.setChecked(false);
			radio2.setChecked(false);
			radio3.setChecked(true);
			radio4.setChecked(false);
			break;
		case 4:
			radio1.setChecked(false);
			radio2.setChecked(false);
			radio3.setChecked(false);
			radio4.setChecked(true);
			break;
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.set_period_radioButton1:
			if (((RadioButton) view).isChecked()) {
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				MainActivity.PERIODO = 1;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences.edit();
				editor.putInt(Constants.PERIODO, 1);
				editor.commit();
			}
			break;
		case R.id.set_period_radioButton2:
			if (((RadioButton) view).isChecked()) {
				radio1.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				MainActivity.PERIODO = 2;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences.edit();
				editor.putInt(Constants.PERIODO, 2);
				editor.commit();
			}
			break;
		case R.id.set_period_radioButton3:
			if (((RadioButton) view).isChecked()) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio4.setChecked(false);
				MainActivity.PERIODO = 3;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences.edit();
				editor.putInt(Constants.PERIODO, 3);
				editor.commit();
			}
			break;
		case R.id.set_period_radioButton4:
			if (((RadioButton) view).isChecked()) {
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				MainActivity.PERIODO = 4;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
				Editor editor = sharedPreferences.edit();
				editor.putInt(Constants.PERIODO, 4);
				editor.commit();
			}
			break;
		case R.id.set_period_textview_label1:
			radio1.setChecked(true);
			radio2.setChecked(false);
			radio3.setChecked(false);
			radio4.setChecked(false);
			MainActivity.PERIODO = 1;
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = sharedPreferences.edit();
			editor.putInt(Constants.PERIODO, 1);
			editor.commit();
			break;
		case R.id.set_period_textview_label2:
			radio1.setChecked(false);
			radio2.setChecked(true);
			radio3.setChecked(false);
			radio4.setChecked(false);
			MainActivity.PERIODO = 2;
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			editor = sharedPreferences.edit();
			editor.putInt(Constants.PERIODO, 2);
			editor.commit();
			break;
		case R.id.set_period_textview_label3:
			radio1.setChecked(false);
			radio2.setChecked(false);
			radio3.setChecked(true);
			radio4.setChecked(false);
			MainActivity.PERIODO = 3;
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			editor = sharedPreferences.edit();
			editor.putInt(Constants.PERIODO, 3);
			editor.commit();
			break;
		case R.id.set_period_textview_label4:
			radio1.setChecked(false);
			radio2.setChecked(false);
			radio3.setChecked(false);
			radio4.setChecked(true);
			MainActivity.PERIODO = 4;
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			editor = sharedPreferences.edit();
			editor.putInt(Constants.PERIODO, 4);
			editor.commit();
			break;
		case R.id.set_period_button_done:
			this.finish();
			break;
		}
	}

}
