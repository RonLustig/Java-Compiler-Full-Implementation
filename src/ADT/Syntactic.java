package ADT;
/**
 *
 * @author Ron Lustig
 * 
 */
public class Syntactic {
	private String filein; // The full file path to input file
	private SymbolTable symbolList; // Symbol table storing ident/const
	///NEW///////////////////////////////
	private QuadTable quads;
	private Interpreter interp;
	///NEW///////////////////////////////
	private Lexical lex; // Lexical analyzer
	private Lexical.token token; // Next Token retrieved
	private boolean traceon; // Controls tracing mode
	private int level = 0; // Controls indent for trace mode
	private boolean anyErrors; // Set TRUE if an error happens
	private final int symbolSize = 250;
	///NEW///////////////////////////////
	private final int quadSize = 1000;
	private int Minus1Index;
	private int Plus1Index;
	///NEW///////////////////////////////
	public Syntactic(String filename, boolean traceOn) {
		filein = filename;
		traceon = false;
		symbolList = new SymbolTable(symbolSize);
		// Add these to symbol table to accommodate sign flips
		Minus1Index = symbolList.AddSymbol("-1", 'C', -1);
		Plus1Index = symbolList.AddSymbol("1", 'C', 1);
		quads = new QuadTable(quadSize);
		interp = new Interpreter();
		////////////////////////////////////////////////////////////////
		lex = new Lexical(filein, symbolList, true);
		lex.setPrintToken(traceon);
		anyErrors = false;
	}
	// The interface to the syntax analyzer, initiates parsing
	// Uses variable RECUR to get return values throughout the non-terminal methods	
	//Interface to the syntax analyzer, initiates parsing
	public void parse() {
		//Use source filename as pattern for symbol table and quad table output later
		String filenameBase = filein.substring(0, filein.length() - 4);
		System.out.println(filenameBase);
		int recur = 0;
		//Prime the pump, get first token
		token = lex.GetNextToken();
		//Call PROGRAM
		recur = Program();
		//Done with recursion, so add the final STOP quad
		quads.AddQuad(0, 0, 0, 0);
		//Print SymbolTable, QuadTable before execute
		symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
		quads.PrintQuadTable(filenameBase + "QUADS.txt");
		//interpret
		if (!anyErrors) {
			interp.InterpretQuads(quads, symbolList, false, filenameBase + "TRACE.txt");
		} else {
			System.out.println("Errors, unable to run program.");
		}
			symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");
	}
	// Non Terminal PROGIDENTIFIER is fully implemented here, leave it as-is.
	private int ProgIdentifier() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		// This non-term is used to uniquely mark the program identifier
		if (token.code == lex.codeFor("IDEN")) {
			// Because this is the progIdentifier, it will get a 'P' type toprevent re-use
			// as a var
			symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);
			// move on
			token = lex.GetNextToken();
		}
		return recur;
	}
	// Non Terminal PROGRAM is fully implemented here.
	//CFG for program, there was nothing additional that needed to be done
	private int Program() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Program", true);
		if (token.code == lex.codeFor("UNII")) {
			token = lex.GetNextToken();
			recur = ProgIdentifier();
			if (token.code == lex.codeFor("SICO")) {
				token = lex.GetNextToken();
				recur = Block();
				if (token.code == lex.codeFor("PERI")) {
					if (!anyErrors) {
						System.out.println("Success.");
					} else {
						System.out.println("Compilation failed.");
					}
				} else {
					error(lex.reserveFor("PERI"), token.lexeme);
				}
			} else {
				error(lex.reserveFor("SICO"), token.lexeme);
			}
		} else {
			error(lex.reserveFor("UNII"), token.lexeme);
		}
		trace("Program", false);
		return recur;
	}
	// Non Terminal BLOCK is fully implemented here.
	//minor changes it is a none-terminal that used to be as one
	//now it just call variable decent and the rest is implemented inside block body
		private int Block() {
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			trace("Block", true);
			//Looking for a variable 
			while (token.code == lex.codeFor("VARI")) {
				recur = Variabledecsec();
			}
			
			recur=BlockBody();
			trace("Block", false);
			return recur;
		}
