package src;

public class Token {
	private TokenType type;
	private String identStr;
	private double numVal;
	private char unknownChar;

	public Token(TokenType type) {
		this.type = type;
	}

	public void setIdentStr(String identStr) {
		if (type == TokenType.IDENTFIFIER)
			this.identStr = identStr;
	}

	public void setNumVal(double numVal) {
		if (type == TokenType.NUMBER)
			this.numVal = numVal;
	}
	
	public void setUnknownChar(char unknownChar) {
		if (type == TokenType.UNKNOWN)
			this.unknownChar = unknownChar;
	}
	
	public TokenType getType() {
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
		if (type == TokenType.IDENTFIFIER)
			return type + ": " + identStr;
		if (type == TokenType.NUMBER)
			return type + ": " + numVal;
		if (type == TokenType.UNKNOWN)
			return type + ": " + unknownChar;

		return type.toString();
	}
}
