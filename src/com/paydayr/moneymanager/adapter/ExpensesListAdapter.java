package com.paydayr.moneymanager.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.util.Constants;
import com.paydayr.moneymanager.util.Converter;

public class ExpensesListAdapter extends ArrayAdapter<Expense> {
	
	private Context context;
	private List<Expense> expenses;
	private SimpleDateFormat sdf;
	
	public ExpensesListAdapter(Context context, int layout, List<Expense> expenses) {
		super(context, layout, expenses);
		this.context = context;
		this.expenses = expenses;
		sdf = new SimpleDateFormat(Constants.DATETIME_SCREEN_PATTERN);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_expenses, parent, false);
		
		TextView dtOcurr = (TextView) rowView.findViewById(R.id.expenses_textView_dtOcurr);
		TextView local = (TextView) rowView.findViewById(R.id.expenses_TextView_local);
		TextView categoria = (TextView) rowView.findViewById(R.id.expenses_categ_item);
		TextView valor = (TextView) rowView.findViewById(R.id.expenses_ammount_item);
		CheckBox checkbox = (CheckBox) 	rowView.findViewById(R.id.expenses_checkBox);
		checkbox.setId(Double.valueOf(expenses.get(position).getId()).intValue()+1000);
		local.setText(expenses.get(position).getLocal());
		local.setTextColor(context.getResources().getColor(R.color.list_dark_color));
		dtOcurr.setText(sdf.format(expenses.get(position).getDtOcurr()));
		String nomeStr = "";
		if( expenses.get(position).getBudget() != null ){
			nomeStr = "Budget: " + expenses.get(position).getBudget().getName();
		} 
		if( expenses.get(position).getCategory() != null && expenses.get(position).getBudget() != null ){
			nomeStr += "\n";
		}
		if ( expenses.get(position).getCategory() != null ) {
			nomeStr += "Category: " + expenses.get(position).getCategory().getCategoryName() + (expenses.get(position).getCategory().getSubCategories() != null && expenses.get(position).getCategory().getSubCategories().size() > 0 ? " - " + expenses.get(position).getCategory().getSubCategories().get(0).getSubCategoryName() : "");
		}
		if( expenses.get(position).getRenewId() > 0 ){
			switch(expenses.get(position).getRenewId()){
				case 1:
					nomeStr += "\n(daily renewal)";
					break;
				case 2:
					nomeStr += "\n(weekly renewal)";
					break;
				case 3:
					nomeStr += "\n(monthly renewal)";
					break;
				case 4:
					nomeStr += "\n(yearly renewal)";
					break;
			}
		}
		categoria.setText(nomeStr);
		categoria.setTextColor(context.getResources().getColor(R.color.dark_blue));
		valor.setText(Converter.getDecimalFormat().format(expenses.get(position).getAmmount()));
		if( position % 2 == 0 ){
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
			local.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
			categoria.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
			valor.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
			checkbox.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
			dtOcurr.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
		} else{
			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
			local.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
			categoria.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
			valor.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
			checkbox.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
			dtOcurr.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
		}		
		
		return rowView;
	}
}

