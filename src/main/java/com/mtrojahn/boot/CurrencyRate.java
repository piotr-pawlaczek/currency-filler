package com.mtrojahn.boot;

public class CurrencyRate {
	private String table;
	private String currency;
	private String code;
	private Rate[] rates;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Rate[] getRates() {
		return rates;
	}

	public void setRates(Rate[] rates) {
		this.rates = rates;
	}

}
