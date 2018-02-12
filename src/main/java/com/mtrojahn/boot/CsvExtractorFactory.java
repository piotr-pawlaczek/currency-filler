package com.mtrojahn.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mtrojahn.boot.csv.CsvCreativeSalesExtractor;
import com.mtrojahn.boot.csv.CsvEtsyExtractor;
import com.mtrojahn.boot.csv.CsvExtractor;

@Component
public class CsvExtractorFactory {
	@Autowired
	private CsvCreativeSalesExtractor csvBotanicaExtractor;
	
	@Autowired
	private CsvEtsyExtractor csvEtsyExtractor;

	public CsvExtractor getCsvExtractor(String pathToFile) {
		if(pathToFile.toLowerCase().contains("creative market sales")) {
			return csvBotanicaExtractor;
		}
		else if (pathToFile.toLowerCase().contains("etsy_sales")) {
			return csvEtsyExtractor;
		}
		return null;
	}
}
