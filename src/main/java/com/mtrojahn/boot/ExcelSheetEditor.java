package com.mtrojahn.boot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelSheetEditor {

	private static final int FIRST_DATA_ROW = 2;
	
	private static final int WORKING_DAY_DATE_COLUMN = 1;
	private static final int DATE_COLUMN = 2;
	private static final int NBP_TABLE_NAME_COLUMN = 3;

	private static final int TRANSACTION_DATE_COLUMN = 5;
	private static final int TRANSACTION_VALUE_COLUMN = 6;
	private static final int CURRENCY_RATE_FROM_PREVIOUS_DAY_COLUMN = 7;
	private static final int PLN_VALUE_COLUMN = 8;

	public void fill(XSSFWorkbook workbook, List<RowEntry> entries, Map<LocalDate, RateDto> currencyRateMap) {
		for (Integer i : entries.get(0).getValues().keySet()) {
			XSSFSheet sheet = workbook.getSheetAt(i);

			fillExcelTemplateWithDatesAndRates(workbook, i, currencyRateMap.values());

			int rowIndex = fillRows(workbook, entries, currencyRateMap, i, sheet, FIRST_DATA_ROW);

			Cell cell = sheet.getRow(++rowIndex).getCell(PLN_VALUE_COLUMN);
			createSumCell(workbook, cell, rowIndex);

			XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
		}
	}

	private void fillExcelTemplateWithDatesAndRates(XSSFWorkbook workbook, Integer sheetNo, Collection<RateDto> rates) {
		XSSFSheet sheet = workbook.getSheetAt(sheetNo);
		Cell cell = null;

		int rowIndex = 2;
		for (RateDto rate : rates.stream().sorted().collect(Collectors.toList())) {
			cell = sheet.getRow(rowIndex).getCell(WORKING_DAY_DATE_COLUMN);
			cell.setCellValue(rate.getEffectiveDate());
			cell = sheet.getRow(rowIndex).getCell(DATE_COLUMN);
			cell.setCellValue(rate.getMid().toPlainString());
			cell = sheet.getRow(rowIndex).getCell(NBP_TABLE_NAME_COLUMN);
			cell.setCellValue(rate.getNo());
			rowIndex++;
		}
	}

	private int fillRows(XSSFWorkbook workbook, List<RowEntry> entries, Map<LocalDate, RateDto> currencyRateMap,
			Integer i, XSSFSheet sheet, int rowIndex) {
		for (RowEntry entry : entries) {
			fillRow(workbook, currencyRateMap, i, sheet, rowIndex, entry);
			rowIndex++;
		}
		return rowIndex;
	}

	private void fillRow(XSSFWorkbook workbook, Map<LocalDate, RateDto> currencyRateMap, Integer i, XSSFSheet sheet,
			int rowIndex, RowEntry entry) {
		Cell cell;
		cell = sheet.getRow(rowIndex).getCell(TRANSACTION_DATE_COLUMN);
		cell.setCellValue(entry.getDate().format(DateTimeFormatter.ISO_DATE));

		cell = sheet.getRow(rowIndex).getCell(TRANSACTION_VALUE_COLUMN);
		cell.setCellValue(entry.getValues().get(i).doubleValue());

		cell = sheet.getRow(rowIndex).getCell(CURRENCY_RATE_FROM_PREVIOUS_DAY_COLUMN);
		cell.setCellValue(getLastWorkingDayRate(entry.getDate(), currencyRateMap).getMid().doubleValue());

		cell = sheet.getRow(rowIndex).getCell(PLN_VALUE_COLUMN);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellFormula("H" + (rowIndex + 1) + "*G" + (rowIndex + 1));
		cell.setCellStyle(createCurrencyCellStyle(workbook));
	}

	private void createSumCell(XSSFWorkbook workbook, Cell cell, int rowIndex) {
		cell.setCellFormula("SUM(I3:I" + (rowIndex - 1) + ")");

		XSSFFont font = workbook.createFont();
		font.setBold(true);

		XSSFCellStyle style = createCurrencyCellStyle(workbook);
		style.setFont(font);

		cell.setCellStyle(style);
	}

	private XSSFCellStyle createCurrencyCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);

		// style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("$0.00"));
		style.setDataFormat(8); // https://poi.apache.org/apidocs/org/apache/poi/ss/usermodel/BuiltinFormats.html
		return style;
	}

	private RateDto getLastWorkingDayRate(LocalDate date, Map<LocalDate, RateDto> currencyRateMap) {
		RateDto dto = null;
		int daysBefore = 1;
		while (dto == null) {
			dto = currencyRateMap.get(date.minusDays(daysBefore++));
		}
		return dto;
	}
}
