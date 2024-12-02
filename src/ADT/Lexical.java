/*
 The following code is provided by the instructor for the completion of PHASE 2 
of the compiler project for CS4100/5100.

FALL 2023 version

STUDENTS ARE TO PROVIDE THE FOLLOWING CODE FOR THE COMPLETION OF THE ASSIGNMENT:

1) Initialize the 2 reserve tables, which are fields in the Lexical class,
    named reserveWords and mnemonics.  Create the following functions.
    These calls are in the lexical constructor:
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);
    One-line examples are provided below

2) getIdentifier, getNumber, getString, and getOtherToken. getOtherToken recognizes
   one- and two-character tokesn in the language. 



PROVIDED UTILITY FUNCTIONS THAT STUDENT MAY NEED TO CALL-
1) YOU MUST NOT USE MAGIC NUMBERS, that is, numeric constants anywhere in the code,
   like "if tokencode == 50".  Instead, use the following:
// To get an integer for a given mnemonic, use
public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
// To get the full reserve word for a given mnemonic, use:
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }

Other methods:
    private void consoleShowError(String message)
    private boolean isLetter(char ch)
    private boolean isDigit(char ch)
    private boolean isStringStart(char ch)
    private boolean isWhitespace(char ch)
    public char GetNextChar()
To check numeric formats of strings to see if they are valid, use:

    public boolean doubleOK(String stin) 
    public boolean integerOK(String stin)
    

CALLING OTHER FUNCTIONS LIKE getNextLine COULD BREAK THE EXISTING CODE!

 */
package ADT;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 *
 * @author abrouill
 */
import java.io.*;

public class Lexical {

    private File file;                        //File to be read for input
    private FileReader filereader;            //Reader, Java reqd
    private BufferedReader bufferedreader;    //Buffered, Java reqd
    private String line;                      //Current line of input from file   
    private int linePos;                      //Current character position
    //  in the current line
    private SymbolTable saveSymbols;          //SymbolTable used in Lexical
    //  sent as parameter to construct
    private boolean EOF;                      //End Of File indicator
    private boolean echo;                     //true means echo each input line
    private boolean printToken;               //true to print found tokens here
    private int lineCount;                    //line #in file, for echo-ing
    private boolean needLine;                 //track when to read a new line

//Tables to hold the reserve words and the mnemonics for token codes
    private final int sizeReserveTable = 50;
    private ReserveTable reserveWords = new ReserveTable(sizeReserveTable); //a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(sizeReserveTable); //a few more than # reserves

    //constructor
    public Lexical(String filename, SymbolTable symbols, boolean echoOn) {
        saveSymbols = symbols;  //map the initialized parameter to the local ST
        echo = echoOn;          //store echo status
        lineCount = 0;          //start the line number count
        line = "";              //line starts empty
        needLine = true;        //need to read a line
        printToken = false;     //default OFF, do not print tokesn here
        //  within GetNextToken; call setPrintToken to
        //  change it publicly.
       // setPrintToken(echoOn);
        linePos = -1;           //no chars read yet
        //call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        //set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);    //creates a new file instance  
            filereader = new FileReader(file);   //reads the file  
            bufferedreader = new BufferedReader(filereader);  //creates a buffering character input stream  
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    }

    // inner class "token" is declared here, no accessors needed
    public class token {

        public String lexeme;
        public int code;
        public String mnemonic;

        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }

// This is the DISCARDABLE dummy method for getting and returning single characters
// STUDENT TURN-IN SHOULD NOT USE THIS!    
    private token dummyGet() {
        token result = new token();
        result.lexeme = "" + currCh; //have the first char
        currCh = GetNextChar();
        result.code = 0;
        result.mnemonic = "DUMY";
        return result;

    }

    
// ******************* PUBLIC USEFUL METHODS
// These are nice for syntax to call later 
// given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
// given a mnemonic, return its reserve word

    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }

    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }
