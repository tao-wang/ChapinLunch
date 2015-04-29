package edu.chapin;

import java.util.HashMap;

public class OrderSummary {
	private String itemName;
	private HashMap<String, Integer> count;
	
	public OrderSummary(String itemName) {
		this.itemName = itemName;
		count = new HashMap<String, Integer>();
	}
	
	public void incrementCountFor(String location) {
		if (count.containsKey(location)) {
			int currentValue = count.get(location);
			count.put(location, currentValue+1);
		}
		else {
			count.put(location, 1);
		}
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public int getCountFor(String location) {
		if (count.containsKey(location))
			return count.get(location);
		
		return 0;
	}
	
	public int getTotal() {
		int sum = 0;
		
		for (int tally : count.values()) {
			sum += tally;
		}
		
		return sum;
	}

}
