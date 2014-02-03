package src;
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
		
		// initialize binary operator precedence levels
		binopPrecedence = new HashMap<Character, Integer>();
		binopPrecedence.put('<', 10);
		binopPrecedence.put('+', 20);
		binopPrecedence.put('-', 20);
		binopPrecedence.put('*', 40);
	}
	
	// called by Driver class
	public Token getCurrToken() {
		return currToken;
	}
	
	private int getTokenPrecedence() {
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
	
	// report parsing error
	public ExprAST errorExpr(String message) {
		System.err.println("Error: " + message);
		return null;
	}
	
	// return type matches caller
	public PrototypeAST errorPrototype(String message) {
		errorExpr(message);
		return null;
	}
	
	// return type matches caller
	public FunctionAST errorFunction(String message) {
		errorExpr(message);
		return null;
	}
	
	public ExprAST parseNumberExpr() {
		ExprAST result = new NumberExprAST(currToken.getNumVal());
		getNextToken(); // eat number token
		return result;
	}
	
	public ExprAST parseParenExpr() {
		getNextToken(); // eat '('
		
		ExprAST subExpr = parseExpression();
		if (subExpr == null)
			return null;
		
		if (currToken.getUnknownChar() != ')')
			return errorExpr("expected ')'");
		getNextToken(); // eat ')'
		
		return subExpr;
	}
	
	public ExprAST parseIdentifierExpr() {
		String idName = currToken.getIdentStr();
		
		getNextToken(); // eat identifier
		
		// check if def contains args
		if (currToken.getUnknownChar() != '(')
			return new VariableExprAST(idName);
		getNextToken(); // eat '('
		
		// collect and store args
		List<ExprAST> args = new ArrayList<ExprAST>();
		while (currToken.getUnknownChar() != ')') {
			ExprAST arg = parseExpression();
			if (arg == null)
				return null;
			args.add(arg);
			
			if (currToken.getUnknownChar() == ')')
				break;
			
			// each arg must be separated by a ','
			if (currToken.getUnknownChar() != ',')
				return errorExpr("expected ')' or ',' in argument list");
			getNextToken(); // eat ','
		}
		
		getNextToken(); // eat ')'
		
		return new CallExprAST(idName, args);
	}
	
	public ExprAST parsePrimary() {
		if (currToken.getType() == TokenType.IDENTFIFIER)
			return parseIdentifierExpr();
		if (currToken.getType() == TokenType.NUMBER)
			return parseNumberExpr();
		if (currToken.getUnknownChar() == '(')
			return parseParenExpr();
		if (currToken.getType() == TokenType.IF)
			return parseIfExpr();
		if (currToken.getType() == TokenType.FOR)
			return parseForExpr();
		
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
			getNextToken(); // eat binop
			
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
		if (currToken.getType() != TokenType.IDENTFIFIER)
			return errorPrototype("expected function name in prototype");
		
		String funcName = currToken.getIdentStr();
		getNextToken(); // eat function identifier
		
		if (currToken.getUnknownChar() != '(')
			return errorPrototype("expected '(' in prototype");
		getNextToken(); // eat '('
		
		// collect and store arg names
		List<String> argNames = new ArrayList<String>();
		while (currToken.getType() == TokenType.IDENTFIFIER) {
			argNames.add(currToken.getIdentStr());
			getNextToken();
		}
		
		if (currToken.getUnknownChar() != ')')
			return errorPrototype("expected ')' in prototype");
		getNextToken(); // eat ')'
		
		return new PrototypeAST(funcName, argNames);
	}
	
	public FunctionAST parseDefinition() {
		getNextToken(); // eat 'def'
		
		PrototypeAST prototype = parsePrototype();
		if (prototype == null)
			return null;
		
		ExprAST body = parseExpression();
		if (body == null)
			return null;
		
		return new FunctionAST(prototype, body);
	}
	
	public PrototypeAST parseExtern() {
		getNextToken(); // eat 'extern'
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
		
		if (currToken.getType() != TokenType.THEN)
			return errorExpr("expected 'then'");
		getNextToken(); // eat 'then'
		
		ExprAST thenExpr = parseExpression();
		if (thenExpr == null)
			return null;
		
		if (currToken.getType() != TokenType.ELSE)
			return errorExpr("expected 'else'");
		getNextToken(); // eat 'else'
		
		ExprAST elseExpr = parseExpression();
		if (elseExpr == null)
			return null;
		
		return new IfExprAST(condExpr, thenExpr, elseExpr);
	}
	
	public ExprAST parseForExpr() {
		getNextToken(); // eat 'for'
		
		if (currToken.getType() != TokenType.IDENTFIFIER)
			return errorExpr("expected identifier after 'for'");
		
		String varName = currToken.getIdentStr();
		getNextToken(); // eat identifier
		
		if (currToken.getUnknownChar() != '=')
			return errorExpr("expected '=' after " + varName);
		getNextToken(); // eat '='
		
		ExprAST start = parseExpression();
		if (start == null)
			return null;
		
		if (currToken.getUnknownChar() != ',')
			return errorExpr("expected ',' after for start value");
		getNextToken(); // eat ','
		
		ExprAST end = parseExpression();
		if (end == null)
			return null;
		
		// step value is optional
		ExprAST step = null;
		if (currToken.getUnknownChar() == ',') {
			getNextToken(); // eat ','
			step = parseExpression();
			if (step == null)
				return null;
		}
		
		if (currToken.getType() != TokenType.IN)
			return errorExpr("expected 'in' after for");
		getNextToken(); // eat 'in'
		
		ExprAST body = parseExpression();
		if (body == null)
			return null;
		
		return new ForExprAST(varName, start, end, step, body);
	}
}
