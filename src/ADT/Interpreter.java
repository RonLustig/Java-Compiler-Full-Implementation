package ADT;
import java.util.Scanner;
public class Interpreter {
	//Ron Lustig CS 4100 Spring 2024
	//Creating objects of ReserveTable, SymbolTable and QuadTable
	public ReserveTable optable;
	public SymbolTable S;
	public QuadTable Q;
	
//Constructor creating reserve table object that will have 16 opcodes
public Interpreter() {
		optable = new ReserveTable(16);
        initReserve(optable);        
	}
	
	//HERE is a free opcode table initialization for a created ReserveTable
	private void initReserve(ReserveTable optable){
	//Adding each OPCODE to Symbol table
	optable.Add("STOP",0);
	optable.Add("DIV",1);
	optable.Add("MUL",2);
	optable.Add("SUB",3);
	optable.Add("ADD",4);
	optable.Add("MOV",5);
	optable.Add("PRINT",6);
	optable.Add("READ",7);
	optable.Add("JMP",8);
	optable.Add("JZ",9);
	optable.Add("JP",10);
	optable.Add("JN",11);
	optable.Add("JNZ",12);
	optable.Add("JNP",13);
	optable.Add("JNN",14);
	optable.Add("JINDR",15);
	}

	//HERE IS THE FACTORIAL INITIALIZATION STUFF...
	public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable) {
	InitSTF(stable);
	InitQTF(qtable);
	     return true;
	}

	//Initialize symbols and quads for Summation function
	public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable) {
	InitSTS(stable);
	InitQTS(qtable);
	    return true;
	}

	//Translate the interpreter guidelines from the pseudo-code located there into equivalent Java code
	public void InterpretQuads(QuadTable Q, SymbolTable S, boolean TraceOn, String filename) {
		//declaring variables that will have all of the OPCODES
		final int _STOP = 0;
		final int _DIV = 1;
		final int _MUL = 2;
		final int _SUB = 3;
		final int _ADD = 4;
		final int _MOV = 5;
		final int _PRINT = 6;
		final int _READ = 7;
		final int _JMP = 8;
		final int _JZ = 9;
		final int _JP = 10;
		final int _JN = 11;
	    final int _JNZ = 12;
	    final int _JNP = 13;
	    final int _JNN = 14;
	    final int _JINDR = 15;
	    //Get the next avaliable quad from quad class
	    final int MAXQUAD = Q.NextQuad();
	    //Creating a program counter stats at 0
	    int PC = 0;
	    //Creating OPCODE, Operand 1, Operand 2, Operand 3
	    int OPCODE, OP1, OP2, OP3;
	    //Ensuring that i didn't go beyond the index size avaliable 
	    while (PC < MAXQUAD) { 
	    	//Adding a row of data
	    	//Getting the whole quad table array. int of 4 elements: OPCODE, OP1,OP2,OP3
	        int[] quadData=Q.GetQuad(PC);
	        OPCODE=quadData[0];
	        OP1=quadData[1];
	        OP2=quadData[2];
	        OP3=quadData[3];
             //making sure that the opcode is a valid opcode	
	         if (OPCODE >= 0 && OPCODE <= 15) {
	        	 //If reciving TraceOn=True then call makeTraceString and print it in that format
	                if (TraceOn) {
	                    System.out.println(makeTraceString(PC, OPCODE, OP1, OP2, OP3));
	                }
	                //Switch OPCODE that will go to the case of the matching OPCODE, 
	                switch (OPCODE) {
	                //if quad is calling OPCODE 0 then stop the program by making PC=NextQuad 
	                case _STOP:
	                    System.out.println("Execution terminated by program STOP.");
	                    PC = MAXQUAD;
	                    break;
	                //if quad is calling OPCODE 1 it will devide OP1 with OP2 and put the result in OP3 then increment PC
	                case _DIV:	                    
	                	S.UpdateSymbol(OP3, S.GetUsage(OP3), (S.GetInteger(OP1) / S.GetInteger(OP2)));
	                    PC++;
	                    break;
	                //if quad is calling OPCODE 2 it will multiply OP1 with OP2 and put the result in OP3, then increment PC
	                case _MUL:	                    
	                	S.UpdateSymbol(OP3, S.GetUsage(OP3), (S.GetInteger(OP1) * S.GetInteger(OP2)));
	                    PC++;
	                    break;
	                //if quad is calling OPCODE 3 it will Subtract OP1 with OP2 and put the result in OP3, then increment PC
	                case _SUB:	                    
	                	S.UpdateSymbol(OP3, S.GetUsage(OP3), (S.GetInteger(OP1) - S.GetInteger(OP2)));
	                    PC++;
	                    break;
	                //if quad is calling OPCODE 4 it will Add OP1 with OP2 and put the result in OP3, then increment PC
	                case _ADD:	                    
	                	S.UpdateSymbol(OP3, S.GetUsage(OP3), (S.GetInteger(OP1) + S.GetInteger(OP2)));
	                    PC++;
	                    break;
	                //if quad is calling OPCODE 5 it will MOV OP1 to OP3
	                //Before it does the move I check the type of OP1 to make sure I update it with the 
	                //Appropriate method. Once it found the method that matches the type it updates it by moving OP1 to OP3
	                case _MOV:
	                	char type = S.GetDataType(OP1);
	                	if (type == 'I') {
	                       int valueint = S.GetInteger(OP1);
	                       S.UpdateSymbol(OP3, 'I', valueint);
	                    } else if (type == 'F') {
	                       float valuefloat = (float) S.GetFloat(OP1);
	                       S.UpdateSymbol(OP3, 'F', valuefloat);
	                    } else if (type == 'S') {
	                       String valuestring = S.GetString(OP1);
	                       S.UpdateSymbol(OP3, 'S', valuestring); 
	                    }
	                	
	                	PC++;
	                	break;
				    //If Quad is calling OPCODE 6 then print the symbol and integer of OP3 or the results                   
	                case _PRINT:
	                	char type1 = S.GetDataType(OP3);
	                	if (type1=='I') {
	                		System.out.println(S.GetInteger(OP3));
	                	}
	                	else if (type1=='S') {
	                		System.out.println(S.GetString(OP3));
	                	}
	                    PC++;
	                    break;
	                //This case was provided to me in instructions! 	                    
	                case _READ:
	                	// Assume parameter/operand must be an integer value	                	
	                	// Make a scanner to read from CONSOLE
	                	Scanner sc = new Scanner(System.in);
	                	// Put out a prompt to the user
	                	System.out.print('>');
	                	// Read one integer only
	                	int readval = sc.nextInt();
	                	// Op3 has the SymbolTable index we need, update its value
	                	S.UpdateSymbol(OP3,'I',readval);
	                	// Deallocate the scanner
	                	sc = null;
	                	// Increment Program Counter
	                	PC++;
	                	break;	                
                    //IF OPCODE 8 then PC equals to OP3. Doing automatic jump
	                case _JMP:
	                	PC = OP3;
                     break;
	                //If OPCODE 9 and OP1 is equals to 0 then do the jump
                    //Otherwise increment PC
	                case _JZ:
	                	if (S.GetInteger(OP1) == 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	                break;
	                //If OPCODE 10 and OP1 is greater than 0 then do the jump
	                //Otherwise increment PC
	                case _JP:
	                	if (S.GetInteger(OP1) > 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	               break;
	             //If OPCODE 11 and OP1 is less than to 0 then do the jump
                 //Otherwise increment PC
	                case _JN:
	                	if (S.GetInteger(OP1) < 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	                break;
	              //If OPCODE 12 and OP1 is not equals to 0 then do the jump
                  //Otherwise increment PC       
	                case _JNZ:
	                	if (S.GetInteger(OP1) != 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	                 break;
	               //If OPCODE 13 and OP1 is less than equal to 0 then do the jump
	               //Otherwise increment PC
	                case _JNP:
	                	if (S.GetInteger(OP1) <= 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	                 break;
	               //If OPCODE 14 and OP1 is less greater than equal to 0 then do the jump
		           //Otherwise increment PC  
	                case _JNN:
	                	if (S.GetInteger(OP1) >= 0) {
                            PC = OP3;
                        } else {
                            PC++;
                        }
	                 break;
	                //Branch (unconditional)              	                 	                 
	                case _JINDR:
	                    //OP3 contains the index in the Symbol Table
	                	PC = S.GetInteger(OP3);           
	                    break;	                
	            }//end of switch statement
	        }// end of if statement  
	    } //end of while
	}//End of InterpretQuads
	//This was given to me. Its changing the format to make the PC, OPCODE, OP1, OP2 and OP3 look like high level language
	private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ) {
		String result = "";
		result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)+"     ").substring(0,6)+String.format("%02d",op1)+", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
	    return result;
	}

	//Factorial SymbolTable initialization
	    private static void InitSTF(SymbolTable st) {
	    	st.AddSymbol("n", 'V', 10); //Symbol n is a variable that its int is 10
	    	st.AddSymbol("I", 'V', 0); //Symbol I is a variable that its int is 0
	    	st.AddSymbol("product", 'v', 0); //Symbol product is a variable that its int is 0
	    	st.AddSymbol("1", 'C', 1); //Symbol 1 is a constant that its int is 1
	    	st.AddSymbol("temp", 'v', 0); //Symbol temp is a variable that its int is 0
	    }

	    // Factorial QuadTable initialization
	    private void InitQTF(QuadTable qt) {
	        qt.AddQuad(5, 3, 0, 2); //MOV prod = 1
	        qt.AddQuad(5, 3, 0, 1); //MOV i = 1
	        qt.AddQuad(3, 1, 0, 4); //SUB $temp = i - n
	        qt.AddQuad(10, 4, 0, 7); //If temp <= 0 go to print quad 7
	        qt.AddQuad(2, 2, 1, 2); //MUL product = product * i
	        qt.AddQuad(4, 1, 3, 1); //ADD i = i + 1
	        qt.AddQuad(8, 0, 0, 2); //jmp to qt.AddQuad(3, 1, 0, 4); // SUB $temp = i - n
	        qt.AddQuad(6,0,0,2); 	//Print product
	        qt.AddQuad(0, 0, 0, 0);	//Stop       
	    }

	    // Summation SymbolTable initialization
	    private static void InitSTS(SymbolTable st) {
	    	st.AddSymbol("n", 'V', 10); //Symbol n is a variable that its int is 10
	    	st.AddSymbol("i", 'V', 0); //Symbol i is a variable that its int is 0
	    	st.AddSymbol("sum", 'v', 0); //Symbol sum is a variable that its int is 0
	    	st.AddSymbol("1", 'c', 1);  //Symbol 1 is a constant that its int is 1
	    	st.AddSymbol("$temp", 'v', 0);  //Symbol temp is a variable that its int is 0
	    }
//JP if greater than 0
	    // Summation QuadTable initialization
	    private void InitQTS(QuadTable qt) {
	    	 qt.AddQuad(5, 3, 0, 1); //MOV sum = 0
		     qt.AddQuad(5, 3, 0, 1); //MOV i = 1
		     qt.AddQuad(3, 1, 0, 4); //SUB $temp = i - n
		     qt.AddQuad(10, 4, 0, 7); //If temp <= 0 go to print at quad 7
		     qt.AddQuad(4, 2, 1, 2); //MUL product = product * i
		     qt.AddQuad(4, 1, 3, 1); //ADD i = i + 1
		     qt.AddQuad(8, 0, 0, 2); //jmp to qt.AddQuad(3, 1, 0, 4); // SUB $temp = i - n
		     qt.AddQuad(6,0,0,2); //Print sum this time
		     qt.AddQuad(0, 0, 0, 0); //Stop   
	    }
	    
	    
	 // public method for returning opcode int for a given opcode
		public int opcodeFor(String op) {

			return optable.LookupName(op);
		}
}