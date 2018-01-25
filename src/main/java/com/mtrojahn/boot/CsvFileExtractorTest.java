package com.mtrojahn.boot;

import java.util.List;

import org.junit.Test;

public class CsvFileExtractorTest {
	
	@Test
	public void test() {
		List<String[]> lines = CsvFileExtractor.extractFromFile("C:\\Users\\pawlacze\\Desktop\\ex1.csv");
		
		for(String[] positions : lines) {
			System.out.println(positions[0]);
		}
	}

}
