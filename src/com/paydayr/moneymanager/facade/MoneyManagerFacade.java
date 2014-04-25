package com.paydayr.moneymanager.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Context;
import com.paydayr.moneymanager.db.BudgetsDAO;
import com.paydayr.moneymanager.db.CategoriesDAO;
import com.paydayr.moneymanager.db.ExpensesDAO;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.holders.TotalByMonthHolder;
import com.paydayr.moneymanager.model.Budget;
import com.paydayr.moneymanager.model.Category;
import com.paydayr.moneymanager.model.Expense;
import com.paydayr.moneymanager.model.SubCategory;
import com.paydayr.moneymanager.service.NotificationService;
import com.paydayr.moneymanager.util.Constants;

public class MoneyManagerFacade {

	private CategoriesDAO categoriesDao;
	private BudgetsDAO budgetsDao;
	private ExpensesDAO expensesDao;
	
	private SimpleDateFormat sdf;
	
	public MoneyManagerFacade(){
		sdf = new SimpleDateFormat(Constants.DATETIME_PATTERN);
	}

	public List<Category> getCategoryList(Context context) {
		categoriesDao = new CategoriesDAO(context);
		List<Category> categorias = categoriesDao.getAllCategories();
		return categorias;
	}

	public void addCategory(Context context, String name) {
		categoriesDao = new CategoriesDAO(context);
		categoriesDao.createCategory(name);
	}

	public void addSubCategory(Context context, Category category,
			String subCategory) {
		categoriesDao = new CategoriesDAO(context);
		categoriesDao.addSubCategory(category, subCategory);
	}

	public Category getCategory(Context context, String categoryName) {
		categoriesDao = new CategoriesDAO(context);
		Category category = categoriesDao.getCategoryByName(categoryName);
		return category;
	}

	public List<Budget> getBudgetList(Context context) throws BusinessException {
		budgetsDao = new BudgetsDAO(context);
		List<Budget> budgets = budgetsDao.getAllBudgets();
		return budgets;
	}

	public void addBudget(Context context, String budgetName, Double ammount,
			String categorySelected, String subCategorySelected, Date dtOcurr, int renovacao)
			throws BusinessException {
		categoriesDao = new CategoriesDAO(context);
		Category category = categoriesDao.getCategoryByName(categorySelected);
		SubCategory subCategory = null;
		if (subCategorySelected != null
				&& !subCategorySelected.equals("None")) {
			for (SubCategory subCat : category.getSubCategories()) {
				if (subCategorySelected.equals(subCat.getSubCategoryName())) {
					subCategory = subCat;
				}
			}
		}
		category.setSubCategories(new ArrayList<SubCategory>());
		if (subCategory != null) {
			category.getSubCategories().add(subCategory);
		}
		Budget budget = new Budget();
		budget.setTotal(ammount);
		budget.setAmmount(ammount);
		budget.setCategory(category);
		budget.setName(budgetName);
		budget.setRenewId(renovacao);
		budget.setDtStart(dtOcurr);
		
		// adiciona o budget no banco de dados
		budgetsDao = new BudgetsDAO(context);
		budgetsDao.createBudget(budget);
		
		// resgatando budget recem adicionado com ID
		budget = budgetsDao.getBudgetByName(budget.getName());
		
		// verifica se existe expense com data maior ou igual a data do budget, se existir seta o budget na despesa e subtrai o valor do total
		// somente se budget tiver categoria/subcategoria
		expensesDao = new ExpensesDAO(context);
		// subtrai 1 dia da data para incluir as despesas da data escolhida
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtOcurr);
		cal.add(Calendar.DATE, -1);
		List<Expense> expenses = expensesDao.getExpenseByCategory(category, sdf.format(cal.getTime()), null);
		boolean encontrado = false;
		for( Expense expense : expenses ){
			if( subCategorySelected != null && !subCategorySelected.equals("None") ){
				if( expense.getCategory().getSubCategories() != null && expense.getCategory().getSubCategories().size() > 0 ){
					encontrado = false;
					for( SubCategory sub : expense.getCategory().getSubCategories() ){
						if( sub.getSubCategoryName().equals(subCategorySelected) ) {
							encontrado = true;
							break;
						}
					}
					if( encontrado ){
						expense.setBudget(budget);
						budget.setAmmount(budget.getAmmount() - expense.getAmmount());	
					}
				}
			} else{
				expense.setBudget(budget);
				budget.setAmmount(budget.getAmmount() - expense.getAmmount());
			}
			// atualiza o expense no banco
			expensesDao.updateExpense(expense);
		}
		
