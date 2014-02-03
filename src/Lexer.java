package src;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Lexer {
	private BufferedReader buffer;
	private char lastChar = ' ';
	private final static char EOF = (char) -1;
	
	public Lexer(File file) throws FileNotFoundException {
		// setup buffer with file
		InputStream is = new FileInputStream(file);
		Reader reader = new InputStreamReader(is);
		buffer = new BufferedReader(reader);
	}
	
	public Token getToken() throws IOException {
		Token token; // will be used if value must be added to token
		
		// skip any whitespace
		while (Character.isWhitespace(lastChar))
			lastChar = (char) buffer.read();
		
		// identifier: [a-zA-Z][a-zA-Z0-9_]*
		if (Character.isLetter(lastChar)) {
			String identStr = "";
			do {
				identStr += lastChar;
				lastChar = (char) buffer.read();
			} while (Character.isLetterOrDigit(lastChar) || lastChar == '_');
			
			// check if identifier matches keyword
			if (identStr.equalsIgnoreCase("def"))
				return new Token(TokenType.DEF);
			if (identStr.equalsIgnoreCase("extern"))
				return new Token(TokenType.EXTERN);
			if (identStr.equalsIgnoreCase("if"))
				return new Token(TokenType.IF);
			if (identStr.equalsIgnoreCase("then"))
				return new Token(TokenType.THEN);
			if (identStr.equalsIgnoreCase("else"))
				return new Token(TokenType.ELSE);
			if (identStr.equalsIgnoreCase("for"))
				return new Token(TokenType.FOR);
			if (identStr.equalsIgnoreCase("in"))
				return new Token(TokenType.IN);
			
			// token is a user defined identifier
			token = new Token(TokenType.IDENTFIFIER);
			token.setIdentStr(identStr);
			return token;
		}
		
		// number: [0-9.]+
		if (Character.isDigit(lastChar)) {
			String numString = "";
			boolean hasDecimal = false; // ensure number contains only 1 decimal
			do {
				if (lastChar == '.') hasDecimal = true;
				numString += lastChar;
				lastChar = (char) buffer.read();
			} while (Character.isDigit(lastChar) || (lastChar == '.' && !hasDecimal));
			
			token = new Token(TokenType.NUMBER);
			token.setNumVal(Double.parseDouble(numString));
			return token;
		}
		
		// comment (ignore as token)
		if (lastChar == '#') {
			// comment until end of line
			do
				lastChar = (char) buffer.read();
			while (lastChar != EOF && lastChar != '\n' && lastChar != '\r');
			
			// grab next token
			if (lastChar != EOF)
				return getToken();
		}
		
		// check for end of file
		if (lastChar == EOF)
			return new Token(TokenType.EOF);
		
		// unknown character
		token = new Token(TokenType.UNKNOWN);
		token.setUnknownChar(lastChar);
		lastChar = (char) buffer.read();
		return token;
	}
}
