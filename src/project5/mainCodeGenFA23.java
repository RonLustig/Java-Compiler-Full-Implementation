package project5;

//import ADT.SymbolTable;
//import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill FALL 2023
 */
public class mainCodeGenFA23 {

    public static void main(String[] args) {
        String filePath = args[0];
        System.out.println("Code Generation FA2023, by Ron Lustig");
        System.out.println("Parsing "+filePath);
        boolean traceon = true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}
