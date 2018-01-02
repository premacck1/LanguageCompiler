package cop5556fa17;

import java.util.HashMap;
import java.util.Map;
import cop5556fa17.AST.Declaration;

public class SymbolTable {
	Map<String, Declaration> symbolTable = new HashMap<>();
	
	public Declaration lookupType(String name)
	{
		return symbolTable.get(name);
	}
	public void insert(String name, Declaration d){
		symbolTable.put(name, d);
	}
}
