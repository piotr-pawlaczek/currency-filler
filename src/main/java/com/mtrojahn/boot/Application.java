package com.mtrojahn.boot;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
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
	public static void main(String[] args) {

		ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(Application.class)
				.bannerMode(Banner.Mode.OFF).run(args);

		Application app = context.getBean(Application.class);
		app.start();

	}

	private void start() {
		System.out.println("Hello World!");
		System.out.println("dddd");

		RestTemplate restTemplate = restTemplate();
		
		String startDate = LocalDate.now().minusMonths(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
		String endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		
		LocalDate.now().compareTo(LocalDate.now());
		
		CurrencyRate response = restTemplate.getForObject(
				"http://api.nbp.pl/api/exchangerates/rates/{table}/{code}/{startDate}/{endDate}", CurrencyRate.class,
				"a", "USD", startDate, endDate);
		
		Rate[] currencyRates = response.getRates();
		
		 Map<LocalDate, Rate> rateMap = new HashMap<>();
		
		for (Rate curRate : currencyRates) {
			rateMap.put(LocalDate.parse(curRate.getEffectiveDate()), curRate);
			System.out.println(curRate.getEffectiveDate() + " " + curRate.getMid() + " " + curRate.getNo());
		}
		
		for(Entry<LocalDate, Rate> mapEntry : rateMap.entrySet()) {
			System.out.println(mapEntry.getKey() + " " + mapEntry.getValue().getMid() + " " + mapEntry.getValue().getNo());
		}
		
		System.out.println("Kurs: :" + rateMap.get(LocalDate.of(2018, 1, 4)).getMid());
	}
	
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
