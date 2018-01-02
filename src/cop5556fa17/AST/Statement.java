package cop5556fa17.AST;

import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Statement extends ASTNode {
	boolean isCartesian;
	Type typeName;
	
	public boolean isCartesian() {
		return isCartesian;
	}
	public void setCartesian(boolean isCartesian) {
		this.isCartesian = isCartesian;
	}
	public Statement(Token firstToken) {
		super(firstToken);
	}

	public Type getTypeName() {
		return typeName;
	}

	public void setTypeName(Type typeName) {
			this.typeName = typeName;
	}

}
