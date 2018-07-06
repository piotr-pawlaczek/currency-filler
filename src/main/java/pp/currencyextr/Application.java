package pp.currencyextr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CsvExtractorFactory csvExtractorFactory;

	@Autowired
	private TemplateFactory templateFactory;

	@Autowired
	private ExcelSheetEditor excelSheetEditor;

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(Application.class)
				.bannerMode(Banner.Mode.OFF).run(args);

		Application app = context.getBean(Application.class);
		app.start();
	}

	private void start() {
		System.out.println("Hello World!");
		System.out.println("dddd");

		List<File> files = collectCsvFiles(new File("C:/Users/pawlacze/Downloads/basia3/"));

		generateOutput(files);
	}

	private List<File> collectCsvFiles(File directory) {
		List<File> files = new ArrayList<>();
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				if (file.getName().endsWith(".csv")) {// only csv files
					files.add(file);
				}
			} else {
				files.addAll(collectCsvFiles(file));
			}
		}
		return files;
	}

	private void generateOutput(List<File> files) {
		for (File file : files) {
			List<RowEntry> entries = csvExtractorFactory.getCsvExtractor(file.getName())
					.extractDataFromCsv(file.getAbsolutePath());
			
			if(entries.isEmpty()) {// pusty plik
				continue;
			}

			List<LocalDate> dates = entries.stream().map(e -> e.getDate()).collect(Collectors.toList());
			Pair<LocalDate, LocalDate> datesRange = DateUtils.retrieveEarliestAndLatestDate(dates);

			CurrencyRateDto currencyRates = fetchCurrencyRate("a", "USD", datesRange.getLeft().minusDays(5),
					datesRange.getRight());

			Map<LocalDate, RateDto> currencyRateMap = createCurrencyRateMap(currencyRates.getRates());

			try (FileInputStream templateFile = new FileInputStream(
					new File(templateFactory.getTemplate(file.getName())))) {
				XSSFWorkbook workbook = new XSSFWorkbook(templateFile);

				excelSheetEditor.fill(workbook, entries, currencyRateMap);

				createOutputDirectoryIfNotExist(file);
				
				FileOutputStream outFile = new FileOutputStream(
						new File(file.getParent() + "/output/" + file.getName().replace("csv", "xlsx")));
				workbook.write(outFile);
				outFile.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void createOutputDirectoryIfNotExist(File file) {
		if(!(new File(file.getParent() + "/output/").exists())) {
			new File(file.getParent() + "/output/").mkdir();
		}
	}

	private CurrencyRateDto fetchCurrencyRate(String table, String currencyCode, LocalDate startDate,
			LocalDate endDate) {
		String startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
		String endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

		return restTemplate.getForObject(NBP_API_URL, CurrencyRateDto.class, table, currencyCode, startDateString,
				endDateString);
	}

	private Map<LocalDate, RateDto> createCurrencyRateMap(RateDto[] currencyRates) {
		Map<LocalDate, RateDto> rateMap = new HashMap<>();

		for (RateDto curRate : currencyRates) {
			rateMap.put(LocalDate.parse(curRate.getEffectiveDate()), curRate);
		}
		return rateMap;
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
