package cop5556fa17;



import java.util.*;
import javax.swing.plaf.synth.SynthSpinnerUI;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionApp;
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
import cop5556fa17.AST.Statement;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	
	public void consume(){
		t = scanner.nextToken();
	}
	
	void match(Kind kind) throws SyntaxException{
		if(t.isKind(kind)){
			consume();
		}else{
			throw new SyntaxException(t, "Exception in Match method");
		}
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		Token firstToken = t;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<>();
		match(IDENTIFIER);
		while(t.isKind(KW_int) || t.isKind(KW_boolean) || t.isKind(KW_image) || t.isKind(KW_url) || t.isKind(KW_file) || t.isKind(IDENTIFIER)){
			
			if(t.isKind(KW_int) || t.isKind(KW_boolean) || t.isKind(KW_image) || t.isKind(KW_url) || t.isKind(KW_file)){
				decsAndStatements.add(declaration());
				match(SEMI);
			}
			else if(t.isKind(IDENTIFIER)){
				decsAndStatements.add(statement());
				match(SEMI);
			}
		}
		return new Program(firstToken, firstToken, decsAndStatements); // give AST Node return statement
	}

	Index selector() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = expression();
		match(COMMA);
		Expression e1 = expression();
		return new Index(firstToken, e0, e1);
	}
	
	Index raSelector() throws SyntaxException{
		Token firstToken = t;
		Expression e0 =  new Expression_PredefinedName(firstToken, Kind.KW_r);
		match(KW_r);
		match(COMMA);
		Expression e1 =  new Expression_PredefinedName(firstToken, Kind.KW_a);
		match(KW_a);
		return new Index(firstToken, e0, e1);
	}

	Index xySelector() throws SyntaxException{
		Token firstToken = t;
		Expression e0 =  new Expression_PredefinedName(firstToken, Kind.KW_x);
		match(KW_x);
		match(COMMA);
		Expression e1 =  new Expression_PredefinedName(firstToken, Kind.KW_y);
		match(KW_y);
		return new Index(firstToken, e0, e1);
	}
	
	Index lhsSelector () throws SyntaxException{
		Index i = null;
		match(Kind.LSQUARE);
		if(t.isKind(KW_x)){
			i = xySelector();
		}
		else if(t.isKind(Kind.KW_r)){
			i = raSelector();
		}else{
			throw new SyntaxException(t, "Exception in LhsSelector");
		}
		match(Kind.RSQUARE);
		return i;
	}
	
	Kind functionName() throws SyntaxException{
		Token firstToken = t;
		if(t.isKind(Kind.KW_sin) || t.isKind(Kind.KW_cos) || t.isKind(Kind.KW_atan) || t.isKind(Kind.KW_abs) || t.isKind(Kind.KW_cart_x) || t.isKind(Kind.KW_cart_y) || t.isKind(Kind.KW_polar_a) || t.isKind(Kind.KW_polar_r)){
			consume();
		}else{
			throw new SyntaxException(t, "Exception in finction Name");
		}
		return firstToken.kind;
	}
	
	
	Expression_FunctionApp FunctionApplication() throws SyntaxException{
		Token firstToken = t;
		Expression_FunctionApp fApp = null;
		Kind function = functionName();
		if(t.isKind(LPAREN)){
			consume();
			Expression e0 = expression();
			match(RPAREN);
			fApp = new Expression_FunctionAppWithExprArg(firstToken, function, e0);
		}
		else if(t.isKind(LSQUARE)){
			consume();
			
			Index i = selector();
			match(RSQUARE);
			fApp = new Expression_FunctionAppWithIndexArg(firstToken, function, i);
		}else{
			throw new SyntaxException(t, "Exception in Function Application");
		}
		return fApp;
	}
	
	LHS lhs(Token ident) throws SyntaxException{
		Token firstToken = ident;
		Index i = null;
//		match(IDENTIFIER);
		if(t.isKind(LSQUARE)){
			consume();
			i = lhsSelector();
			match(RSQUARE);
		}else{
//			return;
		}
		return new LHS(firstToken, firstToken, i);
	}
	
	Expression IdentOrPixelSelectorExpression() throws SyntaxException{
		Token firstToken = t;
		match(IDENTIFIER);
		Expression e = new Expression_Ident(firstToken, firstToken);
		if(t.isKind(LSQUARE)){
			consume();
			Index i = selector();
			match(RSQUARE);
			e = new Expression_PixelSelector(firstToken, firstToken,i);
		}else{

		}
		return e;
	}
	
	Expression primary() throws SyntaxException{
		Token firstToken = t;
		Expression e = null;
		if(t.isKind(INTEGER_LITERAL)) {
			e = new Expression_IntLit(firstToken, t.intVal());
			consume();
		}
		else if(t.isKind(LPAREN)){
			consume();
			e = expression();
			match(RPAREN);
		}
		else if(t.isKind(Kind.KW_sin) || t.isKind(Kind.KW_cos) || t.isKind(Kind.KW_atan) || t.isKind(Kind.KW_abs) || t.isKind(Kind.KW_cart_x) || t.isKind(Kind.KW_cart_y) || t.isKind(Kind.KW_polar_a) || t.isKind(Kind.KW_polar_r)){
			e = FunctionApplication();
		}
		else if(t.isKind(BOOLEAN_LITERAL)){
			e = new Expression_BooleanLit(firstToken, Boolean.parseBoolean(t.getText()));
			consume();
		}
		else{
			throw new SyntaxException(t, "Exception in Primary");
		}
		return e;
	}
	
	Expression unaryExpressionNotPlusMinus() throws SyntaxException{
		Token firstToken = t;
		Expression e = null;
		
		if(t.isKind(OP_EXCL)){
			consume();
			Expression e0 = unaryExpression();
			e = new Expression_Unary(firstToken, firstToken, e0);
		}
		else if(t.isKind(INTEGER_LITERAL) || t.isKind(LPAREN) || t.isKind(Kind.KW_sin) || t.isKind(Kind.KW_cos) || t.isKind(Kind.KW_atan) || t.isKind(Kind.KW_abs) || t.isKind(Kind.KW_cart_x) || t.isKind(Kind.KW_cart_y) || t.isKind(Kind.KW_polar_a) || t.isKind(Kind.KW_polar_r) || t.isKind(BOOLEAN_LITERAL)){
			e = primary();
		}
		else if(t.isKind(Kind.IDENTIFIER)){
			e = IdentOrPixelSelectorExpression();
		}
		else if(t.isKind(KW_x) || t.isKind(KW_y) || t.isKind(Kind.KW_r) || t.isKind(Kind.KW_a) || t.isKind(Kind.KW_X) || t.isKind(Kind.KW_Y) || t.isKind(Kind.KW_Z) || t.isKind(Kind.KW_A) || t.isKind(Kind.KW_R) || t.isKind(Kind.KW_DEF_X) || t.isKind(KW_DEF_Y)){
			e = new Expression_PredefinedName(firstToken, t.kind);
			consume();
		}
		else{
			throw new SyntaxException(t, "Exception in Unary Expression Not Plus Minus");
		}
		return e;
	}
	
	Expression unaryExpression() throws SyntaxException{
		Token firstToken = t;
		Token op = null;
		Expression e = null;
		if(t.isKind(OP_PLUS)){
			op = t;
			consume();
			e = unaryExpression();
		}
		else if(t.isKind(OP_MINUS)){
			op = t;
			consume();
			e = unaryExpression();
		}
		else if(t.isKind(OP_EXCL) || t.isKind(INTEGER_LITERAL) || t.isKind(LPAREN) || t.isKind(Kind.KW_sin) || t.isKind(Kind.KW_cos) || t.isKind(Kind.KW_atan) || t.isKind(Kind.KW_abs) || t.isKind(Kind.KW_cart_x) || t.isKind(Kind.KW_cart_y) || t.isKind(Kind.KW_polar_a) || t.isKind(Kind.KW_polar_r) || t.isKind(BOOLEAN_LITERAL) || t.isKind(Kind.IDENTIFIER) || t.isKind(KW_x) || t.isKind(KW_y) || t.isKind(Kind.KW_r) || t.isKind(Kind.KW_a) || t.isKind(Kind.KW_X) || t.isKind(Kind.KW_Y) || t.isKind(Kind.KW_Z) || t.isKind(Kind.KW_A) || t.isKind(Kind.KW_R) || t.isKind(Kind.KW_DEF_X) || t.isKind(KW_DEF_Y)){
			return unaryExpressionNotPlusMinus();
		}
		else{
			throw new SyntaxException(t, "Exception in Unary Expression");
		}
		return new Expression_Unary(firstToken, op, e);
	}
	
	Expression multExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = unaryExpression();
		Expression e1 = null;
		while(t.isKind(OP_TIMES) || t.isKind(OP_DIV) || t.isKind(OP_MOD))
		{
				Token op = t;
				consume();
				e1 = unaryExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression addExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = multExpression();
		while(t.isKind(OP_PLUS) || t.isKind(OP_MINUS))
		{
				Token op = t;
				consume();
				e1 = multExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression relExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = addExpression();
		while(t.isKind(OP_LT) || t.isKind(OP_GT) || t.isKind(OP_LE) || t.isKind(OP_GE))
		{
				Token op = t;
				consume();
				e1 = addExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression eqExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = relExpression();
		while(t.isKind(OP_EQ) || t.isKind(OP_NEQ))
		{
				Token op = t;
				consume();
				e1 = relExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression andExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = eqExpression();
		while(t.isKind(OP_AND))
		{
				Token op = t;
				consume();
				e1 = eqExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	Expression orExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = andExpression();
		while(t.isKind(OP_OR))
		{
				Token op = t;
				consume();
				e1 = andExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
	
	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		Expression e = null;
		Token firstToken = t;
		Expression trueExpression = null;
		Expression falseExpression = null;
		Expression condition = orExpression();
		if(t.isKind(OP_Q)){
			consume();
			trueExpression = expression();
			match(OP_COLON);
			falseExpression = expression();
			return new Expression_Conditional(firstToken, condition, trueExpression, falseExpression);
		}else{
//			return;
			e = condition;
		}
		return e;
	}
	
	
	Statement_Assign assignmentStatement(Token ident) throws SyntaxException{
		Token firstToken = ident;
		LHS lhs= lhs(ident);
		match(OP_ASSIGN);
		Expression e = expression();
		return new Statement_Assign(firstToken, lhs, e);
	}
	
	Statement_In ImageInStatement(Token ident) throws SyntaxException{
		Token firstToken = ident;
//		match(IDENTIFIER);
		match(OP_LARROW);
		Source source = source();
		return new Statement_In(firstToken, firstToken, source);
	}
	
	Sink sink() throws SyntaxException{
		Token firstToken = t;
		Sink s = null;
		if(t.isKind(IDENTIFIER)){
			s = new Sink_Ident(firstToken, firstToken);
			consume();
		}else if(t.isKind(KW_SCREEN)){
			s = new Sink_SCREEN(firstToken);
			consume();
		}else{
			throw new SyntaxException(t, "Exception in sink");
		}
		return s;
	}
	
	Statement_Out ImageOutStatement(Token ident) throws SyntaxException{
		Token firstToken = ident;
//		match(IDENTIFIER);
		match(OP_RARROW);
		Sink sink = sink();
		return new Statement_Out(firstToken, firstToken, sink);
	}
	
	Statement statement() throws SyntaxException{
		Token firstToken = t;
		LHS lhs = null;
		Token ident = t;
		match(IDENTIFIER);
		
		if(t.isKind(OP_RARROW)){ // Image out		
			return ImageOutStatement(ident);
		}
		else if(t.isKind(OP_LARROW)){ // Image in 
			return ImageInStatement(ident);
		}
		else if(t.isKind(LSQUARE)){
			/*consume();
			Index i = lhsSelector();
			match(RSQUARE);
			lhs = new LHS(firstToken, firstToken, i);
			
			match(OP_ASSIGN);
			Expression e = expression();*/
//			
			return assignmentStatement(ident);
		} 
		else if(t.isKind(Kind.OP_ASSIGN)) {
			lhs = lhs(ident);
			match(OP_ASSIGN);
			Expression e = expression();
			return new Statement_Assign(firstToken, lhs, e);
		}
		else{
			throw new SyntaxException(t, "Exception in Statement");
		}
	}

	Declaration imageDeclaration() throws SyntaxException{
		Token firstToken = t;
		Source source = null;
		Expression xSize = null;
		Expression ySize = null;
		match(KW_image);
		if(t.isKind(LSQUARE)){
			consume();
			xSize = expression();
			match(COMMA);
			ySize = expression();
			match(RSQUARE);
		}else{
			
		}
		Token name = t; // taking identifier name
		match(IDENTIFIER);
		if(t.isKind(OP_LARROW)){
			consume();
			source = source();
		}else{
			
		}
		return new Declaration_Image(firstToken, xSize, ySize, name, source);
	}
	
	Token sourceSinkType() throws SyntaxException{
		Token type = null;
		
		if(t.isKind(KW_url) || t.isKind(KW_file)){
			type = t;
			consume();
		}else{
			throw new SyntaxException(t, "Exception in source sink type");
		}
		return type;
	}
	
	Source source() throws SyntaxException{
		Token firstToken = t;
		
		if(t.isKind(STRING_LITERAL)){
			consume();
			return new Source_StringLiteral(firstToken, firstToken.getText());
		}
		else if(t.isKind(OP_AT)){
			consume();
			Expression paramNum = expression();
			return new Source_CommandLineParam(firstToken, paramNum);
		}
		else if(t.isKind(IDENTIFIER)){
			consume();
			return new Source_Ident(firstToken, firstToken);
		}
		else{
			throw new SyntaxException(t, "Exception in source");
		}
	}
	
	Declaration sourceSinkDeclaration() throws SyntaxException{
		Token firstToken = t;
		Token type = sourceSinkType();
		Token name = t;
		match(IDENTIFIER);
		match(OP_ASSIGN);
		Source source = source();
		return new Declaration_SourceSink(firstToken, type, name, source);
	}
	
	Token varType() throws SyntaxException{
		Token type = null;
		if(t.isKind(KW_int) || t.isKind(KW_boolean)){
			consume();
			type = t;
			return type;
		}
		else{
			throw new SyntaxException(t, "Exception in var types");
		}
	}
	
	Declaration variableDeclaration() throws SyntaxException{
		Token firstToken = t;
		varType();
		
		Expression e = null;
		Token name = t;

		match(IDENTIFIER);
		if(t.isKind(OP_ASSIGN)){
			consume();
			e = expression();
		}else{
			
		}
		return new Declaration_Variable(firstToken, firstToken, name, e);
				
	}
	
	Declaration declaration() throws SyntaxException{
		if(t.isKind(KW_int) || t.isKind(KW_boolean)){
			return variableDeclaration();
		}
		else if(t.isKind(KW_image)){
			return imageDeclaration();
		}
		else if(t.isKind(KW_url) || t.isKind(KW_file)){
			return sourceSinkDeclaration();
		}
		else{
			throw new SyntaxException(t, "Exception in declaration");
		}
	}
	
	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
