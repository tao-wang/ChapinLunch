package edu.chapin;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.csv.CSVRecord;

public class DailySummary {
	HashMap<String, ArrayList<Person>> summaryByLocation;
	HashMap<String, OrderSummary> summaryByItem;
	ArrayList<String> locationSet;
	
	public ArrayList<String> getOrdersFromRecord(CSVRecord record, int day, int dataType) {
		ArrayList<String> orders = new ArrayList<String>();
		
		if (dataType == LunchData.LSDATA) {
			orders.add(record.get(2*day+5));
			orders.add(record.get(2*day+6));
		}
		else {
			orders.add(record.get(4*day+6));
			orders.add(record.get(4*day+7));
			orders.add(record.get(4*day+8));
		}
		
		return orders;
	}
	
	public DailySummary(LunchData[] dataSet, int day) {
		summaryByLocation = new HashMap<String, ArrayList<Person>>();
		summaryByItem = new HashMap<String, OrderSummary>();
		locationSet = new ArrayList<String>();
		
		for (LunchData data : dataSet) {
			for (CSVRecord row : data.getRecords()) {
				String firstName = row.get(Summarizer.FIRST_NAME);
				String lastName = row.get(Summarizer.LAST_NAME);
				String location = row.get(Summarizer.CLASS);
				
				if (!locationSet.contains(location))
					locationSet.add(location);
				
				ArrayList<String> orders = getOrdersFromRecord(row, day, data.getType());
				
				if (!summaryByLocation.containsKey(location)) {
					summaryByLocation.put(location, new ArrayList<Person>());
				}
				
				ArrayList<Person> people = summaryByLocation.get(location);
				// Find and ignore duplicates
				boolean found = false;
				for (Person p : people) {
					if (p.firstName.equals(firstName) && p.lastName.equals(lastName)) {
						found = true;
						break;
					}
				}
				if (found)
					continue;
				
				summaryByLocation.get(location).add(new Person(firstName, lastName, orders));
				
				for (String item : orders) {
					if (item.equals(""))
						continue;
					
					if (!summaryByItem.containsKey(item)) {
						summaryByItem.put(item, new OrderSummary(item));
					}
					summaryByItem.get(item).incrementCountFor(location);
				}
			}
		}
		
	}
}
