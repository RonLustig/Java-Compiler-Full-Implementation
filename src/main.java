/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
//package fa22hw1;
import ADT.*;
/**
* 
* @author abrouill
*/
/********************** FREE BONUS FUNCTION
String pad(String input, int len, boolean left) {
while (input.length() < len){
if (left)
input = " " +input ;
else
input = input + " ";
}
return input;
}
*/
public class main {
/**
* @param args the command line arguments
*/
public static void main(String[] args) {
// Create the table
System.out.println("Ron Lustig CS4100 Homework 1, Sprint2024");
ReserveTable reserve = new ReserveTable(25);
// Add to the table
reserve.Add("cat", 15);
reserve.Add("APPLE", 11);
reserve.Add("Dog", 5);
reserve.Add("DOnE", 21);
reserve.Add("Over", 8);
// Search the table
System.out.println("The Code for 'over' is "+reserve.LookupName("over"));
System.out.println("The Code for 'DOG' is "+reserve.LookupName("DOG"));
System.out.println("The Code for 'Cat' is "+reserve.LookupName("Cat"));
System.out.println("The Code for 'gone' is "+reserve.LookupName("gone"));
System.out.println();
System.out.println("The Name for 11 is "+reserve.LookupCode(11));
System.out.println("The Name for 5 is "+reserve.LookupCode(5));
System.out.println("The Name for 8 is "+reserve.LookupCode(8));
System.out.println("The Name for 28 is "+reserve.LookupCode(28));
// Print table to file
System.out.println("Saving Printed Table to "+args[0]);
reserve.PrintReserveTable(args[0]);
  }
}
