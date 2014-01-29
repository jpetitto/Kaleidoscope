
public class IfExprAST implements ExprAST {
	private ExprAST condExpr, thenExpr, elseExpr;
	
	public IfExprAST(ExprAST condExpr, ExprAST thenExpr, ExprAST elseExpr) {
		this.condExpr = condExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}
	
	public ExprAST getCond() {
		return condExpr;
	}
	
	public ExprAST getThen() {
		return thenExpr;
	}
	
	public ExprAST getElse() {
		return elseExpr;
	}
}
