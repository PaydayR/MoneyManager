package com.paydayr.moneymanager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtil {
	
//	public static void main(String args[]){
//		String texto = "{SANTANDER}Santander Informa: Transacao Visa Electron cartao final 2537 de R$24,29 aprovada em 08/08/13 as 12:33 A PRACA FLORIDA";
//		String texto2 = "{SANTANDER}Santander Informa: Transacao Cartao Mastercard final 3327 de R$ 22,50 aprovada em 11/07/13 as 12:07 SPOLETO";
//		String texto3 = "10/08/13 08:59 BRADESCO Davy: Compra cartao deb. final 9716 de 4,20 realizada no estab. SNACK OFFICE.";
//		String texto4 = "{visa}Santander Informa: Transacao Cartao VISA final 1780 de R$ 19,40 aprovada em 14/03/13 as 12:39 TUTTA GULA RESTAURAN";
//		String texto_itau = "Compra aprovada no seu ITAU UNICLASS VISA final 9611 - VITORIA DROGARIA valor RS 24,54 em 16/04/2013, as 19h24.";
//		String patternValor = "R\\W\\s*[\\d]+[,|.]?\\d+(?=\\s)";
//		String patternData = "\\d{2}/\\d{2}/\\d{2,4}";
//		String patternEstabelecimento = "[A-Z\\s]*$";
//		System.out.println("Hello world!");
//		System.out.println(RegexpUtil.isMatch(patternValor, texto));
//		System.out.println(RegexpUtil.isMatch(patternData, texto2));
//		System.out.println(RegexpUtil.isMatch(patternEstabelecimento, texto));
		
//		RegexpUtil instance = new RegexpUtil();
//	}

	public static boolean isMatch(String regex, String text){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.find(0);
	}
	
	public static String getMatch(String regex, String text){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		if( matcher.find(0) ){
			return matcher.group();
		} else{
			return null;
		}		
	}
}
