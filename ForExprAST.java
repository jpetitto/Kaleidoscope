
public class ForExprAST implements ExprAST {
	private String varName;
	ExprAST start, end, step, body;
	
	public ForExprAST(String varName, ExprAST start, ExprAST end, ExprAST step, ExprAST body) {
		this.varName = varName;
		this.start = start;
		this.end = end;
		this.step = step;
		this.body = body;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public ExprAST getStart() {
		return start;
	}
	
	public ExprAST getEnd() {
		return end;
	}
	
	public ExprAST getStep() {
		return step;
	}
	
	public ExprAST getBody() {
		return body;
	}
	
	public String toString() {
		return "var = " + varName + ", start = " + start + ", end = " + end + ", step = " + step + ", body = " + body;
	}
}