// DEBUG enabler, turns on/OFF token printing inside of GetNextToken

    public void setPrintToken(boolean on) {
        printToken = on;
    }

    /* @@@ */
    private void initReserveWords(ReserveTable reserveWords) {    	
       //Adding everything needed to reserve table
    	reserveWords.Add("GOTO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4); 
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7); 
        reserveWords.Add("OF", 8);
        reserveWords.Add("WRITELN", 9); 
        reserveWords.Add("READLN", 10); 
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("WHILE", 14);       
        reserveWords.Add("UNIT", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("PROCEDURE", 19);
        reserveWords.Add("DOWNTO", 20);
        reserveWords.Add("FUNCTION", 21);
        reserveWords.Add("RETURN", 22);
        reserveWords.Add("REAL", 23); // to match
        reserveWords.Add("STRING", 24);
        reserveWords.Add("ARRAY", 25);
        reserveWords.Add("IDENTIFIER", 50);
        // 1 and 2-char
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
        reserveWords.Add("UN", 99);  
    }

    /* @@@ */
    private void initMnemonics(ReserveTable mnemonics) {
    	//Creating 4 character mnemonics of my own      
    	 mnemonics.Add("GO_T", 0);
         mnemonics.Add("INTE", 1);
         mnemonics.Add("TO_T", 2);
         mnemonics.Add("DO_D", 3);
         mnemonics.Add("IF_F", 4);
         mnemonics.Add("TH_E", 5);
         mnemonics.Add("EL_S", 6);
         mnemonics.Add("FOR_", 7);
         mnemonics.Add("OF_F", 8);
         mnemonics.Add("WRIT", 9);
         mnemonics.Add("READ", 10);
         mnemonics.Add("BEGI", 11);
         mnemonics.Add("END_", 12);
         mnemonics.Add("VARI", 13);
         mnemonics.Add("WHIL", 14);
         mnemonics.Add("UNII",15);
         mnemonics.Add("LABL", 16);
         mnemonics.Add("REPE", 17);
         mnemonics.Add("UNTI", 18);
         mnemonics.Add("PROC", 19);
         mnemonics.Add("DOWT", 20);
         mnemonics.Add("FUNC", 21);
         mnemonics.Add("RETN", 22);
         mnemonics.Add("RE_L", 23);
         mnemonics.Add("STRI", 24);
         mnemonics.Add("ARRY", 25);
                 
         mnemonics.Add("DIVI", 30);
         mnemonics.Add("MULT", 31);
         mnemonics.Add("AD_D", 32);
         mnemonics.Add("SUBT", 33);
         mnemonics.Add("LEPR", 34);
         mnemonics.Add("RIPR", 35);
         mnemonics.Add("SICO", 36);
         mnemonics.Add("EQUT", 37);
         mnemonics.Add("GRTH", 38);
         mnemonics.Add("LETH", 39);
         mnemonics.Add("GREQ", 40);
         mnemonics.Add("LEEQ", 41);
         mnemonics.Add("EQ_L", 42);
         mnemonics.Add("NTQL",43);
         mnemonics.Add("COMM",44);
         mnemonics.Add("LEBR",45);
         mnemonics.Add("RIBR",46);
         mnemonics.Add("COLO", 47);
         mnemonics.Add("PERI", 48);
         mnemonics.Add("IDEN", 50);
         mnemonics.Add("INTC", 51);
         mnemonics.Add("FLOC", 52);
         mnemonics.Add("STRIC", 53);
         mnemonics.Add("UNDE", 99);
            
    }


// ********************** UTILITY FUNCTIONS
    private void consoleShowError(String message) {
        System.out.println("**** ERROR FOUND: " + message);
    }

    // Character category for alphabetic chars
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }

    // Character category for 0..9 
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }

    // Category for any whitespace to be skipped over
    private boolean isWhitespace(char ch) {
        // SPACE, TAB, NEWLINE are white space
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else // 
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    // Called by GetNextChar when the cahracters in the current line are used up.
    // STUDENT CODE SHOULD NOT EVER CALL THIS!
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a character
    }

    // Called to get the next character from file, automatically gets a new
    //      line when needed. CALL THIS TO GET CHARACTERS FOR GETIDENT etc.
    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else { //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }

// The constants below allow flexible comment start/end characters    
    final char commentStart_1 = '{';
    final char commentEnd_1 = '}';
    final char commentStart_2 = '(';
    final char commentPairChar = '*';
    final char commentEnd_2 = ')';

// Skips past single and multi-line comments, and outputs UNTERMINATED 
//  COMMENT when end of line is reached before terminating
    String unterminatedComment = "Comment not terminated before End Of File";

    public char skipComment(char curr) {
        if (curr == commentStart_1) {
            curr = GetNextChar();
            while ((curr != commentEnd_1) && (!EOF)) {
                curr = GetNextChar();
            }
            if (EOF) {
                consoleShowError(unterminatedComment);
            } else {
                curr = GetNextChar();
            }
        } else {
            if ((curr == commentStart_2) && (PeekNextChar() == commentPairChar)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
//            while ((curr != commentPairChar) && (PeekNextChar() != commentEnd_2) &&(!EOF)) {
                while ((!((curr == commentPairChar) && (PeekNextChar() == commentEnd_2))) && (!EOF)) {
//                if (lineCount >=4) {
                    //              System.out.println("In Comment, curr, peek: "+curr+", "+PeekNextChar());}
                    curr = GetNextChar();
                }
                if (EOF) {
                    consoleShowError(unterminatedComment);
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }

        }
        return (curr);
    }

    // Reads past all whitespace as defined by isWhiteSpace
    // NOTE THAT COMMENTS ARE SKIPPED AS WHITESPACE AS WELL!
    public char skipWhiteSpace() {

        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }

    private boolean isStringStart(char ch) {
        return ch == '"';
    }

//This method recives the input and if it starts with a letter and then it contains 
//either a letter or a digit or underscore or dollar sign
//Then add it as identifier
//Since reserve words and identifiers is similar we check to see if its already a reserve word or not
//Then return each input as result after indentifying if its an identifier
    char currCh;
    private token getIdentifier(){
    	token result = new token();
    	result.lexeme = "" + currCh; //have the first char
    	//If it starts with a letter it can potentially be an identifier
    	if (isLetter(currCh)) {
    		currCh = GetNextChar();
    	//While its either a letter or a digit or underscore or dollar sign without getting to EOF add it to lexeme and do GNC
    	while (isLetter(currCh)||(isDigit(currCh)) || currCh == '_'  || currCh == '$' && !EOF) {
    		result.lexeme = result.lexeme + currCh; //extend lexeme
    		currCh = GetNextChar();
    	}    	    	    	
    	// end of token, lookup or IDENT
    	result.code = reserveWords.LookupName(result.lexeme);
    	}//end of if starting with letter
    	//If its not a reserve word
    	if (result.code == -1) {
    		//If the input is not a reserve word its an identifier
    		result.code=codeFor("IDEN");
    		// Identifiers need to be added to the symbol table after truncation
        	//as needed
    		if (result.lexeme.length() <20) {
    			saveSymbols.AddSymbol(result.lexeme, 'V', 0);
    		}
    		// Check if the identifier exceeds the length limitation
            if (result.lexeme.length() > 20) {
                // Truncate the identifier to 30 characters              
            	saveSymbols.AddSymbol(result.lexeme.substring(0, 20), 'V', 0);
                // Generate a warning message for identifier truncation
                System.out.println("Warning: Identifier truncated to 30 characters.");
            }
    	}
    	    	
    	return result;
    }

 //This method, recives numbers
 //It check if its a digit and while its a digit add it
 //Then it determines if its a floating or not by checking for '.'
 //Then it check if it contains E and if it does then we need to ensure that it contains a digit right after the E
 //If it contains E without immidiate digit then its given the code 99 for undefined
 //Otherwise its either a float with . or just integer
 //At the end after determining if its undefined, an integer or float we return the results
    private token getNumber() {
        token result = new token();
        result.lexeme = "" + currCh; // Have the first character
        currCh = GetNextChar();

        // Add digits to the lexeme
        while (isDigit(currCh) && !EOF) {
            result.lexeme += currCh;
            currCh = GetNextChar();
        }

//If its an integer, then add it as integer to symbol table and give it the code with its menmonic
        if (!isDigit(currCh) && currCh != '.') {
            result.code = codeFor("INTC");
            // Truncate the identifier to 6 characters if it's too long
            if (result.lexeme.length() > 6) {               
                saveSymbols.AddSymbol(result.lexeme.substring(0, 6), 'C', 0);
            }
            else {
            	saveSymbols.AddSymbol(result.lexeme, 'C',(int)Long.parseLong(result.lexeme));
            }
            return result;
        }
//If it contains a . its a float so add it as a float using menmonic for float and add it as float to symbol table
        if (currCh == '.') {
            // Add digits to the lexeme
            do {
                result.lexeme += currCh;
                currCh = GetNextChar();
            } while (isDigit(currCh) && !EOF);

// If there are no more digits after the '.', then add it using float menmonic and add it as float to symbol table
            if (!isDigit(currCh) && currCh!='E') {
            	result.code = codeFor("FLOC");
            	//Checking if truncating needed
            	if (result.lexeme.length() > 12) {   
            		System.out.println("Float length > 12, truncated " + result.lexeme + " to " + result.lexeme.substring(0, 12));
                    saveSymbols.AddSymbol(result.lexeme.substring(0, 12), 'C', 0.0);
                }
                else {
                saveSymbols.AddSymbol(result.lexeme, 'C', Double.parseDouble(result.lexeme));
                }
                return result;
            }                     
        }// End if if .
        //Checking if it contains an E, if it does add it
        if (currCh=='E') {
        	 result.lexeme += currCh;
             currCh = GetNextChar();
             
             //If there is no digits right after E, then its undefined return 99 error
             if (!isDigit(currCh)) {
            	 result.code = codeFor("UNDE");
                 return result;
             }
             //If there is a digit after E then its an appropriate float and add it as float
             else if (isDigit(currCh)) {
            	// Add digits to the lexeme
                 while (isDigit(currCh) && !EOF) {
                     result.lexeme += currCh;
                     currCh = GetNextChar();
                 }
                 //If no more left then use float menmonic and add to symbol table
                 if (!isDigit(currCh) && currCh!='E') {
                 	result.code = codeFor("FLOC");                 	
                    saveSymbols.AddSymbol(result.lexeme, 'C', Double.parseDouble(result.lexeme));                   
                    return result;
                 }                 
             }                   	
        }
                                  
        return result;
    }
//This method gets a string
//It determines if its a string
//It is a sting if it contains double qoute at the beggining and at the end and the whole input is a string
//If it does not have double qoute at the beggining and at the end then its undefined 99
    private token getString() {
    	token result = new token();
    	currCh = GetNextChar();
    	
    	//Skip white spaces
    	//skipWhiteSpace();
    	    //While resiving input that doesn't end yet add it to lexeme as string and get next char	    		
    		while (currCh != '\n' && !EOF && !isStringStart(currCh)) {
    			result.lexeme = result.lexeme + currCh; //extend lexeme
        		currCh = GetNextChar();
    		}
    		//check for closing qoute, if there is a closing qoute make it be a string by the menmonic of it
    		if (isStringStart(currCh)) {
    			result.code = codeFor("STRI");
    			//checking if truncating is needed
    			//if (result.lexeme.length() > 20) {
    				//saveSymbols.AddSymbol(result.lexeme.substring(0, 20), 'C', result.lexeme);
    			//}
    			//else {
    			saveSymbols.AddSymbol(result.lexeme, 'C', result.lexeme);
    			//}
    			currCh = GetNextChar();
    			
    		}
    		//If it doesn't end with double qoute and doesn't start with double qoute then its an error code 99
    		else {
    			consoleShowError("unterminated string");    			
    			result.code = codeFor("UNDE");
    		}
    		
        return result;
    }
       
//This method adds all input that are not identifiers, strings or numbers
//I first check if its two characters long and if it is then I make sure its added to be one thing together
//Otherwise its just one character 
//Then return result after determining the appropriate chacters and look up code with its menmonic that determines it
    private token getOtherToken() {
    	token result = new token();
        result.lexeme = "" + currCh; // Have the first character

 // Check for two-character tokens             
if (currCh == '<' && PeekNextChar() == '>') {
	currCh = GetNextChar();
	result.lexeme += currCh;
    result.code = codeFor("NTQL");
 }

else if (currCh == '<' && PeekNextChar() == '=') {
	currCh = GetNextChar();
	result.lexeme += currCh;
    result.code = codeFor("LEEQ");
}

else if (currCh == '>' && PeekNextChar() == '=') {
	currCh = GetNextChar();
	result.lexeme += currCh;
    result.code = codeFor("GREQ");
}
else if (currCh == ':' && PeekNextChar() == '=') {
	currCh = GetNextChar();
	result.lexeme += currCh;
    result.code = codeFor("EQUT");
}


//check for one char tokens
else if (currCh == '>') {
result.code = codeFor("GRTH");
}
else if (currCh == '<') {
result.code = codeFor("LETH");
}

else if (currCh == '/') {
result.code = codeFor("DIVI");
}
else if (currCh == '*') {
	result.code = codeFor("MULT");
}
else if (currCh == '+') {
	result.code = codeFor("AD_D");
}
else if (currCh == '-') {
	result.code = codeFor("SUBT");
}
else if (currCh == '(') {
	result.code = codeFor("LEPR");
}
else if (currCh == ')') {
	result.code = codeFor("RIPR");
}
else if (currCh == ';') {
	result.code = codeFor("SICO");
}
else if (currCh == ',') {
	result.code = codeFor("COMM");
}
else if (currCh == '[') {
	result.code = codeFor("LEBR");
}
else if (currCh == ']') {
	result.code = codeFor("RIBR");
}
else if (currCh == ':') {
	result.code = codeFor("COLO");
}
else if (currCh == '=') {
	result.code = codeFor("EQ_L");
}
else if (currCh == '.') {
	result.code = codeFor("PERI");
}
//If its nither of these cases then its undefined error code 99, then return result and get next character
else {
	result.code = codeFor("UNDE");
}	
currCh = GetNextChar(); // Move to the next character
return result;
}

    // Checks to see if a string contains a valid DOUBLE 
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // Checks the input string for a valid INTEGER

    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }
    


    public token GetNextToken() {
        token result = new token();

        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is identifier
            result = getIdentifier();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else //default char checks
        {
            result = getOtherToken();
        }

        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }
//set the mnemonic
        if (result != null) {
// THIS LINE REMOVED-- PUT BACK IN TO USE LOOKUP            
            result.mnemonic = mnemonics.LookupCode(result.code);
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }
        return result;

    }

}
