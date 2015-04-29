package edu.chapin;

import java.util.ArrayList;

public class Person implements Comparable<Person> {
	String firstName;
	String lastName;
	ArrayList<String> order;
	
	public Person(String firstName, String lastName, ArrayList<String> order) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.order = order;
	}

	public int compareTo(Person person) {
		String thisName = this.lastName.toLowerCase();
		String thatName = person.lastName.toLowerCase();
		
		if (thisName.equals(thatName))
			return this.firstName.toLowerCase().compareTo(person.firstName.toLowerCase());
		
		return thisName.compareTo(thatName);
	}
	
}
