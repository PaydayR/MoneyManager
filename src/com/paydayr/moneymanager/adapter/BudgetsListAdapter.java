package com.paydayr.moneymanager.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.activities.MainActivity;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.util.Constants;
import com.paydayr.moneymanager.util.Converter;

@TargetApi(11)
public class BudgetsListAdapter extends ArrayAdapter<Budget> implements OnClickListener {
	
	private Context context;
	private List<Budget> budgets;
	private ViewGroup parent;
	
	private SimpleDateFormat sdf;
	private Currency currency = Currency.getInstance(Constants.CURRENCY);  
	private DecimalFormat formato = new DecimalFormat(currency.getSymbol() + "#,##0.00");
	
	public BudgetsListAdapter(Context context, int layout, List<Budget> budgets) {
		super(context, layout, budgets);
		this.context = context;
		this.budgets = budgets;
		sdf = new SimpleDateFormat(Constants.DATETIME_SCREEN_PATTERN);
	}


	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		this.parent = parent;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_budgets, parent, false);	
		
		TableLayout categoriesTableLayout = (TableLayout) rowView.findViewById(R.id.budgets_tableLayout);
		
		rowView.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
		CheckBox checkbox = (CheckBox) 	rowView.findViewById(R.id.budget_checkBox);
		checkbox.setId(Double.valueOf(budgets.get(position).getId()).intValue()+3000);
		TextView categoria = (TextView) rowView.findViewById(R.id.budgets_categoria_item);
		categoria.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
		categoria.setOnClickListener(this);
		TextView subCategoria = (TextView) rowView.findViewById(R.id.budgets_subcat_item);
		subCategoria.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
		TextView restante = (TextView) rowView.findViewById(R.id.budgets_ammount_item);
		restante.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
		TextView total = (TextView) rowView.findViewById(R.id.budgets_total_item);
		total.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));

		String nomeStr = budgets.get(position).getName();
		sdf = new SimpleDateFormat(Constants.DATE_SCREEN_PATTERN);
		if( budgets.get(position).getRenewId() > 0 ){
			switch(budgets.get(position).getRenewId()){
				case 1:
					nomeStr += "\n(daily renewal - " + sdf.format(budgets.get(position).getDtStart()) + ")";
					break;
				case 2:
					nomeStr += "\n(weekly renewal - " + sdf.format(budgets.get(position).getDtStart()) + ")";
					break;
				case 3:
					nomeStr += "\n(monthly renewal - " + sdf.format(budgets.get(position).getDtStart()) + ")";
					break;
				case 4:
					nomeStr += "\n(yearly renewal - " + sdf.format(budgets.get(position).getDtStart()) + ")";
					break;
			}
		} else{
			nomeStr += "\n(Beginning on " + sdf.format(budgets.get(position).getDtStart()) + ")";
		}
		
		categoria.setText(nomeStr);
		String subCategoriaStr = null;
		if( budgets.get(position).getCategory().getSubCategories() != null && budgets.get(position).getCategory().getSubCategories().size() > 0 ){
			subCategoriaStr = budgets.get(position).getCategory().getSubCategories().get(0).getSubCategoryName();			
		} 
		subCategoria.setText(budgets.get(position).getCategory().getCategoryName() + ((subCategoriaStr != null) ? " - " + subCategoriaStr : ""));
		restante.setText("Remaining:       " + formato.format(budgets.get(position).getAmmount()));
		total.setText("Total:           " + formato.format(budgets.get(position).getTotal()));
		
		//OBTENDO PREFERENCES PARA VERIFICAR QUAIS CATEGORIAS EXPANDIR
		MoneyManagerFacade moneyManagerFacade = new MoneyManagerFacade();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Set<String> ids = sharedPreferences.getStringSet(Constants.BUDGETS_EXPANDED, null);
		
		//DEMAIS LINHAS ABAIXO COM DESPESAS OU NAO. MONTAGEM DINAMICA:
		if( ids != null ){
			for( String id : ids ){
				if( budgets.get(position).getId() == Integer.parseInt(id) ){
					//BUDGET EXPANDIDO ENCONTRADO. CRIAR OS APONTAMENTOS PARA O BUDGET.
					
					//BUSCAR TODAS AS DESPESAS PARA O BUDGET EM QUESTAO
					List<Expense> expenses = new ArrayList<Expense>();
					try {
						expenses = moneyManagerFacade.getExpenseListByBudget(context, budgets.get(position), MainActivity.PERIODO);
					} catch (BusinessException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						Toast.makeText(context, "[BudgetsListAdapter] Error retrieving expenditures from the selected budget. Contact the system administrator.", Toast.LENGTH_LONG).show();
					}
					
					android.widget.TableRow.LayoutParams rowParams = new android.widget.TableRow.LayoutParams(0, android.widget.TableRow.LayoutParams.FILL_PARENT);
					TableRow tableRow = null;
					String valor = "";
					String data = "";
					String estabelecimento = "";
					double totalExpense = 0.0;
					for( Expense expense : expenses ){
						//ADICIONANDO AS DESPESAS QUE NAO POSSUEM SUBCATEGORIA
						sdf = new SimpleDateFormat(Constants.DATETIME_SCREEN_PATTERN);
						data = sdf.format(expense.getDtOcurr());
						estabelecimento = expense.getLocal();
						valor = Converter.getDecimalFormat().format(expense.getAmmount());
						tableRow = adicionarLinhaDespesa(rowParams, data, estabelecimento, valor);					
						//ADICIONANDO NO LAYOUT
						categoriesTableLayout.addView(tableRow);
						totalExpense += expense.getAmmount();
					}
					
					//ADICIONANDO LINHA DE TOTAL
					if( totalExpense > 0 ){
						tableRow = (TableRow) new TableRow(context);
						TextView totalLabelTv = (TextView) new TextView(context);
						TextView espacoTv = (TextView) new TextView(context);
						TextView totalValueTv = (TextView) new TextView(context);
						rowParams.weight=2;
						totalLabelTv.setTypeface(null, Typeface.BOLD);
						totalLabelTv.setTextSize(14);
						totalLabelTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
						totalLabelTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
						totalLabelTv.setLayoutParams(rowParams);
						totalLabelTv.setPadding(25, 0, 0, 0);
						totalLabelTv.setText("Total:");
						tableRow.addView(totalLabelTv);
						rowParams.weight=2;
						espacoTv.setTextSize(14);
						espacoTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
						espacoTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
						espacoTv.setLayoutParams(rowParams);
						espacoTv.setPadding(25, 0, 0, 0);
						espacoTv.setText("");
						tableRow.addView(espacoTv);
						rowParams.weight=2;
						totalValueTv.setTypeface(null, Typeface.BOLD);
						totalValueTv.setTextSize(14);
						totalValueTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
						totalValueTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
						totalValueTv.setLayoutParams(rowParams);
						totalValueTv.setPadding(25, 0, 0, 0);
						totalValueTv.setText(Converter.getDecimalFormat().format(totalExpense));
						tableRow.addView(totalValueTv);
						categoriesTableLayout.addView(tableRow);						
					}
					
					break;
				}
			}
		}
		
		return rowView;
	}


	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v;
		
		//ARMAZENAR O ID DO BUDGET NO PREFERENCES
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		Set<String> ids = sharedPreferences.getStringSet(Constants.BUDGETS_EXPANDED, null);
		if( ids == null ){
			ids = new HashSet<String>();
		}
		//BUSCA NA LISTA DE BUDGETS QUAL FOI A CLICADO
		boolean encontrado = false; 
		for( Budget budget : budgets ){
			if( tv.getText().toString().contains(budget.getName()) ){
				//VERIFICA SE JA NAO EXISTE NO CONJUNTO, SE EXISTIR RETIRA, SENAO INCLUI
				if( ids.size() > 0 ){
					for( String id : ids ){
						if( id.equals(String.valueOf(budget.getId())) ){
							encontrado = true;
							break;
						}
					}
					if( encontrado ){
						ids.remove(String.valueOf(budget.getId()));
					} else{
						ids.add(String.valueOf(budget.getId()));
					}
				} else{
					ids.add(String.valueOf(budget.getId()));
				}
				//JA ENCONTROU ENTAO PODE SAIR DO LOOP
				break;
			}
		}
		editor.putStringSet(Constants.BUDGETS_EXPANDED, ids);
		editor.commit();
		
		//REINICIANDO A LISTVIEW
		ListView listView = (ListView) parent.findViewById(R.id.budgets_listView);
		listView.invalidateViews();
	}

	private TableRow adicionarLinhaDespesa(LayoutParams rowParams, String data, String estabelecimento, String valor){
		
		TableRow tableRow = (TableRow) new TableRow(context);
		TextView dataTv = (TextView) new TextView(context);
		TextView estabelecimentoTv = (TextView) new TextView(context);
		TextView valorTv = (TextView) new TextView(context);
		rowParams.weight=2;
		dataTv.setTextSize(12);
//		dataTv.setHeight(android.view.ViewGroup.LayoutParams.FILL_PARENT);
		dataTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
		dataTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
		dataTv.setLayoutParams(rowParams);
		dataTv.setPadding(25, 0, 0, 0);
		dataTv.setText(data);
		tableRow.addView(dataTv);
		rowParams.weight=6;
//		rowParams.gravity=Gravity.LEFT;
		estabelecimentoTv.setTextSize(12);
		estabelecimentoTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
		estabelecimentoTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
		estabelecimentoTv.setLayoutParams(rowParams);
		estabelecimentoTv.setPadding(25, 0, 0, 0);
		estabelecimentoTv.setText(estabelecimento);
		tableRow.addView(estabelecimentoTv);
		rowParams.weight=2;
		rowParams.gravity=Gravity.RIGHT;
		valorTv.setTextSize(12);
		valorTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
		valorTv.setBackgroundColor(context.getResources().getColor(R.color.background_color));
		valorTv.setLayoutParams(rowParams);
		valorTv.setPadding(25, 0, 0, 0);
		valorTv.setText(valor);
		tableRow.addView(valorTv);
		
		return tableRow;
	}
}

