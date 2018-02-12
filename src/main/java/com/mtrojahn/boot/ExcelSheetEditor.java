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

public class ExcelSheetEditor {

	public void fillExcelTemplateWithDatesAndRates(XSSFWorkbook workbook, Integer sheetNo, Collection<RateDto> rates) {
		XSSFSheet sheet = workbook.getSheetAt(sheetNo);
		Cell cell = null;

		int rowIndex = 2;
		for (RateDto rate : rates.stream().sorted().collect(Collectors.toList())) {
			cell = sheet.getRow(rowIndex).getCell(1);
			cell.setCellValue(rate.getEffectiveDate());
			cell = sheet.getRow(rowIndex).getCell(2);
			cell.setCellValue(rate.getMid().toPlainString());
			cell = sheet.getRow(rowIndex).getCell(3);
			cell.setCellValue(rate.getNo());
			rowIndex++;
		}
	}
	
	public void fill(XSSFWorkbook workbook, List<RowEntry> entries, Map<LocalDate, RateDto> currencyRateMap) {
		for(Integer i : entries.get(0).getValues().keySet()) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			Cell cell = null;
			
			fillExcelTemplateWithDatesAndRates(workbook, i, currencyRateMap.values());

			int rowIndex = 2;
			rowIndex = fillRows(workbook, entries, currencyRateMap, i, sheet, rowIndex);
			
			rowIndex++;

			cell = sheet.getRow(rowIndex).getCell(8);
			cell.setCellFormula("SUM(I3:I" + (rowIndex - 1) + ")");

			XSSFFont font = workbook.createFont();
			font.setBold(true);

			XSSFCellStyle style = createCellStyle(workbook);
			style.setFont(font);

			cell.setCellStyle(style);

			XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
		}
		

	}

	private int fillRows(XSSFWorkbook workbook, List<RowEntry> entries, Map<LocalDate, RateDto> currencyRateMap,
			Integer i, XSSFSheet sheet, int rowIndex) {
		Cell cell;
		for (RowEntry entry : entries) {
			cell = sheet.getRow(rowIndex).getCell(5);
			cell.setCellValue(entry.getDate().format(DateTimeFormatter.ISO_DATE));
			
			cell = sheet.getRow(rowIndex).getCell(6);
			cell.setCellValue(entry.getValues().get(i).doubleValue());
			
			cell = sheet.getRow(rowIndex).getCell(7);
			cell.setCellValue(getLastWorkingDayRate(entry.getDate(), currencyRateMap).getMid().doubleValue());
			
			cell = sheet.getRow(rowIndex).getCell(8);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellFormula("H"+(rowIndex+1) + "*G"+(rowIndex+1));
			cell.setCellStyle(createCellStyle(workbook));
			rowIndex++;
		}
		return rowIndex;
	}

	private XSSFCellStyle createCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);

		style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
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
