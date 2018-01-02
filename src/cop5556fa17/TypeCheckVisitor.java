package cop5556fa17;

import java.util.*;
import java.net.URL;
import cop5556fa17.TypeUtils;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	
		SymbolTable symbolTable = new SymbolTable();
		TypeUtils tu = new TypeUtils();
		
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		if(symbolTable.lookupType(declaration_Variable.name) == null){
			declaration_Variable.setTypeName(TypeUtils.getType(declaration_Variable.firstToken));
			if(declaration_Variable.e != null){
				Expression exp = (Expression) declaration_Variable.e.visit(this, arg);
				if(declaration_Variable.getTypeName() != exp.getTypeName()){
					throw new SemanticException(declaration_Variable.firstToken, "Exception in visit declaration variable");
				}
			}
			symbolTable.insert(declaration_Variable.name, declaration_Variable);
			return declaration_Variable;
		}
		else{
			throw new SemanticException(declaration_Variable.firstToken, "Exception in visit declaration variable");
		}
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		Expression e0 = (Expression) expression_Binary.e0.visit(this, arg);
		Expression e1 = (Expression) expression_Binary.e1.visit(this, arg);
		Kind op = expression_Binary.op;
		
			if(op == Kind.OP_EQ || op == Kind.OP_NEQ) expression_Binary.setTypeName(Type.BOOLEAN);
			else if((op == Kind.OP_GE || op == Kind.OP_GT || op == Kind.OP_LT || op == Kind.OP_LE) && e0.getTypeName()==Type.INTEGER){
				expression_Binary.setTypeName(Type.BOOLEAN);
			}
			else if((op == Kind.OP_AND || op == Kind.OP_OR) && (e0.getTypeName() == Type.INTEGER || e0.getTypeName() == Type.BOOLEAN)){
				expression_Binary.setTypeName(e0.getTypeName());
			}else if((op == Kind.OP_DIV || op == Kind.OP_MINUS || op == Kind.OP_MOD || op == Kind.OP_PLUS || op == Kind.OP_POWER || op == Kind.OP_TIMES) && e0.getTypeName() == Type.INTEGER ){
				expression_Binary.setTypeName(Type.INTEGER);
			}else{
				expression_Binary.setTypeName(null);
			}
			if(e0.getTypeName() == e1.getTypeName() && expression_Binary.getTypeName() != null){
				return expression_Binary;
			}else{
				throw new SemanticException(expression_Binary.firstToken, "Exception in visit expression binary");	
			}
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		Expression e = (Expression) expression_Unary.e.visit(this, arg);
		Kind op = expression_Unary.op;
		Type t = e.getTypeName();
		
		if(op == Kind.OP_EXCL && (t == Type.BOOLEAN || t == Type.INTEGER)) expression_Unary.setTypeName(t);
		else if((op == Kind.OP_PLUS || op == Kind.OP_MINUS) && t == Type.INTEGER) expression_Unary.setTypeName(Type.INTEGER);
		else expression_Unary.setTypeName(null);
		
		if(expression_Unary.getTypeName() == null){
			throw new SemanticException(expression_Unary.firstToken, "Exception in visit expression unary");	
		}
		return expression_Unary;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		Expression e0 = (Expression) index.e0.visit(this, arg);
		Expression e1 = (Expression) index.e1.visit(this, arg);
		if(e0.getTypeName() == Type.INTEGER && e1.getTypeName() == Type.INTEGER){
			index.setCartesian(!(e0.firstToken.kind == Kind.KW_r && e1.firstToken.kind == Kind.KW_a));
			
//			boolean e0Bool = e0 instanceof Expression_PredefinedName? ((Expression_PredefinedName)e0).kind == Kind.KW_r : false;
//			boolean e1Bool = e1 instanceof Expression_PredefinedName? ((Expression_PredefinedName)e1).kind == Kind.KW_a : false;
//			if(e0Bool && e1Bool){
//				index.setCartesian(!(e0Bool && e1Bool));
//			}
//			
			return index;
		}
		else{
			throw new SemanticException(index.firstToken, "Exception in visit index");	
		}
		
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		Declaration de = symbolTable.lookupType(expression_PixelSelector.name);
		if(de == null) {
			throw new SemanticException(expression_PixelSelector.firstToken, "Exception in visit expression pixel selector");
		}
		Type nameTyp = de.getTypeName();
		Index ind = null;
		
		if(expression_PixelSelector.index != null){
		ind = (Index) expression_PixelSelector.index.visit(this, arg);
		}
		
		if(nameTyp == Type.IMAGE) expression_PixelSelector.setTypeName(Type.INTEGER);
		else if(ind == null) expression_PixelSelector.setTypeName(nameTyp);
		else expression_PixelSelector.setTypeName(null);
		
		if(expression_PixelSelector.getTypeName()== null){
			throw new SemanticException(expression_PixelSelector.firstToken, "Exception in visit expression pixel selector");
		}
		return expression_PixelSelector;
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		Expression c = (Expression) expression_Conditional.condition.visit(this, arg);
		Expression t = (Expression) expression_Conditional.trueExpression.visit(this, arg);
		Expression f = (Expression) expression_Conditional.falseExpression.visit(this, arg);
		if(c.getTypeName() == Type.BOOLEAN && t.getTypeName() == f.getTypeName())
		{
			expression_Conditional.setTypeName(t.getTypeName());
			return expression_Conditional;
		}else{
			throw new SemanticException(expression_Conditional.firstToken, "Exception in visit expression conditional");	
		}
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		if(symbolTable.lookupType(declaration_Image.name)==null)
		{
			declaration_Image.setTypeName(Type.IMAGE);
			if(declaration_Image.xSize != null)
			{	
				if(declaration_Image.ySize == null){
					throw new SemanticException(declaration_Image.firstToken, "Exception in visit declaration image");
				}
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
				
				if(declaration_Image.xSize.getTypeName() != Type.INTEGER || declaration_Image.ySize.getTypeName() != Type.INTEGER){					
					throw new SemanticException(declaration_Image.firstToken, "Exception in visit declaration image");
				}
			}
			if(declaration_Image.source != null){
				declaration_Image.source.visit(this, arg);
			}
			symbolTable.insert(declaration_Image.name, declaration_Image);
				return declaration_Image;
		}else{
			throw new SemanticException(declaration_Image.firstToken, "Exception in visit declaration image");
		}
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		try{
		new URL(source_StringLiteral.fileOrUrl);
		source_StringLiteral.setTypeName(Type.URL);
		}
		catch(Exception e){
		source_StringLiteral.setTypeName(Type.FILE);	
		}
		return source_StringLiteral;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception 
	{
		Expression paranum = (Expression) source_CommandLineParam.paramNum.visit(this, arg);
//		source_CommandLineParam.setTypeName(paranum.getTypeName());
		source_CommandLineParam.setTypeName(null);
		if(paranum.getTypeName() != Type.INTEGER){
			throw new SemanticException(source_CommandLineParam.firstToken, "Exception in visit source command line param");
		}
		return source_CommandLineParam;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		Declaration de = symbolTable.lookupType(source_Ident.name);
		if(de == null) {
			throw new SemanticException(source_Ident.firstToken, "Exception in visit source ident");
		}
		source_Ident.setTypeName(de.getTypeName());
		if(source_Ident.getTypeName() == Type.FILE || source_Ident.getTypeName() == Type.URL){
			return source_Ident;
		}else{
			throw new SemanticException(source_Ident.firstToken, "Exception in visit source ident");
		}
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		if(symbolTable.lookupType(declaration_SourceSink.name)==null)
		{
			Source sc = null;
			if( declaration_SourceSink.source != null){
			sc = (Source) declaration_SourceSink.source.visit(this, arg);
			}
			declaration_SourceSink.setTypeName(TypeUtils.getType(declaration_SourceSink.firstToken));		
			
			if(sc.getTypeName() == null || sc.getTypeName() == declaration_SourceSink.getTypeName()){
				symbolTable.insert(declaration_SourceSink.name, declaration_SourceSink);
				return declaration_SourceSink;
			}else{
				throw new SemanticException(declaration_SourceSink.firstToken, "Exception in visit declaration sourcce sink");	
			}
			
		}else{
			throw new SemanticException(declaration_SourceSink.firstToken, "Exception in visit declaration sourcce sink");
		}
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		try {
			expression_IntLit.setTypeName(Type.INTEGER);
			return expression_IntLit;
			}
			catch (Exception e){
				throw new SemanticException(expression_IntLit.firstToken, "Exception in visit Integer literal");
			}
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		Expression e0 = (Expression) expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(e0.getTypeName() != Type.INTEGER){
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "Exception in visit visitExpression_FunctionAppWithExprArg");
		}
		expression_FunctionAppWithExprArg.setTypeName(Type.INTEGER);
		return expression_FunctionAppWithExprArg;
	}
	
	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception
	{
		expression_FunctionAppWithIndexArg.arg.visit(this, arg);
		expression_FunctionAppWithIndexArg.setTypeName(Type.INTEGER);
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.setTypeName(Type.INTEGER);
		return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		Declaration de = symbolTable.lookupType(statement_Out.name);
		if(de == null){
			throw new SemanticException(statement_Out.firstToken, "Exception in visit Statement Out");	
		}
		statement_Out.setDec(de);
		Sink s = (Sink) statement_Out.sink.visit(this, arg);
		if(symbolTable.lookupType(statement_Out.name) == null){
			throw new SemanticException(statement_Out.firstToken, "Exception in visit Statement Out");
		} 
		else{
			Type t = de.getTypeName();
			Type sinkType = s.getTypeName();
			if(((t == Type.INTEGER || t == Type.BOOLEAN) && sinkType == Type.SCREEN) || (t == Type.IMAGE && (sinkType == Type.FILE || sinkType == Type.SCREEN))){
				return statement_Out;
			}else{
				throw new SemanticException(statement_Out.firstToken, "Exception in visit Statement Out");	
			}
		}
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		Declaration de = symbolTable.lookupType(statement_In.name);
		statement_In.setDec(de);
		Source sc = (Source) statement_In.source.visit(this, arg);
//		Assignment 5 modification
//		if(symbolTable.lookupType(statement_In.name)!=null && de.getTypeName() == sc.getTypeName()){
			return statement_In;
//		}else{
//			throw new SemanticException(statement_In.firstToken, "Exception in visit Statement In");
//		}
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		LHS l = (LHS) statement_Assign.lhs.visit(this, arg);
		Expression e = (Expression) statement_Assign.e.visit(this, arg);
		if(l.getTypeName() == e.getTypeName() || (l.getTypeName()==Type.IMAGE && e.getTypeName() == Type.INTEGER)){
			statement_Assign.setCartesian(l.isCartesian());
			return statement_Assign;
		}else{
			throw new SemanticException(statement_Assign.firstToken, "Exception in visit Statement Assign");
		}
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		Declaration de = symbolTable.lookupType(lhs.name);
		if(de == null){
			throw new SemanticException(lhs.firstToken, "Exception in visit lhs");
		}
		lhs.setDec(de);
		lhs.setTypeName(lhs.dec.getTypeName());
		
		if(lhs.index != null){
			Index ind = (Index) lhs.index.visit(this, arg);
			lhs.setCartesian(ind.isCartesian());
		}
		
		return lhs;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		sink_SCREEN.setTypeName(Type.SCREEN);
		return sink_SCREEN;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		Declaration de = symbolTable.lookupType(sink_Ident.name);
		if(de == null) throw new SemanticException(sink_Ident.firstToken, "Exception in visit Boolean literal");
		
		sink_Ident.setTypeName(de.getTypeName());
		if(sink_Ident.getTypeName() != Type.FILE)
		{
			throw new SemanticException(sink_Ident.firstToken, "Exception in visit Boolean literal");	
		}
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		try {
		expression_BooleanLit.setTypeName(Type.BOOLEAN);
		return expression_BooleanLit;
		}
		catch (Exception e){
			throw new SemanticException(expression_BooleanLit.firstToken, "Exception in visit Boolean literal");
		}
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		Declaration de = null;	
		try {
			
		de = symbolTable.lookupType(expression_Ident.name);
		if(de == null){
			throw new SemanticException(expression_Ident.firstToken, "Exception in visit Expression Ident");
		}
		
		expression_Ident.setTypeName(de.getTypeName());
		return expression_Ident;
		}
		catch(Exception e){
			throw new SemanticException(expression_Ident.firstToken, "Exception in visit Expression Ident");
		}
	}

}
