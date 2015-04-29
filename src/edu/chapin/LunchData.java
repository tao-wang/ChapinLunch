package edu.chapin;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class LunchData {
	public static final int LSDATA = 0;
	public static final int USDATA = 1;
	
	private int type;
	private ArrayList<CSVRecord> records;
	
	public LunchData(String filePath, int type) throws IOException {
		this.type = type;
		Reader reader = new FileReader(filePath);
		setRecords(CSVFormat.EXCEL.parse(reader));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<CSVRecord> getRecords() {
		return records;
	}

	public void setRecords(Iterable<CSVRecord> records) {
		this.records = new ArrayList<CSVRecord>();
		boolean first = true;
		
		for (CSVRecord row : records) {
			if (first) {
				first = false;
				continue;
			}
			
			this.records.add(row);
		}
	}

}