///////////////////////////////////////////Part B//////////////////////////////////////////////////////////	
	//<block-body> -> $BEGIN <statement> {$SCOLN <statement>}
		//$END
		//This method is seperation none-terminal from block
	//Same old block body which gets the Begin, semicolon and an END_
		private int BlockBody() {
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			trace("BlockBody", true);
			//looking for begin
			if (token.code == lex.codeFor("BEGI")) {
				token = lex.GetNextToken();
				recur = Statement();
			//while more semiclon and no error do the following
				while ((token.code == lex.codeFor("SICO")) && (!lex.EOF()) && (!anyErrors)) {
					token = lex.GetNextToken();
					recur = Statement();
				}
			//Looking for an END token and take it if you find it
				if (token.code == lex.codeFor("END_")) {
					token = lex.GetNextToken();
				} 
			} 
			trace("BlockBody", false);
			return recur;
		}
//This ensuuring a proper variable 
//Used as none-terminal inside many places
		private int Variabledecsec() {
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			trace("Variabledecsec", true);
			//checking for variable
			if (token.code == lex.codeFor("VARI")) {
				token = lex.GetNextToken();
				//this is a do while more identifiers coming in
				do {
					if (token.code == lex.codeFor("IDEN")) {
						token = lex.GetNextToken();
						//$COMMA <identifier>
						while (token.code == lex.codeFor("COMM")) {
							token = lex.GetNextToken();
							if (token.code == lex.codeFor("IDEN")) {
								token = lex.GetNextToken();
							}
						}
						//$COLON <simple type> $SEMICOLON
						if (token.code == lex.codeFor("COLO")) {
							token = lex.GetNextToken();
						}
						//ensuring the rest of the rules are correct
						if (token.code == lex.codeFor("INTE") || token.code == lex.codeFor("FLOC") || token.code == lex.codeFor("STRI") ) {
							recur=Simpletype();
						}
					//Looking for a semicolon and this do while will contine until there are no more identifiers
						if (token.code == lex.codeFor("SICO")) {
							token = lex.GetNextToken();
						}			
					}
				}while (token.code == lex.codeFor("IDEN"));
			}
			trace("Variabledecsec", false);
			return recur;
		}
//This CFG just takes int, float or string		
		private int Simpletype() {
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			trace("Simpletype", true);
			//$INTEGER | $FLOAT | $STRING
			//this just take any int, float or string 
			if (token.code == lex.codeFor("INTE") || token.code == lex.codeFor("FLOC") || token.code == lex.codeFor("STRI") ) {
				token = lex.GetNextToken();
			}
			trace("Simpletype", false);
			return recur;
		}
//New Releexpression, This time it saves the left operand from simpleexpression and the right from simpleexpression
//Saving the quad and updating quad accordingly
//Using a temp variable 
		int relexpression() {
			int left, right, saveRelop, result, temp;
			
		left = simpleexpression(); //get the left operand, our ‘A’
		saveRelop = relop(); //returns tokenCode of rel
		right = simpleexpression();//the right operand, our ‘B’
		temp = symbolList.GenSymbol(); //Create temp var in symbol table
		quads.AddQuad(3, left, right, temp); //compare A-B
		result = quads.NextQuad(); //Save Q index where branch will be
		quads.AddQuad(relopToOpcode(saveRelop),temp,0,0);
		//target set later
		return (result);
	}
//This method is an extra helper
//It just checks for any kind of condition
//It is used as a none-terminal inside real expression
		private int relop() {
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			trace("relop", true);
			//$EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
			// = or < or > or != or <= or >=
	if (token.code == lex.codeFor("EQ_L") || token.code == lex.codeFor("LETH") 
			|| token.code == lex.codeFor("GRTH") || token.code == lex.codeFor("NTQL") 
			|| token.code == lex.codeFor("GREQ") || token.code == lex.codeFor("LEEQ")) {
		//Check here
				recur = token.code;
				token = lex.GetNextToken();
			}
			trace("relop", false);
			return recur;
		}
		
//This cfg handles the read from the statement method or cfg
//it is called from statement
//when it gets a while it checks for the proper cfg for that		
		private int handleReadln() {
			int recur = 0;
			int toprint = 0;
			if (anyErrors) {
			return -1;
			}
			//$READLN $LPAR <identifier> $RPAR
			trace("handleReadln", true);
			//got here from a read token, move past it...
			token = lex.GetNextToken();
			//look for ( stringconst, ident, or simpleexp )
			if (token.code == lex.codeFor("LEPR")) { //move on
				token = lex.GetNextToken();
				if ((token.code ==lex.codeFor("IDEN"))) {
					// save index for string literal or identifier
					toprint = symbolList.LookupSymbol(token.lexeme);
					//move on
					token = lex.GetNextToken();
					} 
					quads.AddQuad(7, 0, 0, toprint);
					//now need right ")"
					if (token.code == lex.codeFor("RIPR")) {
						//move on
						token = lex.GetNextToken();
					} 
				} 
					// end lpar group
					trace("handleReadln", false);
					return recur;
		}		
