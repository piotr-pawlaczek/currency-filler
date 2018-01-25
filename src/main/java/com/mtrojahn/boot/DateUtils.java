package com.mtrojahn.boot;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class DateUtils {
	
	public static Pair<LocalDate, LocalDate> retrieveEarliestDate(List<LocalDate> dates) {
		LocalDate earliest = dates.stream().min((d1, d2) -> d1.compareTo(d2)).get();
		LocalDate latest = dates.stream().max((d1, d2) -> d1.compareTo(d2)).get();
		
		return Pair.of(earliest, latest);
	}
	
}
