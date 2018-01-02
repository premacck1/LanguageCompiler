package cop5556fa17.AST;

import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Source extends ASTNode{

	Type typeName;

	public Source(Token firstToken) {
		super(firstToken);
	}
	public Type getTypeName() {
		return typeName;
	}

	public void setTypeName(Type typeName) {
			this.typeName = typeName;
		}
	}
	