//This is the CFG for a for loop
//It is looking for a For token,$ASSIGN,TO_T and Do
//It is saving indexes and also increments by 1
//Evaluating equations properly and checking it with a JP
		private int handleFor() {
		    int saveTop, branchQuad,nextquad;
		    int left,right;
		    int recur = 0;
		    if (anyErrors) {
		        return -1;
		    }
		    trace("handleFor", true);
		    // Ensure there is a FOR
		    // Then take the token and call variable method to take care of it
		    if (token.code == lex.codeFor("FOR_")) {
		        token = lex.GetNextToken();
		        left = variable();

		        // Save top of loop index
		        saveTop = quads.NextQuad();

		        // Looking for $ASSIGN EQUT
		        if (token.code == lex.codeFor("EQUT")) {
		            token = lex.GetNextToken();
		            recur = simpleexpression();
		        }
		        //Move simple expression into variable - Variable = simpleexpression
		        quads.AddQuad(5, recur, 0, left);
		        
		        // Looking for a $TO
		        if (token.code == lex.codeFor("TO_T")) {
		            token = lex.GetNextToken();
		            recur = simpleexpression();
		        
		        }
		        //Creating temp variable to get index
		        int temp = symbolList.GenSymbol();
		        // Save branchQuad index
		        branchQuad = quads.NextQuad();
		        quads.AddQuad(3, left, recur, temp);
		        // Generate conditional jump based on TO condition
		        nextquad = quads.NextQuad();
		        quads.AddQuad(10, temp, 0, 0); 

		        // Looking for a $DO
		        if (token.code == lex.codeFor("DO_D")) {
		            token = lex.GetNextToken();
		            recur = Statement();
		        }
		        quads.AddQuad(4, left, Plus1Index, left);
		        // Generate unconditional jump to loop top
		        quads.AddQuad(8, 0, 0, branchQuad); // Jump quad, jump to top of loop

		        // Backfill the forward branch
		        quads.UpdateJump(nextquad, quads.NextQuad()); // Conditional jump to nextQuad
		    }

		    trace("handleFor", false);
		    return recur;
		}
//This cfg handles the if that gets from statement
//it is called from statement
//when it gets a while it checks for the proper cfg for that		
		private int handleIf() {
			int branchQuad, patchElse;
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			token=lex.GetNextToken(); //move past ‘if’
			branchQuad = relexpression(); //tells where branchTarget to be set
			if (token.code == lex.codeFor("TH_E")) {
				token=lex.GetNextToken();
				recur=Statement();
				if (token.code == lex.codeFor("EL_S")) //have to jump around to ??
				{
					token=lex.GetNextToken(); // move past ELSE
					patchElse = quads.NextQuad(); //save backfill quad to jump around
					//ELSE body, target is unknown now
					quads.AddQuad(8, 0, 0, 0);
					//backfill the FALSE IF branch
					quads.UpdateJump(branchQuad,quads.NextQuad());
					//conditional jump
					//gen ELSE body quads
					recur=Statement();
					//fill in end of ELSE part
					quads.UpdateJump(patchElse,quads.NextQuad());
				}
				else {
					quads.UpdateJump(branchQuad,quads.NextQuad());
				}
			}
			trace("handleIf", false);
			return recur;
		}
//This cfg handles the while
//it is called from statement
//when it gets a while it checks for the proper cfg for that
		private int handleWhile() {
			int saveTop, branchQuad;
			int recur = 0;
			if (anyErrors) {
				return -1;
			}
			// declared somewhere above are:
			// int saveTop – index, conditional quad
			// int branchQuad – index, branch to patch
			token=lex.GetNextToken(); //move past this WHILE token
			saveTop = quads.NextQuad(); //Before generating code, save top of loop idx
			//where unconditional branch will jump back
			branchQuad = relexpression(); //tells where branch target
			//to be set
			if (token.code == lex.codeFor("DO_D"))
			{
			token=lex.GetNextToken();
			recur=Statement(); //the loop body is processed
			quads.AddQuad(8, 0, 0, saveTop);//jump, top of loop
			//backfill the forward branch
			//Assume quad function for ease- to set 3rd op branch target
			quads.UpdateJump(branchQuad,quads.NextQuad());//conditional jump to
			// nextQuad
			} // End if DO found
			trace("handleWhile", false);
			return recur;
		}
