package com.mtrojahn.boot.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mtrojahn.boot.RowEntry;

@Component
public abstract class CsvExtractor {
	
	public abstract List<RowEntry> extractDataFromCsv(String csvFilePath);

	

	protected Map<String, Integer> parseTemplate(String templatePath) {
		String templateLine;
		Map<String, Integer> columnsMap = new HashMap<>();

		try (BufferedReader brTest = new BufferedReader(new FileReader(templatePath))) {
			templateLine = brTest.readLine();
			String[] columnNames = templateLine.split(":");
			for (int i = 0; i < columnNames.length; i++) {
				columnsMap.put(columnNames[i], i);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return columnsMap;
	}
}