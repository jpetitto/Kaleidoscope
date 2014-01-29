import java.io.FileNotFoundException;


public class Driver {
	public static void main(String[] args) throws FileNotFoundException {
		Parser parser = new Parser("sourcecode.k");
		Token currToken = parser.getNextToken(); // prime the pump
		
		while (true) {
			switch (currToken.getType()) {
				case EOF:
					return;
				case DEF:
					if (parser.parseDefinition() != null)
						System.err.println("Parsed a function definition");
					else
						currToken = parser.getNextToken();
					break;
				case EXTERN:
					if (parser.parseExtern() != null)
						System.err.println("Parsed an extern function");
					else
						currToken = parser.getNextToken();
					break;
				default:
					if (currToken.getUnknownChar() != ';' && parser.parseTopLevelExpr() != null)
						System.err.println("Parsed a top-level expression");
					else
						currToken = parser.getNextToken();
					break;
			}
			
			currToken = parser.getCurrToken();
		}
	}
}
