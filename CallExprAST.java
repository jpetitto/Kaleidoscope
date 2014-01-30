import java.util.List;


public class CallExprAST implements ExprAST {
	private String callee;
	private List<ExprAST> args;
	
	public CallExprAST(String callee, List<ExprAST> args) {
		this.callee = callee;
		this.args = args;
	}
	
	public String getCallee() {
		return callee;
	}
	
	public List<ExprAST> getArgs() {
		return args;
	}
	
	public String toString() {
		return "callee = " + callee + ", args = " + args;
	}
}
