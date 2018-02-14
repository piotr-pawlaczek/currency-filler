package pp.currencyextr;

import org.springframework.stereotype.Component;

@Component
public class TemplateFactory {
	
	public String getTemplate(String fileName) {
		if(fileName.toLowerCase().contains("etsy_sales")) {
			return getClass().getClassLoader().getResource("Etsy_template.xlsx").getFile();
		}
		else if(fileName.toLowerCase().contains("creative market sales")) {
			return getClass().getClassLoader().getResource("CreativeMarketSales_template.xlsx").getFile();
		}
		return null;
	}

}
