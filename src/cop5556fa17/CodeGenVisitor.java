package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	
	int SLOT_x = 1;
	int SLOT_y = 2;
	int SLOT_X = 3;
	int SLOT_Y = 4;
	int SLOT_r = 5;
	int SLOT_a = 6;
	int SLOT_R = 7;
	int SLOT_A = 8;
	int SLOT_DEF_X  = 9;
	int SLOT_DEF_Y  = 10;
	int SLOT_Z  = 11;
	

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
//		CodeGenUtils_old.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
//		CodeGenUtils_old.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		
		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, SLOT_x);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, SLOT_y);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, SLOT_X);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, SLOT_Y);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, SLOT_r);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, SLOT_a);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, SLOT_R);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, SLOT_A);
		
		mv.visitLocalVariable("DEF_X", "I", null, mainStart, mainEnd, SLOT_DEF_X);
		mv.visitLocalVariable("DEF_Y", "I", null, mainStart, mainEnd, SLOT_DEF_Y);
		mv.visitLocalVariable("Z", "I", null, mainStart, mainEnd, SLOT_Z);
				
		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO 
		
		String fieldName = declaration_Variable.name;
		String fieldType = getType(declaration_Variable.getTypeName());
		Object initval = fieldType=="I"? new Integer(0) : new Boolean(false);
		cw.visitField(ACC_STATIC, fieldName, fieldType, null, initval);
		
		if(declaration_Variable.e != null){
		declaration_Variable.e.visit(this, arg);		
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		Kind op = expression_Binary.op;
		
		switch(op){
		
		case OP_PLUS:
			mv.visitInsn(IADD);
			break;
			
		case OP_MINUS:
			mv.visitInsn(ISUB);
			break;
			
		case OP_DIV:
			mv.visitInsn(IDIV);
			break;
			
		case OP_TIMES:
			mv.visitInsn(IMUL);
			break;
			
		case OP_MOD:
			mv.visitInsn(IREM);
			break;
			
		case OP_OR:
			mv.visitInsn(IOR);
			 break;
			
		case OP_AND:
			mv.visitInsn(IAND);
			break;
			
		default:
			Label l1 = new Label();
			Label l2 = new Label();
			Label end = new Label();
			
			if(op == Kind.OP_EQ){
			mv.visitJumpInsn(IF_ICMPNE, l2);
			}
			else if(op == Kind.OP_NEQ){
			mv.visitJumpInsn(IF_ICMPEQ, l2);	
			}
			else if(op == Kind.OP_LT){
				mv.visitJumpInsn(IF_ICMPGE, l2);	
			}
			else if(op == Kind.OP_GT){
				mv.visitJumpInsn(IF_ICMPLE, l2);	
			}
			else if(op == Kind.OP_LE){
				mv.visitJumpInsn(IF_ICMPGT, l2);	
			}
			else if(op == Kind.OP_GE){
				mv.visitJumpInsn(IF_ICMPLT, l2);	
			}
			mv.visitLabel(l1);
			mv.visitLdcInsn(true);
			mv.visitJumpInsn(GOTO, end);
			mv.visitLabel(l2);
			mv.visitLdcInsn(false);
			mv.visitLabel(end);
			break;		
		}	
//		CodeGenUtils_old.genLogTOS(GRADE, mv, expression_Binary.getTypeName());
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
		Kind op = expression_Unary.op;	
		expression_Unary.e.visit(this, arg);
		
		switch(op){
		
		case OP_MINUS:
			mv.visitInsn(INEG);
			break;
			
		case OP_EXCL:
			if(expression_Unary.getTypeName() == Type.INTEGER){
				mv.visitLdcInsn(Integer.MAX_VALUE);
				mv.visitInsn(IXOR);	
			}
			else if(expression_Unary.getTypeName() == Type.BOOLEAN)
			{
				Label start = new Label();
				Label end = new Label();
				mv.visitJumpInsn(IFEQ, start);
				mv.visitLdcInsn(new Integer(0));			
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(start);
				mv.visitLdcInsn(new Integer(1));
				mv.visitLabel(end);
			}
			break;
		
		default:
			break;
	}
//		CodeGenUtils_old.genLogTOS(GRADE, mv, expression_Unary.getTypeName());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(!index.isCartesian()){
			mv.visitInsn(DUP2);
			// stack have r, a , r, a
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);
			// stack have r, a ,x
			
			mv.visitInsn(DUP_X2);
			// stack have x, r, a, x
			
			mv.visitInsn(POP);
			// stack have x, r, a
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);
			// stack have x, y
		}
		return null;
	}

	/*
	 * Generate code to load the image reference on the stack. Visit the index to generate code to
	leave Cartesian location of index on the stack. Then invoke ImageSupport.getPixel which
	generates code to leave the value of the pixel on the stack.
	 * */
	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		String imageRefName = expression_PixelSelector.name;
		
		mv.visitFieldInsn(GETSTATIC, className, imageRefName, "Ljava/awt/image/BufferedImage;");
		
		expression_PixelSelector.index.visit(this, arg);
		
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getPixel", ImageSupport.getPixelSig, false);
		
		return null;
	}
	

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		expression_Conditional.condition.visit(this, arg);
		
		Label start = new Label();
		Label end = new Label();
		mv.visitJumpInsn(IFEQ, start);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, end);
		
		mv.visitLabel(start);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitLabel(end);
		
