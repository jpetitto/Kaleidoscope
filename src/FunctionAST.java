package src;

public class FunctionAST {
	private PrototypeAST prototype;
	private ExprAST body;
	
	public FunctionAST(PrototypeAST prototype, ExprAST body) {
		this.prototype = prototype;
		this.body = body;
	}
	
	public PrototypeAST getPrototype() {
		return prototype;
	}
	
	public ExprAST getBody() {
		return body;
	}
	
	public String toString() {
		return "prototype = " + prototype + ", body = " + body;
	}
}
