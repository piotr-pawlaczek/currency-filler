package com.mtrojahn.boot;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class CsvFileExtractorTest {
	
	@Test
	public void test() {
		List<String[]> lines = CsvFileExtractor.extractFromFile("C:\\Users\\pawlacze\\Desktop\\ex1.csv");

		for (String[] positions : lines) {
			System.out.println(positions[0]);
		}
	}
	
	@Test
	public void test2() {
		List<String[]> lines = CsvFileExtractor.extractFromFile("C:\\Users\\pawlacze\\Desktop\\ex1.csv");
		List<LocalDate> extractedDates = CsvFileExtractor.extractDatesFromCsvEntries(lines, 3);
		
		for(LocalDate date : extractedDates) {
			System.out.println(date);
		}
	}
}
