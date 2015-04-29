package edu.chapin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Summarizer {
	private static final String SHEET_NAME_TOTALS = "Totals";
	private static final String EXTENSION = ".xls";
	
	public static int FIRST_NAME = 1;
	public static int LAST_NAME  = 2;
	public static int CLASS      = 4;
	
	public static int MONDAY    = 0;
	public static int TUESDAY   = 1;
	public static int WEDNESDAY = 2;
	public static int THURSDAY  = 3;
	public static int FRIDAY    = 4;
	
	public static String[] DAY_STRING = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
	
	LunchData dataLS;
	LunchData dataUS;
	LunchData[] dataSet;
	
	public Summarizer(LunchData dataLS, LunchData dataUS) {
		this.dataLS = dataLS;
		this.dataUS = dataUS;
		this.dataSet = new LunchData[2];
		this.dataSet[0] = dataLS;
		this.dataSet[1] = dataUS;
	}
	
	public void summarize() throws IOException {
		for (int day = MONDAY; day <= FRIDAY; day++) {
			writeWorkbooksFor(day);
		}
	}
	
	private ArrayList<String> setToSortedArrayList(Set<String> set) {
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (String s : set)
			arrayList.add(s);
		
		Collections.sort(arrayList);
		
		return arrayList;
	}
	
	public void writeWorkbooksFor(int day) throws IOException {
		Workbook summaryWorkbook = new HSSFWorkbook();
		
		DailySummary dailySummary = new DailySummary(dataSet, day);
		
		HashMap<String, OrderSummary> itemSummary = dailySummary.summaryByItem;
		ArrayList<String> locationSet = dailySummary.locationSet;
		
		// SHEET: TOTALS
		Sheet totals = summaryWorkbook.createSheet(SHEET_NAME_TOTALS);
		totals.setPrintGridlines(true);
		totals.getHeader().setCenter(DAY_STRING[day]);
		totals.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
		
		PrintSetup printSetup = totals.getPrintSetup();
		printSetup.setLandscape(true);
		printSetup.setPaperSize(PrintSetup.ELEVEN_BY_SEVENTEEN_PAPERSIZE);
		
		// ROW: HEADERS
		Row headers = totals.createRow(0);
		headers.createCell(0).setCellValue("Item");
		headers.createCell(1).setCellValue("Total");
		
		// COLUMNS: HEADERS, LOCATION CATEGORIES
		Collections.sort(locationSet);
		CellStyle verticalText = summaryWorkbook.createCellStyle();
		verticalText.setRotation((short) 90);
		for (int i = 0, col = 2; i < locationSet.size(); i++, col++) {
			String location = locationSet.get(i);
			Cell cell = headers.createCell(col);
			cell.setCellValue(location);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(verticalText);
		}
		
		// ROWS: ITEM AND ITEM COUNTS
		ArrayList<String> itemSet = setToSortedArrayList(itemSummary.keySet());
		for (int i = 0, row = 1; i < itemSet.size(); i++, row++) {
			String item = itemSet.get(i);
			OrderSummary summary = itemSummary.get(item);
			
			Row itemRow = totals.createRow(row);
			Cell itemCell = itemRow.createCell(0);
			itemCell.setCellValue(item);
			itemCell.getCellStyle().setWrapText(true);
			itemRow.createCell(1).setCellValue(summary.getTotal());
			
			for (int j = 0, col = 2; j < locationSet.size(); j++, col++) {
				String location = locationSet.get(j);
				int count = summary.getCountFor(location);
				
				if (count > 0)
					itemRow.createCell(col).setCellValue(count);
			}

		}
		
		for (int col = 0; col < totals.getRow(0).getPhysicalNumberOfCells(); col++) {
			if (col == 0)
				totals.setColumnWidth(col, 32*256);
			else
				totals.autoSizeColumn(col);
		}
		
		FileOutputStream summaryFileOut = new FileOutputStream(day + "-" + DAY_STRING[day] + "-Summary" + EXTENSION);
		summaryWorkbook.write(summaryFileOut);
		summaryWorkbook.close();
		summaryFileOut.close();
		
		Workbook locationWorkbook = new HSSFWorkbook();
		
		// SHEETS: LOCATION CATEGORIES
		for (String location : locationSet) {
			Sheet locationSheet = locationWorkbook.createSheet(location);
			locationSheet.setPrintGridlines(true);
			locationSheet.getHeader().setCenter(DAY_STRING[day] + " " + location);
			//locationSheet.getPrintSetup().setLandscape(true);
			
			ArrayList<Person> personSet = dailySummary.summaryByLocation.get(location);
			Collections.sort(personSet);
			for (int i = 0, row = 0; i < personSet.size(); i++, row++) {
				Person person = personSet.get(i);
				
				Row personRow = locationSheet.createRow(row);
				personRow.createCell(0).setCellValue(person.lastName);
				personRow.createCell(1).setCellValue(person.firstName);
				
				for (int j = 0, col = 2; j < person.order.size(); j++, col++) {
					String item = person.order.get(j);
					Cell personCell = personRow.createCell(col);
					personCell.setCellValue(item);
					personCell.getCellStyle().setWrapText(true);
				}
			}
			
			for (int col = 0; col < locationSheet.getRow(0).getPhysicalNumberOfCells(); col++) {
				if (col < 2)
					locationSheet.setColumnWidth(col, 12*256);
				else
					locationSheet.setColumnWidth(col, 18*256);
			}
		}
		
		FileOutputStream locationFileOut = new FileOutputStream(day + "-" + DAY_STRING[day] + "-ByLocation" + EXTENSION);
		locationWorkbook.write(locationFileOut);
		locationWorkbook.close();
		locationFileOut.close();
		
		
	}
	
}