		// atualiza o budget com novo ammount restante
		budgetsDao.updateBudget(budget);
	}

	public void addExpense(Context context, String local, Double ammount,
			String budgetSelected, String categorySelected,
			String subCategorySelected, String novaCategoria, Date dtOcurr,
			int renovacao) throws BusinessException {

		Expense expense = new Expense();
		expense.setAmmount(ammount);

		if (dtOcurr == null) {
			throw new BusinessException("Invalid date.");
		}
		expense.setDtOcurr(dtOcurr);

		if (local != null) {
			expense.setLocal(local);
		}

		expensesDao = new ExpensesDAO(context);
		if (categorySelected != null && !categorySelected.equals("None")) {
			categoriesDao = new CategoriesDAO(context);
			SubCategory subCategory = null;
			Category category = categoriesDao
					.getCategoryByName(categorySelected);
			for (SubCategory subCat : category.getSubCategories()) {
				if (subCategorySelected != null
						&& subCategorySelected.equals(subCat
								.getSubCategoryName())) {
					subCategory = subCat;
				}
			}
			category.setSubCategories(new ArrayList<SubCategory>());
			if (subCategory != null) {
				category.getSubCategories().add(subCategory);
			}
			expense.setCategory(category);
		} else if (novaCategoria != null && novaCategoria.trim().length() > 0) {
			addCategory(context, novaCategoria);
			categoriesDao = new CategoriesDAO(context);
			expense.setCategory(categoriesDao.getCategoryByName(novaCategoria));
		}
		if (budgetSelected != null && !budgetSelected.equals("None")) {
			budgetsDao = new BudgetsDAO(context);
			expense.setBudget(budgetsDao.getBudgetByName(budgetSelected));
			expense.getBudget().setAmmount(
					expense.getBudget().getAmmount() - ammount);
			budgetsDao.updateBudget(expense.getBudget());
		}
		expense.setRenewId(renovacao);

		expensesDao.createExpense(expense);

		// ANALISANDO LIMITES
		if (expense.getBudget() != null) {
			verifyBudgetLimits(context, expense.getBudget());
		}
	}

	public void editExpenses(Context context, String budgetSelected,
			String categorySelected, String subCategorySelected,
			String novaCategoria, List<Expense> expensesEditList, int renovacao)
			throws BusinessException {

		expensesDao = new ExpensesDAO(context);
		// CONFIGURA LISTA DE EXPENSES COM CATEGORY NOVA, SELECIONADA OU BUDGET
		// NOVO (atribuindo nulo p/ "None")
		if (novaCategoria != null && novaCategoria.trim().length() > 0) {
			addCategory(context, novaCategoria);
			categoriesDao = new CategoriesDAO(context);

			for (Expense expense : expensesEditList) {
				expense.setCategory(categoriesDao
						.getCategoryByName(novaCategoria));
			}
		} else if (categorySelected != null
				&& !categorySelected.equals("None")) {
			categoriesDao = new CategoriesDAO(context);
			SubCategory subCategory = null;
			Category category = categoriesDao
					.getCategoryByName(categorySelected);
			for (SubCategory subCat : category.getSubCategories()) {
				if (subCategorySelected != null
						&& subCategorySelected.equals(subCat
								.getSubCategoryName())) {
					subCategory = subCat;
				}
			}
			category.setSubCategories(new ArrayList<SubCategory>());
			if (subCategory != null) {
				category.getSubCategories().add(subCategory);
			}

			for (Expense expense : expensesEditList) {
				expense.setCategory(category);
			}
		} else {
			for (Expense expense : expensesEditList) {
				expense.setCategory(null);
			}
		}
		if (budgetSelected != null && !budgetSelected.equals("None")) {
			budgetsDao = new BudgetsDAO(context);
			for (Expense expense : expensesEditList) {
				if (categorySelected == null
						|| categorySelected.equals("None")) {
					expense.setCategory(null);
				}
				// CREDITAR VALOR EM BUDGET ANTIGO ANTES DE TROCAR
				if (expense.getBudget() != null) {
					expense.getBudget().setAmmount(
							expense.getBudget().getAmmount()
									+ expense.getAmmount());
					budgetsDao.updateBudget(expense.getBudget());
				}
				expense.setBudget(budgetsDao.getBudgetByName(budgetSelected));
				expense.getBudget()
						.setAmmount(
								expense.getBudget().getAmmount()
										- expense.getAmmount());
				budgetsDao.updateBudget(expense.getBudget());
			}
		} else {
			// NAO TEM BUDGET LIMPA EXPENSES E CREDITA VALORES EM BUDGETS
			// ANTIGOS.
			for (Expense expense : expensesEditList) {
				if (expense.getBudget() != null) {
					expense.getBudget().setAmmount(
							expense.getBudget().getAmmount()
									+ expense.getAmmount());
					budgetsDao.updateBudget(expense.getBudget());
					expense.setBudget(null);
				}
			}
		}

		// ATUALIZA EXPENSES
		for (Expense expense : expensesEditList) {
			expense.setRenewId(renovacao);
			expensesDao.updateExpense(expense);
			// ANALISANDO LIMITES
			if (expense.getBudget() != null) {
				verifyBudgetLimits(context, expense.getBudget());
			}
		}

	}

	public List<Expense> getExpenseList(Context context, int periodo)
			throws BusinessException {

		expensesDao = new ExpensesDAO(context);
		String dataInicio = null;
		String dataFim = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = null;

		try {
			switch (periodo) {
			case 1:
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				calendar.add(Calendar.DATE, -1);
				dataInicio = sdf.format(calendar.getTime());
				break;
			case 2:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				Date dataTmp = sdf.parse(sdf.format(new Date()));
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(dataTmp);
				break;
			case 3:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.MONTH, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(calendar.getTime());

				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.DATE, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataFim = sdf.format(calendar.getTime());
				break;
			case 4:
				sdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
				dataInicio = sdf.format(new Date());
				break;
			}
		} catch (ParseException e) {
			throw new BusinessException(
					"Error retrieving period. Contact the system administrator.");
		}
		List<Expense> expenses = expensesDao.getExpensesByPeriod(dataInicio,
				dataFim);

		// ORDENAR
		Collections.sort(expenses, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				return rhs.getDtOcurr().compareTo(lhs.getDtOcurr());
			}
		});

		return expenses;
	}

	public List<Expense> getExpenseListByName(Context context, String local)
			throws BusinessException {
		expensesDao = new ExpensesDAO(context);
		List<Expense> expenses = expensesDao.getExpenseByLocal(local);
		// ORDENAR
		Collections.sort(expenses, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				return rhs.getDtOcurr().compareTo(lhs.getDtOcurr());
			}
		});
		return expenses;
	}

	public List<Expense> getExpenseListByCategory(Context context,
			Category category, int periodo) throws BusinessException {
		expensesDao = new ExpensesDAO(context);

		String dataInicio = null;
		String dataFim = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = null;

		try {
			switch (periodo) {
			case 1:
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				calendar.add(Calendar.DATE, -1);
				dataInicio = sdf.format(calendar.getTime());
				break;
			case 2:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				Date dataTmp = sdf.parse(sdf.format(new Date()));
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(dataTmp);
				break;
			case 3:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.MONTH, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(calendar.getTime());

				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.DATE, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataFim = sdf.format(calendar.getTime());
				break;
			case 4:
				sdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
				dataInicio = sdf.format(new Date());
				break;
			}
		} catch (ParseException e) {
			throw new BusinessException(
					"Error retrieving period. Contact the system administrator");
		}

		List<Expense> expenses = expensesDao.getExpenseByCategory(category,
				dataInicio, dataFim);

		// ORDENAR
		Collections.sort(expenses, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				return rhs.getDtOcurr().compareTo(lhs.getDtOcurr());
			}
		});

		return expenses;
	}

	public List<Expense> getExpenseListByBudget(Context context, Budget budget,
			int periodo) throws BusinessException {
		expensesDao = new ExpensesDAO(context);

		String dataInicio = null;
		String dataFim = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = null;

		try {
			switch (periodo) {
			case 1:
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				calendar.add(Calendar.DATE, -1);
				dataInicio = sdf.format(calendar.getTime());
				break;
			case 2:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				Date dataTmp = sdf.parse(sdf.format(new Date()));
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(dataTmp);
				break;
			case 3:
				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.MONTH, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataInicio = sdf.format(calendar.getTime());

				sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
				calendar.setTime(sdf.parse(sdf.format(new Date())));
				calendar.add(Calendar.DATE, -1);
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dataFim = sdf.format(calendar.getTime());
				break;
			case 4:
				sdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
				dataInicio = sdf.format(new Date());
				break;
			}
		} catch (ParseException e) {
			throw new BusinessException(
					"Error retrieving period. Contact the system administrator");
		}

		List<Expense> expenses = expensesDao.getExpenseByBudget(budget,
				dataInicio, dataFim);

		// ORDENAR
		Collections.sort(expenses, new Comparator<Expense>() {
			@Override
			public int compare(Expense lhs, Expense rhs) {
				return rhs.getDtOcurr().compareTo(lhs.getDtOcurr());
			}
		});

		return expenses;
	}

	public void removeCategory(Context context, Category category)
			throws BusinessException {
		expensesDao = new ExpensesDAO(context);
		budgetsDao = new BudgetsDAO(context);
		categoriesDao = new CategoriesDAO(context);

		List<Budget> budgets = budgetsDao.getBudgetsByCategory(category);
		for (Budget budget : budgets) {
			removeBudget(context, budget);
		}
		// RETIRAR O RELACIONAMENTO DA CATEGORIA NAS DESPESAS
		List<Expense> expenses = getExpenseListByCategory(context, category, 4);
		for (Expense expense : expenses) {
			expense.setCategory(null);
			expensesDao.updateExpense(expense);
		}
		categoriesDao.deleteCategory(category);
	}

	public void removeExpense(Context context, Expense expense)
			throws BusinessException {
		expensesDao = new ExpensesDAO(context);
		// ADICIONAR VALOR AO BUDGET ANTES DE REMOVER
		if (expense.getBudget() != null) {
			expense.getBudget().setAmmount(
					expense.getBudget().getAmmount() + expense.getAmmount());
			budgetsDao.updateBudget(expense.getBudget());
		}
		expensesDao.deleteExpense(expense);
	}

	public void removeBudget(Context context, Budget budget)
			throws BusinessException {
		expensesDao = new ExpensesDAO(context);
		budgetsDao = new BudgetsDAO(context);

		// REMOVER O RELACIONAMENTO DO BUDGET NAS DESPESAS
		List<Expense> expenses = getExpenseListByBudget(context, budget, 4);
		if (expenses != null && expenses.size() > 0) {
			for (Expense expense : expenses) {
				expense.setBudget(null);
				expensesDao.updateExpense(expense);
			}
		}
		budgetsDao.deleteBudget(budget);
	}

	public void verifyBudgetLimits(Context context, Budget budget) {
		// ALERTAR QUANDO CHEGAR EM 50%
		if (budget.getAmmount() < budget.getTotal() / 2) {
			NotificationService.sendNotification(context,
					"MoneyManager Alert", "MoneyManager",
					"You've reached 50% of your limit.", ((int) budget.getId()));
		}
		// ALERTAR QUANDO CHEGAR EM 25%
		if (budget.getAmmount() < budget.getTotal() / 4) {
			NotificationService.sendNotification(context,
					"MoneyManager Alert", "MoneyManager",
					"You've reached 75% of your limit.", ((int) budget.getId()));
		}
		// ALERTAR QUANDO CHEGAR EM 10%
		if (budget.getAmmount() < budget.getTotal() / 10) {
			NotificationService.sendNotification(context,
					"MoneyManager Alert", "MoneyManager",
					"You've reached 90% of your limit.", ((int) budget.getId()));
		}
		// ALERTAR QUANDO CHEGAR EM 0%
		if (budget.getAmmount() <= 0) {
			NotificationService.sendNotification(context,
					"MoneyManager Alert", "MoneyManager",
					"You've reached 100% of your limit.", ((int) budget.getId()));
		}
	}

	public List<TotalByMonthHolder> getTotalByMonth(Context context)
			throws BusinessException {
		expensesDao = new ExpensesDAO(context);
		List<TotalByMonthHolder> totais = new ArrayList<TotalByMonthHolder>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = null;

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.YEAR, -1);
		String dataInicio = sdf.format(calendar.getTime());

		totais = expensesDao.getExpensesTotalMonthly(dataInicio);

		return totais;
	}

	public void executarRenovacoes(Context context) throws BusinessException {
		// BUSCAR BUDGETS PACIVEIS DE RENOVACAO.
		// REGRA: BUSCAR TODOS COM RENEW_ID = 1 (DIARIO) e dt_start < now
		// OU BUSCAR TODOS COM RENEW_ID = 2 (SEMANAL) E DT_START < NOW - 7
		// OU BUSCAR TODOS COM RENEW_ID = 3 (MENSAL) E DT_START <= MES - 1
		// OU BUSCAR TODOS COM RENEW_ID = 4 (ANUAL) E DT_START <= ANO -1
		// PARA TODOS QUE RETORNAR RECRIAR

		budgetsDao = new BudgetsDAO(context);

		String dataInicio = null;
		SimpleDateFormat sdf = null;

		// RENOVACAO DIARIOS
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar.setTime(new Date());
		dataInicio = sdf.format(calendar.getTime());
		List<Budget> diarios = budgetsDao.getBudgetsToRenew(dataInicio, 1);
		for (Budget budget : diarios) {
			budget.setDtStart(calendar.getTime());
			budget.setAmmount(budget.getTotal());
			budgetsDao.deleteBudget(budget);
			budgetsDao.createBudget(budget);
		}

		// RENOVACAO SEMANAIS
		calendar2.setTime(new Date(0));
		calendar2.add(Calendar.DATE, -7);
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -6);
		dataInicio = sdf.format(calendar.getTime());
		List<Budget> semanais = budgetsDao.getBudgetsToRenew(dataInicio, 2);
		for (Budget budget : semanais) {
			do {
				calendar.setTime(budget.getDtStart());
				calendar.add(Calendar.DATE, 7);
				budget.setDtStart(calendar.getTime());
			} while (budget.getDtStart().before(calendar2.getTime()));
			budget.setAmmount(budget.getTotal());
			budgetsDao.deleteBudget(budget);
			budgetsDao.createBudget(budget);
		}

		// RENOVACAO MENSAIS
		calendar2.setTime(new Date());
		calendar2.add(Calendar.MONTH, -1);
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.add(Calendar.DATE, 1);
		dataInicio = sdf.format(calendar.getTime());
		List<Budget> mensais = budgetsDao.getBudgetsToRenew(dataInicio, 3);
		for (Budget budget : mensais) {
			do {
				calendar.setTime(budget.getDtStart());
				calendar.add(Calendar.MONTH, 1);
				budget.setDtStart(calendar.getTime());
			} while (budget.getDtStart().before(calendar2.getTime()));
			budget.setAmmount(budget.getTotal());
			budgetsDao.deleteBudget(budget);
			budgetsDao.createBudget(budget);
		}

		// RENOVACAO ANUAIS
		calendar2.setTime(new Date());
		calendar2.add(Calendar.YEAR, -1);
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, -1);
		calendar.add(Calendar.DATE, 1);
		dataInicio = sdf.format(calendar.getTime());
		List<Budget> anuais = budgetsDao.getBudgetsToRenew(dataInicio, 4);
		for (Budget budget : anuais) {
			do {
				calendar.setTime(budget.getDtStart());
				calendar.add(Calendar.YEAR, 1);
				budget.setDtStart(calendar.getTime());
			} while (budget.getDtStart().before(calendar2.getTime()));
			budget.setAmmount(budget.getTotal());
			budgetsDao.deleteBudget(budget);
			budgetsDao.createBudget(budget);
		}

		// /////////////////////////////////////////////////////////////////////////////////////////
		// EXPENSES
		// /////////////////////////////////////////////////////////////////////////////////////////

		expensesDao = new ExpensesDAO(context);

		try {

			// RENOVACAO DIARIOS
			calendar2.setTime(new Date());
			calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			dataInicio = sdf.format(calendar.getTime());
			List<Expense> expensesDiarios = expensesDao.getExpensesToRenew(
					dataInicio, 1);
			for (Expense expense : expensesDiarios) {
				do {
					expense.setRenewed(true);
					expensesDao.updateExpense(expense);
					calendar.setTime(expense.getDtOcurr());
					calendar.add(Calendar.DATE, 1);
					expense.setDtOcurr(calendar.getTime());
					expense.setRenewed(false);
					expense = expensesDao.createExpense(expense);
				} while (sdf.parse(sdf.format(expense.getDtOcurr())).before(
						sdf.parse(sdf.format(calendar2.getTime()))));
			}

			// RENOVACAO SEMANAIS
			calendar2.setTime(new Date());
			calendar2.add(Calendar.DATE, -7);
			calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -6);
			dataInicio = sdf.format(calendar.getTime());
			List<Expense> expensesSemanais = expensesDao.getExpensesToRenew(
					dataInicio, 2);
			for (Expense expense : expensesSemanais) {
				do {
					expense.setRenewed(true);
					expensesDao.updateExpense(expense);
					calendar.setTime(expense.getDtOcurr());
					calendar.add(Calendar.DATE, 7);
					expense.setDtOcurr(calendar.getTime());
					expense.setRenewed(false);
					expense = expensesDao.createExpense(expense);
				} while (sdf.parse(sdf.format(expense.getDtOcurr())).before(
						sdf.parse(sdf.format(calendar2.getTime()))));
			}

			// RENOVACAO MENSAIS
			calendar2.setTime(new Date());
			calendar2.add(Calendar.MONTH, -1);
			calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			calendar.add(Calendar.DATE, 1);
			dataInicio = sdf.format(calendar.getTime());
			List<Expense> expensesMensais = expensesDao.getExpensesToRenew(
					dataInicio, 3);
			for (Expense expense : expensesMensais) {
				do {
					expense.setRenewed(true);
					expensesDao.updateExpense(expense);
					calendar.setTime(expense.getDtOcurr());
					calendar.add(Calendar.MONTH, 1);
					expense.setDtOcurr(calendar.getTime());
					expense.setRenewed(false);
					expense = expensesDao.createExpense(expense);
				} while (sdf.parse(sdf.format(expense.getDtOcurr())).before(
						sdf.parse(sdf.format(calendar2.getTime()))));
			}

			// RENOVACAO ANUAIS
			calendar2.setTime(new Date());
			calendar2.add(Calendar.YEAR, -1);
			calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.YEAR, -1);
			calendar.add(Calendar.DATE, 1);
			dataInicio = sdf.format(calendar.getTime());
			List<Expense> expensesAnuais = expensesDao.getExpensesToRenew(
					dataInicio, 4);
			for (Expense expense : expensesAnuais) {
				do {
					calendar.setTime(expense.getDtOcurr());
					calendar.add(Calendar.YEAR, 1);
					expense.setDtOcurr(calendar.getTime());
					expense.setRenewed(true);
					expensesDao.updateExpense(expense);
					expense.setRenewed(false);
					expense = expensesDao.createExpense(expense);
				} while (sdf.parse(sdf.format(expense.getDtOcurr())).before(
						sdf.parse(sdf.format(calendar2.getTime()))));
			}

		} catch (ParseException e) {
			throw new BusinessException(
					"[MoneyManagerFacade.executarRenovacoes] Error retrieving dates.");
		}

	}
}
