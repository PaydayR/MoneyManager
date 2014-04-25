package com.paydayr.moneymanager.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.util.Constants;
import com.paydayr.moneymanager.util.Converter;

public class CategoriesListAdapter extends ArrayAdapter<Category> implements OnClickListener{
	
	private Context context;
	private List<Category> categorias;
	private ViewGroup parent;
	private SimpleDateFormat sdf;
	
	public CategoriesListAdapter(Context context, int layout, List<Category> categorias) {
		super(context, layout, categorias);
		this.context = context;
		this.categorias = categorias;
		sdf = new SimpleDateFormat(Constants.DATETIME_SCREEN_PATTERN);
	}


	@SuppressWarnings("deprecation")
	@TargetApi(11)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		this.parent = parent;
		MoneyManagerFacade moneyManagerFacade = new MoneyManagerFacade();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_categories, parent, false);
		
		TableLayout categoriesTableLayout = (TableLayout) rowView.findViewById(R.id.categories_tableLayout);
		
		//PRIMEIRA LINHA ABAIXO (CATEGORIA E CHECKBOX)
		TextView categoria = (TextView) rowView.findViewById(R.id.categoria);
		categoria.setBackgroundColor(context.getResources().getColor(R.color.list_main_color));
		categoria.setOnClickListener(this);
		categoria.setText(categorias.get(position).getCategoryName());
		
		CheckBox checkbox = (CheckBox) 	rowView.findViewById(R.id.checkBox);
		checkbox.setId(Double.valueOf(categorias.get(position).getId()).intValue()+2000);
		
		//OBTENDO PREFERENCES PARA VERIFICAR QUAIS CATEGORIAS EXPANDIR
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Set<String> ids = sharedPreferences.getStringSet(Constants.CATEGORIES_EXPANDED, null);
		
		//DEMAIS LINHAS ABAIXO COM DESPESAS OU NAO. MONTAGEM DINAMICA:
		boolean encontrado = false;
		if( ids != null ){
			for( String id : ids ){
				if( categorias.get(position).getId() == Integer.parseInt(id) ){
					//CATEGORIA EXPANDIDA ENCONTRADA. CRIAR OS APONTAMENTOS PARA A CATEGORIA E PARA AS SUBCATEGORIAS.
					
					//BUSCAR TODAS AS DESPESAS PARA A CATEGORIA EM QUESTAO
					List<Expense> expenses = new ArrayList<Expense>();
					try {
						expenses = moneyManagerFacade.getExpenseListByCategory(context, categorias.get(position), MainActivity.PERIODO);
					} catch (BusinessException e) {
						Log.e(this.getClass().getName(), e.getMessage());
						Toast.makeText(context, "[CategoriesListAdapter] Error retrieving expenditures from the selected category. Contact the system administrator.", Toast.LENGTH_LONG).show();
					}
					
					android.widget.TableRow.LayoutParams rowParams = new android.widget.TableRow.LayoutParams(0, android.widget.TableRow.LayoutParams.FILL_PARENT);
					TableRow tableRow = null;
					String valor = "";
					String data = "";
					String estabelecimento = "";
					double totalExpense = 0.0;
					for( Expense expense : expenses ){
						if( expense.getCategory().getSubCategories() == null || expense.getCategory().getSubCategories().size() == 0 ){
							//ADICIONANDO AS DESPESAS QUE NAO POSSUEM SUBCATEGORIA
							data = sdf.format(expense.getDtOcurr());
							estabelecimento = expense.getLocal();
							valor = Converter.getDecimalFormat().format(expense.getAmmount());
							tableRow = adicionarLinhaDespesa(rowParams, data, estabelecimento, valor);					
							//ADICIONANDO NO LAYOUT
							categoriesTableLayout.addView(tableRow);
							totalExpense += expense.getAmmount();
						}
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
					
					//ADICONANDO AS DEMAIS DESPESAS COM SUBCATEGORIA
					rowParams.weight=1;
					TextView subCategoria = null;
					for( SubCategory subCat : categorias.get(position).getSubCategories() ){
						
						//CRIANDO ROW
						tableRow = (TableRow) new TableRow(context);
						
						//CRIANDO TEXTVIEW
						subCategoria = (TextView) new TextView(context);
						subCategoria.setLayoutParams(rowParams);
						subCategoria.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
						subCategoria.setText("- " + subCat.getSubCategoryName());
						subCategoria.setTextSize(14);
						subCategoria.setTextColor(context.getResources().getColor(R.color.font_basic_color));
						subCategoria.setPadding(25, 0, 0, 0);
						tableRow.addView(subCategoria);
						
						categoriesTableLayout.addView(tableRow);
						
						//ADICIONANDO AS DEMAIS DESPESAS PARA A SUBCATEGORIA
						totalExpense = 0.0;
						for( Expense expense : expenses ){
							if( expense.getCategory().getSubCategories() != null && expense.getCategory().getSubCategories().size() > 0 && expense.getCategory().getSubCategories().get(0).getId() == subCat.getId() ){
								//ADICIONANDO AS DESPESAS QUE NAO POSSUEM SUBCATEGORIA
								data = sdf.format(expense.getDtOcurr());
								estabelecimento = expense.getLocal();
								valor = Converter.getDecimalFormat().format(expense.getAmmount());
								tableRow = adicionarLinhaDespesa(rowParams, data, estabelecimento, valor);					
								//ADICIONANDO NO LAYOUT
								categoriesTableLayout.addView(tableRow);
								totalExpense += expense.getAmmount();
							}
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
					}
					
					encontrado = true;
					
					//ENCONTROU PODE SAIR DO LOOP
					break;
				}
			}
		}

		if( !encontrado ){

			android.widget.TableRow.LayoutParams rowParams = new android.widget.TableRow.LayoutParams(android.widget.TableRow.LayoutParams.WRAP_CONTENT, android.widget.TableRow.LayoutParams.WRAP_CONTENT);
			rowParams.weight=1;
			TextView subCategoria = null;
			TableRow tableRow = null;
			for( SubCategory subCat : categorias.get(position).getSubCategories() ){
				
				//CRIANDO ROW
				tableRow = (TableRow) new TableRow(context);
				
				//CRIANDO TEXTVIEW
				subCategoria = (TextView) new TextView(context);
				subCategoria.setLayoutParams(rowParams);
				subCategoria.setBackgroundColor(context.getResources().getColor(R.color.list_second_color));
				subCategoria.setText("- " + subCat.getSubCategoryName());
				subCategoria.setTextSize(14);
				subCategoria.setTextColor(context.getResources().getColor(R.color.font_basic_color));
				subCategoria.setPadding(25, 0, 0, 0);
				tableRow.addView(subCategoria);
				
				categoriesTableLayout.addView(tableRow);
			}			
		}
		
		return rowView;
	}


	@TargetApi(11)
	@Override
	public void onClick(View v) {
		TextView tv = (TextView) v;
		
		//ARMAZENAR O ID DA CATEGORIA NO PREFERENCES
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		Set<String> ids = sharedPreferences.getStringSet(Constants.CATEGORIES_EXPANDED, null);
		if( ids == null ){
			ids = new HashSet<String>();
		}
		//BUSCA NA LISTA DE CATEGORIAS QUAL FOI A CLICADA
		boolean encontrado = false; 
		for( Category category : categorias ){
			if( category.getCategoryName().equals(tv.getText().toString()) ){
				//VERIFICA SE JA NAO EXISTE NO CONJUNTO, SE EXISTIR RETIRA, SENAO INCLUI
				if( ids.size() > 0 ){
					for( String id : ids ){
						if( id.equals(String.valueOf(category.getId())) ){
							encontrado = true;
							break;
						}
					}
					if( encontrado ){
						ids.remove(String.valueOf(category.getId()));
					} else{
						ids.add(String.valueOf(category.getId()));
					}
				} else{
					ids.add(String.valueOf(category.getId()));
				}
				//JA ENCONTROU ENTAO PODE SAIR DO LOOP
				break;
			}
		}
		editor.putStringSet(Constants.CATEGORIES_EXPANDED, ids);
		editor.commit();
		
		//REINICIANDO A LISTVIEW
		ListView listView = (ListView) parent.findViewById(R.id.categories_listView);
		listView.invalidateViews();
	}

	private TableRow adicionarLinhaDespesa(LayoutParams rowParams, String data, String estabelecimento, String valor){
		
		TableRow tableRow = (TableRow) new TableRow(context);
		TextView dataTv = (TextView) new TextView(context);
		TextView estabelecimentoTv = (TextView) new TextView(context);
		TextView valorTv = (TextView) new TextView(context);
		rowParams.weight=2;
		dataTv.setTextSize(12);
		dataTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
//		dataTv.setBackgroundColor(context.getResources().getColor(R.color.font_basic_color));
		dataTv.setLayoutParams(rowParams);
		dataTv.setPadding(25, 0, 0, 0);
		dataTv.setText(data);
		tableRow.addView(dataTv);
		rowParams.weight=6;
		estabelecimentoTv.setTextSize(12);
		estabelecimentoTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
//		estabelecimentoTv.setBackgroundColor(context.getResources().getColor(R.color.font_basic_color));
		estabelecimentoTv.setLayoutParams(rowParams);
		estabelecimentoTv.setPadding(25, 0, 0, 0);
		estabelecimentoTv.setText(estabelecimento);
		tableRow.addView(estabelecimentoTv);
		rowParams.weight=2;
		rowParams.gravity=Gravity.RIGHT;
		valorTv.setTextSize(12);
		valorTv.setTextColor(context.getResources().getColor(R.color.font_basic_color));
//		valorTv.setBackgroundColor(context.getResources().getColor(R.color.font_basic_color));
		valorTv.setLayoutParams(rowParams);
		valorTv.setPadding(25, 0, 0, 0);
		valorTv.setText(valor);
		tableRow.addView(valorTv);
		
		return tableRow;
	}
}
