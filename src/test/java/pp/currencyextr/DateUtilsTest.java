package pp.currencyextr;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class DateUtilsTest {
	@Test
	public void shouldReturnMaxAndMinDateTupleWhenGivenListOfFewDates() {
		// given
		LocalDate date1 = LocalDate.of(2017, Month.AUGUST, 18);
		LocalDate date2 = LocalDate.of(2017, Month.AUGUST, 26);
		LocalDate date3 = LocalDate.of(2017, Month.DECEMBER, 22);
		LocalDate date4 = LocalDate.of(2017, Month.DECEMBER, 24);
		LocalDate date5 = LocalDate.of(2017, Month.DECEMBER, 30);
		LocalDate date6 = LocalDate.of(2018, Month.JANUARY, 17);

		// when
		Pair<LocalDate, LocalDate> result = DateUtils
				.retrieveEarliestAndLatestDate(Arrays.asList(date1, date2, date3, date4, date5, date6));

		// then
		assertEquals(date1, result.getLeft());
		assertEquals(date6, result.getRight());
	}

	@Test
	public void shouldReturnTheSameMaxAndMinDateTupleWhenGivenListTheSameDates() {
		// given
		LocalDate date1 = LocalDate.of(2017, Month.AUGUST, 18);
		LocalDate date2 = LocalDate.of(2017, Month.AUGUST, 18);
		LocalDate date3 = LocalDate.of(2017, Month.AUGUST, 18);

		// when
		Pair<LocalDate, LocalDate> result = DateUtils.retrieveEarliestAndLatestDate(Arrays.asList(date1, date2, date3));

		// then
		assertEquals(date1, result.getLeft());
		assertEquals(date1, result.getRight());
	}

	@Test
	public void shouldReturnTheSameMaxAndMinDateWhenListContainsOnlyOneDate() {
		// given
		LocalDate date1 = LocalDate.of(2017, Month.AUGUST, 18);

		// when
		Pair<LocalDate, LocalDate> result = DateUtils.retrieveEarliestAndLatestDate(Arrays.asList(date1));

		// then
		assertEquals(date1, result.getLeft());
		assertEquals(date1, result.getRight());
	}
}