//THIS method handles the entrie CFG for write
//Its been called from statement
//this goes through the whole rule
		private int handleWriteln() {
			int recur = 0;
			int toprint = 0;
			if (anyErrors) {
			return -1;
			}
			trace("handleWriteln", true);
			//got here from a WRITELN token, move past it...
			token = lex.GetNextToken();
			//look for ( stringconst, ident, or simpleexp )
			if (token.code == lex.codeFor("LEPR")) { //move on
				token = lex.GetNextToken();
				if ((token.code == lex.codeFor("STRI"))) {
					// save index for string literal or identifier
					toprint = symbolList.LookupSymbol(token.lexeme);
					//move on
					token = lex.GetNextToken();
					} else {
						toprint = simpleexpression();
					}
					//quads.AddQuad(interp.opcodeFor("PRINT"), 0, 0, toprint);
					quads.AddQuad(6, 0, 0, toprint);
					//now need right ")"
					if (token.code == lex.codeFor("RIPR")) {
						//move on
						token = lex.GetNextToken();
					} 
				} 
					// end lpar group
					trace("handlePrintn", false);
					return recur;
			}
	// Not a Non Terminal, but used to shorten Statement code body for readability.
	// <variable> $COLON-EQUALS <simple expression>
	//Handeling the variable from the left and taking simple expression from the right
	//It does a move operand
	//Doing the a:=5 type statements
	private int handleAssignment() {
		int right,left;
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("handleAssignment", true);
		//Variable gets the left side of the statement or the results something equals to something
		left = variable(); 
		if (token.code == lex.codeFor("EQUT")) //assignment operator
		{
			token=lex.GetNextToken(); //move ahead, next token
			right = simpleexpression(); //get result index
			quads.AddQuad(5,right,0,left);
		}
		trace("handleAssignment", false);
		return recur;
	}
///////////Added here More methods /////////////////////////////////////// Added Here New Methods ///////////////////////////////
//Here I created methods for Term, Factor, UnsignedConstant, UnsignedNumber and Mulop
//As you scroll down you have the fully implemented Simple Expression for part 1 and created Addop and Sign as it requires for simple expression 
//Term Starts with none-Terminal "Factor", Factor is required in order for CFG to be accepted
//Then there is an optional two none termianls that will go to two methods: Mulop and Factor, Mulop and Factor can happen 0 or more times
//If it does decide to go into mulop then it has to go to factor too 
//Its also checks for errors and is announcing when term is entering and exiting
	//<term> -> <factor> {<mulop> <factor> }*
	//This term is similar to simpleexpression
	//It fist determin if its a mutiply or a divide opcode
	//Then it calls factor and takes symbol and then update quads
	private int term() {
		int left, right,temp, opcode;
		trace("Term", true);
		left = factor(); //returns index of term result
		//While more mutiply or divide codes take it and work it its opcodes
		while ((token.code == lex.codeFor("MULT") || token.code == lex.codeFor("DIVI")) && !anyErrors) 
		{
			if (token.code == lex.codeFor("MULT")) {
				opcode = 2;
			} else {
				opcode = 1; 
			}
			token=lex.GetNextToken(); //move ahead
			right = factor(); // index of term result
			temp = symbolList.GenSymbol(); //GenSymbol; //index of new temp variable symbolList
			quads.AddQuad(opcode,left,right,temp); 
			left = temp; //new leftmost term is last result
		}
		trace("Term", false);
		return (left);
}
//This method is representing the none-terminal of factor in the CFG
//The rule for factor is stating that it has to go to non-terminal unsigned constant 
//or it has to go to the none-terminal Variable
//Or it has a terminal left parenthesis and a non-terminal simple expression and a terminal Right parenthesis
//Its also checks for errors and is announcing when factor is entering and exiting
	private int factor() {
		//if there are any errors return -1 otherwise return 0
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
	    trace("Factor",true);
	    //If its reciving a constant integer, constant float or a string then it will call the UnsignedConstant method 
	    //The unsigned Constant method will take care of any unsigned constants
	    //It also reciving the ouput of unsigned constant method
	    if (token.code == lex.codeFor("INTC") || token.code == lex.codeFor("FLOC") || token.code == lex.codeFor("STRI")) {
	    	recur=UnsignedConstant();
	    }
	    //Or if its reciving an input that is an identifier than call the variable method and recive the output of Variable
	    else if (token.code == lex.codeFor("IDEN")) {
	    	recur=variable();	
	    }
	    //Cheking if there is left parenthesis 
	    else if (token.code == lex.codeFor("LEPR")) {
	    	//If there is a left parenthesis then take it and call Simple Expression, then get the result of simple expression
	    	token = lex.GetNextToken();
	    	recur=simpleexpression();
	    	//Checking for right parenthesis, if there is a right parethesis then get the token of right parenthesis
	    	if (token.code == lex.codeFor("RIPR")) {
	    		token = lex.GetNextToken();
	    	}
	  
	    }//else if
	    trace("Factor",false);
	    return recur;
	}