//		CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.getTypeName());
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		String fieldName = declaration_Image.name;
		String fieldType = "Ljava/awt/image/BufferedImage;";
		cw.visitField(ACC_STATIC, fieldName, fieldType , null, null);
		
		if(declaration_Image.source != null){
			declaration_Image.source.visit(this, arg);
		
			if(declaration_Image.xSize != null){
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
			}else{
				mv.visitLdcInsn(ACONST_NULL);
			}
			if(declaration_Image.ySize != null){
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
			}else{
				mv.visitLdcInsn(ACONST_NULL);
			}
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);		
			
		}else{
			
			if(declaration_Image.xSize != null){
				declaration_Image.xSize.visit(this, arg);
			}else{
				mv.visitLdcInsn(new Integer(256));
			}
			if(declaration_Image.ySize != null){
				declaration_Image.ySize.visit(this, arg);
			}else{
				mv.visitLdcInsn(new Integer(256));
			}
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "makeImage", ImageSupport.makeImageSig, false);
		}
		
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		
		return null;
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO 
		mv.visitVarInsn(ALOAD,0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		mv.visitLdcInsn(source_Ident.name);
		return null;
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		String fieldName = declaration_SourceSink.name;
		String fieldType = "Ljava/lang/String;";
		cw.visitField(ACC_STATIC, fieldName, fieldType, null, null); 
		
		if(declaration_SourceSink.source != null){
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		// TODO 
		mv.visitLdcInsn(expression_IntLit.value);		
//		CodeGenUtils_old.genLogTOS(GRADE, mv, Type.INTEGER);
		return expression_IntLit;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function == (Kind.KW_abs))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "abs", RuntimeFunctions.absSig, false);		
		}
		else if(expression_FunctionAppWithExprArg.function == (Kind.KW_log))
		{
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "log", RuntimeFunctions.logSig, false);	
		}
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6

		if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_x)
		{
			expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
			expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);	
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_y)
		{
			expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
			expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);	
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_r)
		{
			expression_FunctionAppWithIndexArg.arg.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);	
		}
		else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_a)
		{
			expression_FunctionAppWithIndexArg.arg.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);	
		}
		return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		if(expression_PredefinedName.kind == Kind.KW_x){
			mv.visitVarInsn(ILOAD, SLOT_x);
		}else if(expression_PredefinedName.kind == Kind.KW_y){
			mv.visitVarInsn(ILOAD, SLOT_y);
		}else if(expression_PredefinedName.kind == Kind.KW_X){
			mv.visitVarInsn(ILOAD, SLOT_X);
		}else if(expression_PredefinedName.kind == Kind.KW_Y){
			mv.visitVarInsn(ILOAD, SLOT_Y);
		}else if(expression_PredefinedName.kind == Kind.KW_r)
		{
			mv.visitVarInsn(ILOAD, SLOT_x);
			mv.visitVarInsn(ILOAD, SLOT_y);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);
		}
		else if(expression_PredefinedName.kind == Kind.KW_a)
		{
			mv.visitVarInsn(ILOAD, SLOT_x);
			mv.visitVarInsn(ILOAD, SLOT_y);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		else if(expression_PredefinedName.kind == Kind.KW_R)
		{
			mv.visitVarInsn(ILOAD, SLOT_R);
		}
		else if(expression_PredefinedName.kind == Kind.KW_A){
			mv.visitVarInsn(ILOAD, SLOT_A);
		}else if(expression_PredefinedName.kind == Kind.KW_DEF_X){
			mv.visitLdcInsn(new Integer(256));
		}else if(expression_PredefinedName.kind == Kind.KW_DEF_Y){
			mv.visitLdcInsn(new Integer(256));
		}else if(expression_PredefinedName.kind == Kind.KW_Z){
			mv.visitLdcInsn(new Integer(16777215));
		}
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases
		String feildName = statement_Out.name;
		String feildDesc = getType(statement_Out.getDec().getTypeName());

		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		
		mv.visitFieldInsn(GETSTATIC, className, feildName, feildDesc);
		
		CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().getTypeName());
		
		if(feildDesc.equals("I")){
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		}else if(feildDesc.equals("Z"))
		{
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
		}
		else if(feildDesc.equals("Ljava/awt/image/BufferedImage;"))
		{
			mv.visitFieldInsn(GETSTATIC, className, feildName, feildDesc);
			statement_Out.sink.visit(this, arg);
		}
				
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		String fieldName = statement_In.name;
		String fieldType = getType(statement_In.getDec().getTypeName());
		statement_In.source.visit(this, arg);

		if(fieldType.equals("I"))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
		}
		else if(fieldType.equals("Z"))
		{
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
		}
		else if(fieldType.equals("Ljava/awt/image/BufferedImage;"))
		{
			Declaration_Image dec_img = (Declaration_Image)statement_In.getDec();
			
			if(dec_img.xSize != null){
				dec_img.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

				dec_img.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				
			}else{
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}

			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "readImage", ImageSupport.readImageSig, false);		
		}
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		return null;
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String fieldName = statement_Assign.lhs.name;
		String fieldType = getType(statement_Assign.lhs.getTypeName());		
		
		if(fieldType != "Ljava/awt/image/BufferedImage;")
		{
		statement_Assign.e.visit(this, arg);
		statement_Assign.lhs.visit(this, arg);
		}
		else{
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			mv.visitInsn(DUP);
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getX", ImageSupport.getXSig, false);			
			mv.visitVarInsn(ISTORE, SLOT_X);

			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "getY", ImageSupport.getYSig, false);			
			mv.visitVarInsn(ISTORE, SLOT_Y);
			
			//*******************
			
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, SLOT_x);
			// loads 0 value in x
			
			Label l3 = new Label();
			mv.visitLabel(l3);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			Label l5 = new Label();
			mv.visitLabel(l5);
			
			
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, SLOT_y);
			// loads 0 value in y
			
			Label l6 = new Label();
			mv.visitLabel(l6);
			Label l7 = new Label();
			mv.visitJumpInsn(GOTO, l7);
			Label l8 = new Label();
			mv.visitLabel(l8);
			
			// Implementation
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			
			
			Label l9 = new Label();
			mv.visitLabel(l9);
			mv.visitIincInsn(SLOT_y, 1);
			mv.visitLabel(l7);
			mv.visitVarInsn(ILOAD, SLOT_y); //y
			mv.visitVarInsn(ILOAD, SLOT_Y); //Y
			mv.visitJumpInsn(IF_ICMPLT, l8);

			Label l10 = new Label();
			mv.visitLabel(l10);
			mv.visitIincInsn(SLOT_x, 1);
			mv.visitLabel(l4);
			mv.visitVarInsn(ILOAD, SLOT_x);		// x
			mv.visitVarInsn(ILOAD, SLOT_X);		//X
			mv.visitJumpInsn(IF_ICMPLT, l5);


		}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		String fieldName = lhs.name;
		String fieldType = getType(lhs.getTypeName());		
		if(lhs.getTypeName() != Type.IMAGE){
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}else{
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			// on stack - pixel, ref, x, y
			
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "setPixel", ImageSupport.setPixelSig, false);				
		}
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "makeFrame", ImageSupport.makeFrameSig, false);				
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		String fieldName = sink_Ident.name;
//		String fieldType = getType(sink_Ident.getTypeName());
//		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		
		mv.visitLdcInsn(fieldName);
		
		mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/ImageSupport", "write", ImageSupport.writeSig, false);				

		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO
		mv.visitLdcInsn(expression_BooleanLit.value);		
//		CodeGenUtils_old.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		String fieldName = expression_Ident.name;
		String fieldDesc = getType(expression_Ident.getTypeName());	
		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldDesc);;
//		CodeGenUtils_old.genLogTOS(GRADE, mv, expression_Ident.getTypeName());
		return null;
	}


	public static String getType(Type t){
		if(t == (Type.INTEGER)){
			return "I";
		}else if(t == (Type.BOOLEAN)){
			return "Z";
		}else if(t == (Type.IMAGE)){
			return "Ljava/awt/image/BufferedImage;";
		}
		/*else if(t == (Type.URL)){
			return "Ljava/awt/image/BufferedImage;";
		}
		else if(t == (Type.FILE)){
			return "Ljava/awt/image/BufferedImage;";
		}
		else if(t == (Type.SCREEN)){
			return "Ljava/awt/image/BufferedImage;";
		}*/
		else {
			return "Ljava/lang/String;";
		}
	}
}