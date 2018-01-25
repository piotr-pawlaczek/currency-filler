package com.mtrojahn.boot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/*
 * Manual:
 * public @interface SpringBootApplication
 * Indicates a configuration class that declares one or more @Bean methods and also triggers auto-configuration and
 * component scanning. This is a convenience annotation that is equivalent to declaring @Configuration,
 * @EnableAutoConfiguration and @ComponentScan.
 *
 * Registra uma classe de configuração que declara um ou mais metodos @Bean e também dispara a auto-configuração e
 * busca por componentes neste pacote. Esta é uma anotação de conveniência que é equivalente a declarar @Configuration
 * @EnableAutoConfiguration e @ComponentScan.
 *
 */
@SpringBootApplication
public class Application {

	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {

		ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(Application.class)
				.bannerMode(Banner.Mode.OFF).run(args);

		Application app = context.getBean(Application.class);
		app.start();

	}

	private void start() {
		System.out.println("Hello World!");
		System.out.println("dddd");

		Map<String, Integer> template = parseTemplate();

		// wczytaj plik csv z pliku
		List<String[]> csvEntries = CsvFileExtractor.extractFromFile("C:\\Users\\pawlacze\\Desktop\\ex1.csv");

		// wyciagnij z niego daty
		List<LocalDate> datesFromCsv = CsvFileExtractor.extractDatesFromCsvEntries(csvEntries, 3);

		// wyciagnij najmniejsza i najwieksa date
		Pair<LocalDate, LocalDate> datesRange = DateUtils.retrieveEarliestDate(datesFromCsv);

		// pobierz kursy walut z przedzialu
		CurrencyRateDto currencyRates = fetchCurrencyRate("a", "USD", datesRange.getLeft(), datesRange.getRight());

		// stworz nowy plik csv z dodanymi kolumnami: kurs + nazwa tabeli npb
		List<String> lines = new ArrayList<>();
		Map<LocalDate, RateDto> currencyRateMap = createCurrencyRateMap(currencyRates.getRates());
		for (String[] lineEntries : csvEntries) {
			LocalDate date = extractDateFromLineEntries(lineEntries, template.get("date"));
			RateDto rate = getWorkingDayRate(date, currencyRateMap);
			lines.add(generateCsvLine(lineEntries, rate.getMid(), rate.getNo()));
		}

		createOutputCsvFile("C:\\\\Users\\\\pawlacze\\\\Desktop\\\\ex1_obiaaadek.csv", lines);

		// test();
	}

	private Map<String, Integer> parseTemplate() {
		String templateLine;
		Map<String, Integer> columnsMap = new HashMap<>();

		try (BufferedReader brTest = new BufferedReader(new FileReader("C:\\Users\\pawlacze\\Desktop\\template.txt"))) {
			templateLine = brTest.readLine();
			String[] columnNames = templateLine.split(":");
			for (int i = 0; i < columnNames.length; i++) {
				columnsMap.put(columnNames[i], i);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return columnsMap;
	}

	private RateDto getWorkingDayRate(LocalDate date, Map<LocalDate, RateDto> currencyRateMap) {
		RateDto rate = currencyRateMap.get(date);
		while (rate == null) {
			rate = getWorkingDayRate(date.minusDays(1), currencyRateMap);
		}
		return rate;
	}

	private LocalDate extractDateFromLineEntries(String[] lineEntries, int dateColumnNo) {
		return LocalDate.parse(lineEntries[dateColumnNo]);
	}

	private void createOutputCsvFile(String pathToFile, List<String> lines) {
		try (PrintStream fw = new PrintStream(pathToFile)) {
			for (String line : lines) {
				fw.println(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private String generateCsvLine(String[] lineEntries, BigDecimal currencyRate, String tableName) {
		String line = "";
		for (String lineEntry : lineEntries) {
			line += lineEntry + ",";
		}
		line += currencyRate.toPlainString() + ",";
		line += tableName;
		return line;
	}

	private CurrencyRateDto fetchCurrencyRate(String table, String currencyCode, LocalDate startDate,
			LocalDate endDate) {
		String urlPattern = "http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}";

		String startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
		String endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

		return restTemplate.getForObject(urlPattern, CurrencyRateDto.class, table, currencyCode, startDateString,
				endDateString);
	}

	private Map<LocalDate, RateDto> createCurrencyRateMap(RateDto[] currencyRates) {
		Map<LocalDate, RateDto> rateMap = new HashMap<>();

		for (RateDto curRate : currencyRates) {
			rateMap.put(LocalDate.parse(curRate.getEffectiveDate()), curRate);
		}
		return rateMap;
	}

	private void test() {
		CurrencyRateDto response = fetchCurrencyRate("a", "USD", LocalDate.now().minusMonths(5), LocalDate.now());

		RateDto[] currencyRates = response.getRates();

		Map<LocalDate, RateDto> rateMap = new HashMap<>();

		for (RateDto curRate : currencyRates) {
			rateMap.put(LocalDate.parse(curRate.getEffectiveDate()), curRate);
			System.out.println(curRate.getEffectiveDate() + " " + curRate.getMid() + " " + curRate.getNo());
		}

		for (Entry<LocalDate, RateDto> mapEntry : rateMap.entrySet()) {
			System.out.println(
					mapEntry.getKey() + " " + mapEntry.getValue().getMid() + " " + mapEntry.getValue().getNo());
		}

		System.out.println("Kurs: :" + rateMap.get(LocalDate.of(2018, 1, 4)).getMid());
	}

	@Bean
	public RestTemplate restTemplate() {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		final InetSocketAddress address = new InetSocketAddress("10.144.1.10", 8080);
		final Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
		factory.setProxy(proxy);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(factory);
		return restTemplate;
	}

}