//This method is representing the none-terminal of UnsignedConstant in the CFG
//Inside the UnsignedConstant there is none-terminal of UnsignedConstant
//This method checks if input is an unsigned constant
//If input is either a constant integer, constant float, or a string its an unsigned constant
//Then call Unsigned number method, as represented in the rule for UnsignedConstant
//Its also checks for errors and is announcing when UnsignedConstant is entering and exiting
private int UnsignedConstant() {
	int recur = 0;
	if (anyErrors) {
		return -1;
	}
	trace("UnsignedConstant", true);
	// only accepting a number
	recur = UnsignedNumber();
	trace("UnsignedConstant", false);
	return recur;
}
//Representing the none-terminal of UnsignedNumber, it goes to it through UnsignedConstant
//The rule for UnsignedNumber is that it expects either an integer constant or a float constant 
//If input is either an integer constant or a float constant, then it takes the output and gets the next token
//If its not either an integer constant or a float constant than it just returns 
//Its also checks for errors and is announcing when UnsignedNumber is entering and exiting
private int UnsignedNumber() {
int recur = 0;
	if (anyErrors) {
		return -1;
	}
	trace("UnsignedNumber", true);
	// float or int or ERROR
	// unsigned constant starts with integer or float number
	if ((token.code == lex.codeFor("INTC") || (token.code == lex.codeFor("FLOC")))) {
		// return the s.t. index
		recur = symbolList.LookupSymbol(token.lexeme);
		token = lex.GetNextToken();
	} 
	trace("UnsignedNumber", false);
return recur;
}

//This is implementing the simpleexpression sfg
//Similarly to how term is done it is determine the opcode which is either a + or -
//Then saves it using right and left 
//Afterwards it updates the quads and add to it
int simpleexpression() {
	int left, right, signval, temp, opcode;
	signval = sign(); //optional + or – sign at front,
	// returns -1 if neg, otherwise 1
	left = term(); //returns index of term result
	if (signval == -1) //Add a negation quad for the term
	{
		quads.AddQuad(2,left,Minus1Index,left); //quads.AddQuad(MULT,left,Minus1Index,left);
	}
	//This is adding terms together as they are found, adding Quads
	while (token.code == lex.codeFor("AD_D") || token.code == lex.codeFor("SUBT")) //is + or – operator (not sign) 
{ 
		if (token.code == lex.codeFor("AD_D")) {
			opcode = 4;
		} else {
			opcode = 3; 
		}
		
		token=lex.GetNextToken(); //move ahead
		right = term(); // index of term result
		temp = symbolList.GenSymbol(); //GenSymbol; //index of new temp variable symbolList
		quads.AddQuad(opcode,left,right,temp); 
		left = temp; //new leftmost term is last result
}
	return (left);
}
//This is relopToOpcode 
//This cfg is folling the rules provided in the assigtment descrition
//Looking for the proper sign and doing things accordingly
int relopToOpcode(int relop) //relop is the token code found
{
	int result=0;
	if (relop == lex.codeFor("EQ_L")) { //EQUAL: result = 12; 
		result = 12;
	}
	else if (relop == lex.codeFor("NTQL")) { //NOTEQUAL: result = 9; 
		result = 9;
	}
	else if (relop == lex.codeFor("LETH")) { //LESS: result = 14;
		result = 14;
	}
	else if (relop == lex.codeFor("GRTH")) { //GREATER: result = 13; 
		result = 13;
	}
	else if (relop == lex.codeFor("LEEQ")) { //LESSEQUAL: result = 10; 
		result = 10; 
	}
	else if (relop == lex.codeFor("GREQ")) { //GREATEREQUAL: result =11; 
		result =11; 
	}
  return result;
}
///////////  Added here  ///////////////////////////////////////////////////////////////////
//Added Sign and Addop methods as they are used for simple expression rule in the CFG
//This method is representing the none-terminal of Sign in the CFG
//The rule of Sign states that it is expecting either ADD or SUBTRACT as input
//If input is either ADD or SUBTRACT then consume token and get next token otherwise just return
//Sign is used inside simple expression 
//Its also checks for errors and is announcing when SimpleExpression is entering and exiting

