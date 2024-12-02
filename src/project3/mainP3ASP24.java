package project3;
import ADT.*;
/**
 *
 * @author abrouill SPRING 2024
 */
public class mainP3ASP24 {
	public static void main(String[] args) {
		String filePath = args[0];
		boolean traceon = true;
		System.out.println("Ron Lustig, 1778, CS4100/5100,SPRING 2024");
				System.out.println("INPUT FILE TO PROCESS IS: "+filePath);
				Syntactic parser = new Syntactic(filePath, traceon);
				parser.parse();
				System.out.println("Done.");
	}
}
