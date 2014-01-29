
public class Token {
	private TokenClass type;
	private String identStr;
	private double numVal;
	private char unknownChar;

	public Token(TokenClass type) {
		this.type = type;
	}

	public void setIdentStr(String identStr) {
		if (type == TokenClass.IDENTFIFIER)
			this.identStr = identStr;
	}

	public void setNumVal(double numVal) {
		if (type == TokenClass.NUMBER)
			this.numVal = numVal;
	}
	
	public void setUnknownChar(char unknownChar) {
		if (type == TokenClass.UNKNOWN)
			this.unknownChar = unknownChar;
	}
	
	public TokenClass getType() {
		return type;
	}
	
	public String getIdentStr() {
		return identStr;
	}
	
	public double getNumVal() {
		return numVal;
	}
	
	public char getUnknownChar() {
		return unknownChar;
	}

	public String toString() {
		if (type == TokenClass.IDENTFIFIER)
			return type + ": " + identStr;
		if (type == TokenClass.NUMBER)
			return type + ": " + numVal;
		if (type == TokenClass.UNKNOWN)
			return type + ": " + unknownChar;

		return type.toString();
	}
	
	/*
	public static void main(String[] args) {
		Token token = new Token(TokenClass.DEF);
		token.setIdentStr("foobar");
		token.setNumVal(69);
		token.setUnknownChar('+');
		System.out.println(token);
		
		token = new Token(TokenClass.IDENTFIFIER);
		token.setIdentStr("foobar");
		token.setNumVal(69);
		token.setUnknownChar('+');
		System.out.println(token);
		
		token = new Token(TokenClass.NUMBER);
		token.setIdentStr("foobar");
		token.setNumVal(69);
		token.setUnknownChar('+');
		System.out.println(token);
		
		token = new Token(TokenClass.UNKNOWN);
		token.setIdentStr("foobar");
		token.setNumVal(69);
		token.setUnknownChar('+');
		System.out.println(token);
	}
	*/
}