//This method is following the provided guide
//It is checking if you get subtract token or add token
//If its a subtract then results returns a -1 otherwise it just moves ahead
int sign() // looks for optional sign in front of term
{
	int result=1; //only move ahead if + or - found; optional sign
if (token.code == lex.codeFor("SUBT")) {
	result = -1;
	token=lex.GetNextToken(); //move ahead
}
else if (token.code == lex.codeFor("AD_D")) {
	token=lex.GetNextToken(); //move ahead
}
	return (result);
}
///END OF SIGN AND ADDOP METHODS ////////////////////////////////////////////////////////////////////////////////////
	// Eventually this will handle all possible statement starts in
	// a nested if/else or switch structure. Only ASSIGNMENT is implemented now.
//This is the statement method
//hadle: identifier, begin, string, if, while, repeat, for, write, and read
//Based on the input it will go to each CFG
	private int Statement() {
		int recur = 0;
		if (anyErrors) {
			return -1;
		}
		trace("Statement", true);
		if (token.code == lex.codeFor("IDEN")) { // must be an ASSIGNMENT, this calls the assigtment CFG
			recur=handleAssignment();
		} else if (token.code == lex.codeFor("BEGI")) { //For Block Body, this calls the BlockBody CFG
			recur = BlockBody();
		}else if (token.code == lex.codeFor("STRI")) { //For String literal, this just getting the string as part of the fist check
			token = lex.GetNextToken(); //This happens after taking care of the assigtment
		} else if (token.code == lex.codeFor("IF_F")) { //must be an IF, then goes to IF CFG
			recur=handleIf();
		} else if (token.code == lex.codeFor("WHIL")) { //must be an WHILE, then goes to while CFG
			recur=handleWhile();
		} else if (token.code == lex.codeFor("FOR_")) { //must be an FOR,then goes to FOR CFG 
			recur = handleFor();
		} else if (token.code == lex.codeFor("WRIT")) { //must be an WRITELN, then goes to WRITE CFG 
			recur = handleWriteln();
		} else if (token.code == lex.codeFor("READ")) { //must be an READLN, then goes to READ CFG 
			recur = handleReadln();
		} 
		trace("Statement", false);
		return recur;
	}
	// Non-terminal VARIABLE just looks for an IDENTIFIER. Later, a
	// type-check can verify compatible math ops, or if casting is required.	
	int variable()
	{ 
		int result=-1;
		result=symbolList.LookupSymbol(token.lexeme);
		if (token.code == lex.codeFor("IDEN")) {
			token=lex.GetNextToken();
		}
	return(result);
	}
	/**
	 * *************************************************
	 */
	/* UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */
	// error provides a simple way to print an error statement to standard output
	// and avoid reduncancy
	private void error(String wanted, String got) {
		anyErrors = true;
		System.out.println("ERROR: Expected " + wanted + " but found " + got);
	}
	// trace simply RETURNs if traceon is false; otherwise, it prints an
	// ENTERING or EXITING message using the proc string
	private void trace(String proc, boolean enter) {
		String tabs = "";
		if (!traceon) {
			return;
		}
		if (enter) {
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("--> Entering " + proc);
			level++;
		} else {
			if (level > 0) {
				level--;
			}
			tabs = repeatChar(" ", level);
			System.out.print(tabs);
			System.out.println("<-- Exiting " + proc);
		}
	}
	// repeatChar returns a string containing x repetitions of string s;
	// nice for making a varying indent format
	private String repeatChar(String s, int x) {
		int i;
		String result = "";
		for (i = 1; i <= x; i++) {
			result = result + s;
		}
		return result;
	}
}