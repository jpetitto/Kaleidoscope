
public class BinaryExprAST implements ExprAST {
	private char operator;
	private ExprAST lhs, rhs;
	
	public BinaryExprAST(char operator, ExprAST lhs, ExprAST rhs) {
		this.operator = operator;
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public char getOperator() {
		return operator;
	}
	
	public ExprAST getLHS() {
		return lhs;
	}
	
	public ExprAST getRHS() {
		return rhs;
	}
	
	public String toString() {
		return "op = " + operator + ", lhs = " + lhs + ", rhs = " + rhs;
	}
}
