package ADT;
//Ron Lustig
//CS 4100
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
//Ron Lustig CS 4100 Spring 2024
public class QuadTable {
	//Decrlaring variables
	//Table maxSizex4
    int[][] table;
    //mximam size
    int maxSize;
    //Rows of the array
    private int nextAvailable;
    //Constructor to initilazize the variables
    public QuadTable(int maxSize) {
        this.maxSize = maxSize;
        this.nextAvailable = 0;
        this.table = new int[maxSize][4];
    }
//Returns index of the next open slot
    public int NextQuad() {
        return nextAvailable;
    }
//Adding the opcode, operand1,2,3 to a row and then increasing the rows to go to the next one    
    public void AddQuad(int opcode,int op1,int op2,int op3) {
    	//Checking if nextAvaliable is less then maxSize just in case 
    	if (nextAvailable < maxSize) {
    		table[nextAvailable][0]=opcode;
    		table[nextAvailable][1]=op1;
    		table[nextAvailable][2]=op2;
    		table[nextAvailable][3]=op3;
    		nextAvailable++;
    	}
    }
   //Returns the int data 4 for the row index
   public int[] GetQuad(int index) {
    	return table[index];
    }
    //Changes the contents of the quad 3 operand which is at index.
    public void UpdateJump(int index,int op3) {
    	//Update only if index is less than nextAvaliable becuase if not it should do that
    	//It is also checking for index just in case, to avoid crushing if given negative index
    	 if ( (index < nextAvailable) && (index >= 0) ) {
             table[index][3] = op3;
         } 
    }
    //Pritnting to a file all of the content of the quad table
    //It is using the given main to test them and print it to file
    public void PrintQuadTable(String filename) {
        try  {
        	FileOutputStream outputStream = new FileOutputStream(filename);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			PrintWriter bufferedWriter = new PrintWriter(outputStreamWriter);
			bufferedWriter.write("Index\tOpcode\t Op1\t Op2\tOp3");
			bufferedWriter.write("\n");
            for (int i = 0; i < nextAvailable; i++) {
                int[] quadRow = GetQuad(i);
                bufferedWriter.printf("%d\t| %d\t| %d\t| %d\t| %d\t|%n", i, quadRow[0], quadRow[1], quadRow[2], quadRow[3]);
            }
            bufferedWriter.close();
            System.out.println("QuadTable printed to file: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
} //end of QuadTable 