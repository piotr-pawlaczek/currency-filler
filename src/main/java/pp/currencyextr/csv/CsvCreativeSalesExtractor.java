package pp.currencyextr.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import pp.currencyextr.CsvLineExtractor;
import pp.currencyextr.RowEntry;

@Component
public class CsvCreativeSalesExtractor extends CsvExtractor {

	private static final String DATE_FORMAT = "uuuu-MM-dd";

	private static final String TAXES = "Taxes";
	private static final String EARNINGS = "Earnings";
	private static final String PRICE = "Price";
	private static final String DATE = "Date";

	@Override
	public List<RowEntry> extractDataFromCsv(String csvFilePath) {
		List<RowEntry> entries = new ArrayList<>();

		Map<String, Integer> template = parseTemplate(
				getClass().getClassLoader().getResource("creative_market_sales_template.txt").getFile());
		List<String[]> csvEntries = CsvLineExtractor.extractFromFile(csvFilePath, ';');

		for (int i = 1; i < csvEntries.size(); i++) {//skip header row
			LocalDate date = LocalDate.parse(csvEntries.get(i)[template.get(DATE)],
					DateTimeFormatter.ofPattern(DATE_FORMAT));
			BigDecimal price = new BigDecimal(csvEntries.get(i)[template.get(PRICE)]);
			BigDecimal earning = new BigDecimal(csvEntries.get(i)[template.get(EARNINGS)]);
			BigDecimal tax = new BigDecimal(csvEntries.get(i)[template.get(TAXES)]);
			BigDecimal provision = calculateProvision(price, earning, tax);

			Map<Integer, BigDecimal> values = new HashMap<>();
			values.put(0, price);
			values.put(1, provision);
			values.put(2, tax);
			RowEntry rowEntry = new RowEntry(date, values);

			entries.add(rowEntry);
		}
		return entries;
	}
	
	private BigDecimal calculateProvision(BigDecimal price, BigDecimal earnings, BigDecimal tax) {
		return price.subtract(earnings).subtract(tax);
	}
}
