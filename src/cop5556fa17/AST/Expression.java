package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils;
import cop5556fa17.TypeUtils.Type;

public abstract class Expression extends ASTNode {
	
	public Type typeName;
	
	public Expression(Token firstToken) {
		super(firstToken);
	}

	public Type getTypeName() {
		return typeName;
	}

	public void setTypeName(Type typeName) {
			this.typeName = typeName;
	}

	
	
}
