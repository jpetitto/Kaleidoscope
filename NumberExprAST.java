
public class NumberExprAST implements ExprAST {
	private double value;
	
	public NumberExprAST(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
}
