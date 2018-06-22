package pp.currencyextr;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

/**
 * Represents row entry for given date. Map stores values for dates where
 * integer key points to the sheet where value is stored
 * 
 * @author pawlacze
 *
 */
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
		this.values = Collections.unmodifiableMap(values);
	}

}
