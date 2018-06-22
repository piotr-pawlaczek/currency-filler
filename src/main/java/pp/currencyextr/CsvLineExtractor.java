package pp.currencyextr;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class CsvLineExtractor {

	public static List<String[]> extractFromFile(String pathToFile, char separator) {
		List<String[]> lines = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(pathToFile), separator)) {
			String[] line;
			while ((line = reader.readNext()) != null) {
				lines.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

}
