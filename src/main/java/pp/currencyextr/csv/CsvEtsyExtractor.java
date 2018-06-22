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
public class CsvEtsyExtractor extends CsvExtractor {

	private static final char SEPARATOR = ',';
	private static final String DATE_FORMAT = "MM/dd/uuuu";

	@Override
	public List<RowEntry> extractDataFromCsv(String csvFilePath) {
		List<RowEntry> entries = new ArrayList<>();
		Map<String, Integer> template = parseTemplate(
				getClass().getClassLoader().getResource("etsy_template.txt").getFile());
		List<String[]> csvEntries = CsvLineExtractor.extractFromFile(csvFilePath, SEPARATOR);

		for (int i = 1; i < csvEntries.size(); i++) {
			LocalDate date = LocalDate.parse(csvEntries.get(i)[template.get("Funds Available")],
					DateTimeFormatter.ofPattern(DATE_FORMAT));
			BigDecimal grossAmount = new BigDecimal(csvEntries.get(i)[template.get("Gross Amount")]);
			BigDecimal netAmount = new BigDecimal(csvEntries.get(i)[template.get("Net Amount")]);
			BigDecimal fee = grossAmount.subtract(netAmount);

			Map<Integer, BigDecimal> values = new HashMap<>();
			values.put(0, grossAmount);
			values.put(1, fee);

			RowEntry rowEntry = new RowEntry(date, values);

			entries.add(rowEntry);
		}

		return entries;
	}

}
