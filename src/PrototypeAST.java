package src;
import java.util.List;

public class PrototypeAST {
	private String name;
	private List<String> args;
	
	public PrototypeAST(String name, List<String> args) {
		this.name = name;
		this.args = args;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getArgs() {
		return args;
	}
	
	public String toString() {
		return "name = " + name + ", args = " + args.toString();
	}
}
