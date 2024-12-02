package ADT;
//Ron Lustig CS 4100 Spring 2024
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

//Ron Lustig 
//CS 4100 Part 1, Foundations: Assignment 1, Reserve Table

public class ReserveTable {
	//declaring variables that will be used
	ArrayofObj[] array;
	int maxSize;
	int currentsize;

//Constructor 
//initializes the internal storage to contain up to maxSize rows of data
	public ReserveTable(int maxSize) {
		this.maxSize = maxSize;
		this.currentsize = 0;
		this.array = new ArrayofObj[maxSize];
	}

//Just Adds the name and code to the array and returns index
	public int Add(String name, int code) {
		if (currentsize < maxSize) {
			array[currentsize] = new ArrayofObj(name, code);
			currentsize++;
		}
		return currentsize - 1;
	}

//Returns the integer code associated with name if name is in the table
//if not return -1
	public int LookupName(String name) {
		for (int i = 0; i < currentsize; i++) {
			if (array[i].getName().compareToIgnoreCase(name) == 0) {
				return array[i].getCode();
			}
		}
		return -1;
	}

//Returns the string that is associated with the code
	public String LookupCode(int code) {
		for (int i = 0; i < currentsize; i++) {
			if (Integer.compare(array[i].getCode(), code) == 0) {
				return array[i].getName();
			}
		}
		return " ";
	}

//Prints index,name,code and it prints it to a text file
	public void PrintReserveTable(String filename) {
		// Prints to the named file with the required error catching
		try {
			FileOutputStream outputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			PrintWriter bufferedWriter = new PrintWriter(outputStreamWriter);
			bufferedWriter.write("Index\tname\tcode\n");
			for (int i=0; i<=5; i++) {
				if (array[i] != null) {
				bufferedWriter.printf("%d\t%s\t%d\n",i,array[i].getName(),array[i].getCode());
				 }
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//Inner class that represents the array of objects
//It has a string name and a integer code
	class ArrayofObj {
		String name;
		int code;

		ArrayofObj(String name, int code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public int getCode() {
			return code;
		}
	}
}
