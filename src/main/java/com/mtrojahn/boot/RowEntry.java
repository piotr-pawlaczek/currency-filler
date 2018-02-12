package com.mtrojahn.boot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RowEntry {

	public RowEntry(LocalDate date, Map<Integer, BigDecimal> values) {
		this.date = date;
		this.values = values;
	}

	private LocalDate date;
	private Map<Integer, BigDecimal> values;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Map<Integer, BigDecimal> getValues() {
		return values;
	}

	public void setValues(Map<Integer, BigDecimal> values) {
		this.values = values;
	}

}
