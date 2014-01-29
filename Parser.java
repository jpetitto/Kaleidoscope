import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parser {
	private Lexer lexer;
	private Token currToken;
	private Map<Character, Integer> binopPrecedence;
	
	public Parser(String fileName) throws FileNotFoundException {
		lexer = new Lexer(new File(fileName));
		
		binopPrecedence = new HashMap<Character, Integer>();
		binopPrecedence.put('<', 10);
		binopPrecedence.put('+', 20);
		binopPrecedence.put('-', 20);
		binopPrecedence.put('*', 40);
	}
	
	public Token getCurrToken() {
		return currToken;
	}
	
	public int getTokenPrecedence() {
		char binop = currToken.getUnknownChar();
		Integer tokenPrec = binopPrecedence.get(binop);
		
		// binop may not have declared precedence
		if (tokenPrec == null)
			return -1;
		
		return tokenPrec.intValue();
	}
	
	public Token getNextToken() {
		// temporary try/catch
		try {
			currToken = lexer.getToken();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currToken;
	}
	
	public ExprAST errorExpr(String message) {
		System.err.println("Error: " + message);
		return null;
	}
	
	public PrototypeAST errorPrototype(String message) {
		errorExpr(message);
		return null;
	}
	
	public FunctionAST errorFunction(String message) {
		errorExpr(message);
		return null;
	}
	
	public ExprAST parseNumberExpr() {
		ExprAST result = new NumberExprAST(currToken.getNumVal());
		getNextToken();
		return result;
	}
	
	public ExprAST parseParenExpr() {
		getNextToken(); // eat '('
		
		ExprAST subExpr = parseExpression();
		if (subExpr == null)
			return null;
		
		if (currToken.getUnknownChar() != ')')
			return errorExpr("expected ')'");
		
		getNextToken();
		return subExpr;
	}
	
	public ExprAST parseIdentifierExpr() {
		String idName = currToken.getIdentStr();
		
		getNextToken();
		
		if (currToken.getUnknownChar() != '(')
			return new VariableExprAST(idName);
		
		getNextToken();
		List<ExprAST> args = new ArrayList<ExprAST>();
		while (currToken.getUnknownChar() != ')') {
			ExprAST arg = parseExpression();
			if (arg == null)
				return null;
			args.add(arg);
			
			if (currToken.getUnknownChar() == ')')
				break;
			
			if (currToken.getUnknownChar() != ',')
				return errorExpr("expected ')' or ',' in argument list");
			
			getNextToken();
		}
		
		getNextToken();
		
		return new CallExprAST(idName, args);
	}
	
	public ExprAST parsePrimary() {
		if (currToken.getType() == TokenClass.IDENTFIFIER)
			return parseIdentifierExpr();
		if (currToken.getType() == TokenClass.NUMBER)
			return parseNumberExpr();
		if (currToken.getUnknownChar() == '(')
			return parseParenExpr();
		if (currToken.getType() == TokenClass.IF)
			return parseIfExpr();
		
		return errorExpr("unknown token when expecting an expression");
	}

	private ExprAST parseExpression() {
		ExprAST lhs = parsePrimary();
		if (lhs == null)
			return null;
		
		return parseBinopRHS(0, lhs);
	}
	
	public ExprAST parseBinopRHS(int exprPrec, ExprAST lhs) {
		while (true) {
			int tokenPrec = getTokenPrecedence();
			if (tokenPrec < exprPrec)
				return lhs;
			
			char binop = currToken.getUnknownChar();
			getNextToken();
			
			ExprAST rhs = parsePrimary();
			if (rhs == null)
				return null;
			
			int nextPrec = getTokenPrecedence();
			if (tokenPrec < nextPrec) {
				rhs = parseBinopRHS(tokenPrec + 1, rhs);
				if (rhs == null)
					return null;
			}
			
			lhs = new BinaryExprAST(binop, lhs, rhs);
		}
	}
	
	public PrototypeAST parsePrototype() {
		if (currToken.getType() != TokenClass.IDENTFIFIER)
			return errorPrototype("expected function name in prototype");
		
		String funcName = currToken.getIdentStr();
		getNextToken();
		
		if (currToken.getUnknownChar() != '(')
			return errorPrototype("expected '(' in prototype");
		
		List<String> argNames = new ArrayList<String>();
		getNextToken();
		while (currToken.getType() == TokenClass.IDENTFIFIER) {
			argNames.add(currToken.getIdentStr());
			getNextToken();
		}
		
		if (currToken.getUnknownChar() != ')')
			return errorPrototype("expected ')' in prototype");
		
		getNextToken();
		
		return new PrototypeAST(funcName, argNames);
	}
	
	public FunctionAST parseDefinition() {
		getNextToken();
		
		PrototypeAST prototype = parsePrototype();
		if (prototype == null)
			return null;
		
		ExprAST body = parseExpression();
		if (body == null)
			return null;
		
		return new FunctionAST(prototype, body);
	}
	
	public PrototypeAST parseExtern() {
		getNextToken();
		return parsePrototype();
	}
	
	public FunctionAST parseTopLevelExpr() {
		ExprAST expr = parseExpression();
		if (expr == null)
			return null;
		
		// anonymous prototype for function
		PrototypeAST prototype = new PrototypeAST("", new ArrayList<String>());
		return new FunctionAST(prototype, expr);
	}
	
	public ExprAST parseIfExpr() {
		getNextToken(); // eat 'if'
		
		// condition
		ExprAST condExpr = parseExpression();
		if (condExpr == null)
			return null;
		
		if (currToken.getType() != TokenClass.THEN)
			return errorExpr("expected 'then'");
		getNextToken(); // eat 'then'
		
		ExprAST thenExpr = parseExpression();
		if (thenExpr == null)
			return null;
		
		if (currToken.getType() != TokenClass.ELSE)
			return errorExpr("expected 'else'");
		getNextToken(); // eat 'else'
		
		ExprAST elseExpr = parseExpression();
		if (elseExpr == null)
			return null;
		
		return new IfExprAST(condExpr, thenExpr, elseExpr);
	}
	
	/*
	public static void main(String[] args) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		map.put('<', 10);
		map.put('+', 20);
		map.put('-', 20);
		map.put('*', 40);
		System.out.println(map.get('<'));
		System.out.println(map.get('+'));
		System.out.println(map.get('-'));
		System.out.println(map.get('*'));
		System.out.println(map.get('>'));
		Integer val = map.get('>');
		System.out.println(val);
		Integer val2 = map.get(null);
		System.out.println(val2);
		int val3 = val2.intValue();
		System.out.println(val3);
	}
	*/
}
