package com.mtrojahn.boot;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class CsvFileExtractor {

	public static List<String[]> extractFromFile(String pathToFile) {
		CSVReader reader = null;
		List<String[]> lines = new ArrayList<>();
		try {
			reader = new CSVReader(new FileReader(pathToFile));
			String[] line;
			while ((line = reader.readNext()) != null) {
				lines.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println("Unable to close CSV reader. Stacktrace: " + e.getStackTrace());
				}
			}
		}
		return lines;
	}
	
	public static List<LocalDate> extractDatesFromCsvEntries(List<String[]> entries, int columnNumber) {
		List<LocalDate> dates = new ArrayList<>();
		for(String[] line : entries) {
			dates.add(LocalDate.parse(line[columnNumber]));
		}
		return dates;
	}

}
