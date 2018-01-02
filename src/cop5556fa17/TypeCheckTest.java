package cop5556fa17;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;

import static cop5556fa17.Scanner.Kind.*;

public class TypeCheckTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	
	/**
	 * Scans, parses, and type checks given input String.
	 * 
	 * Catches, prints, and then rethrows any exceptions that occur.
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			ASTNode ast = new Parser(scanner).parse();
			show(ast);
			ASTVisitor v = new TypeCheckVisitor();
			ast.visit(v, null);
		} catch (Exception e) {
			show(e);
			throw e;
		}
	}

	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSmallest() throws Exception {
		String input = "n"; //Smallest legal program, only has a name
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner); // Create a parser
		ASTNode ast = parser.parse(); // Parse the program
		TypeCheckVisitor v = new TypeCheckVisitor();
		String name = (String) ast.visit(v, null);
		show("AST for program " + name);
		show(ast);
	}



	
	/**
	 * This test should pass with a fully implemented assignment
	 * @throws Exception
	 */
	 @Test
	 public void testDec1() throws Exception {
	 String input = "prog int k = 42;";
	 typeCheck(input);
	 }
	 
	 /**
	  * This program does not declare k. The TypeCheckVisitor should
	  * throw a SemanticException in a fully implemented assignment.
	  * @throws Exception
	  */
	 @Test
	 public void testUndec() throws Exception {
	 String input = "prog k = 42;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
		public void testDec5() throws LexicalException, SyntaxException, Exception {
			String input = "prog int k = kanika;";
			show(input);
			thrown.expect(SemanticException.class);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			}
		
		@Test
		public void testDec6() throws LexicalException, SyntaxException, Exception {
			String input = "prog int k = k[2,3];";
			show(input);
			thrown.expect(SemanticException.class);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			}
		
		@Test
		public void testDec7() throws LexicalException, SyntaxException, Exception {
			String input = "prog int k = !2;";
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			assertEquals(ast.name, "prog"); 
			//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
			Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
					.get(0);  
			assertEquals(KW_int, dec.type.kind);
			assertEquals("k", dec.name);
			assertEquals("Expression_Unary [op=OP_EXCL, e=Expression_IntLit [value=2]]",dec.e.toString());
		}

		@Test
		public void testDec8() throws LexicalException, SyntaxException, Exception {
			String input = "prog int k = R;";
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			assertEquals(ast.name, "prog"); 
			//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
			Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
					.get(0);  
			assertEquals(KW_int, dec.type.kind);
			assertEquals("k", dec.name);
			assertEquals("Expression_PredefinedName [name=KW_R]",dec.e.toString());
		}
		
		@Test
		public void testDec9() throws LexicalException, SyntaxException, Exception {
			//String input = "prog int k = 90;";
			//String input = "prog int k = false;";
			String input = "prog int k = (2);";
			//String input = "prog int k = atan(k[2,3]);";
			//String input = "prog int k = cart_y[7162712,7832982];";
			//String input = "prog int k = R;";
		
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			assertEquals(ast.name, "prog"); 
			//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
			Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
					.get(0);  
			assertEquals(KW_int, dec.type.kind);
			assertEquals("k", dec.name);
			
		}
		
		@Test
		public void testDec10() throws LexicalException, SyntaxException, Exception {
			
		//	String input = "prog int k = 2|2&2;";
			String input = "prog int k = true?2:3;";
			
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			assertEquals(ast.name, "prog"); 
			//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
			Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);  
			assertEquals(KW_int, dec.type.kind);
			assertEquals("k", dec.name);
			
		}
		
		@Test
		public void testDec12() throws LexicalException, SyntaxException, Exception {
			
		//	String input = "prog int k = 2|2&2;";
			String input = "kanika int kani; \n kani -> SCREEN;";
			
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			
		}
		
	/*	@Test
		public void testDec14() throws LexicalException, SyntaxException, Exception {
			
			String input = "prog int k = k+1;";
			
			show(input);
			Scanner scanner = new Scanner(input).scan(); 
			show(scanner); 
			Parser parser = new Parser(scanner);
			Program ast = parser.parse();
			TypeCheckVisitor v = new TypeCheckVisitor();
			String name = (String) ast.visit(v, null);
			show("AST for program " + name);
			show(ast);
			assertEquals(ast.name, "prog"); 
		}*/
		
		@Test
		public void test1() throws Exception {
		 String input = "prog int k = 42; int k=12;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test2() throws Exception {
		 String input = "prog int k = 42;\n boolean k=true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test3() throws Exception {
		 String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test4() throws Exception {
		 String input = "prog boolean abcd=(true&true|false&1);"; 
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test5() throws Exception {
		 String input = "prog image x;"; 
		 thrown.expect(SyntaxException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test6() throws Exception {
		 String input = "prog image [10,11] abcd <- \"\";"; 
		 //thrown.expect(SyntaxException.class);
		 typeCheck(input);
		 }
		 
		 
		 @Test
		 public void test7() throws Exception {
		 String input = "prog url imageurl1=\"https://www.google.com\"; url imageurl=imageurl1;"; 
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test8() throws Exception {
		 String input = "prog url imageurl=@(1234+3454);";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test9() throws Exception {
		 String input = "prog image imageName;image imageName1 <- \"https://www.google.com\";"+ 
				        "imageName -> SCREEN;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 
		 @Test
		 public void test10() throws Exception {
		 String input = "prog int value1=10; int value2 =20; int sinValue=abs(sin(value1)); int cosValue=abs(cos(value2));";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test11() throws Exception {
		 String input = "prog boolean k = 3 != 4;";
		 typeCheck(input);
		 }

		 @Test
		 public void test112() throws Exception {
		 String input = "prog boolean k = 3 == 4;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test113() throws Exception {
		 String input = "prog boolean k = 3 != 4;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void test114() throws Exception {
		 String input = "prog boolean k = 5 == 6 ? 1 < 2 : 1 > 3;";
		 typeCheck(input);
		 }

@Test
public void test01() throws Exception {
String input = "prog int k = 42; int k=12;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test02() throws Exception {
String input = "prog int k = 42;\n boolean k=true;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test03() throws Exception {
String input = "prog file k = 42;\n boolean k=true;";
thrown.expect(SyntaxException.class);
typeCheck(input);
}

@Test
public void test04() throws Exception {
String input = "prog file k = 42;\n boolean k=true;";
thrown.expect(SyntaxException.class);
typeCheck(input);
}

@Test
public void test05() throws Exception {
String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test06() throws Exception {
String input = "prog image[filepng,png] imageName; \n boolean ab=true;"; 
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test07() throws Exception {
String input = "prog int abcd=(true&true);"; 
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test08() throws Exception {
String input = "prog boolean abcd=(true&true|false&1);"; 
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test09() throws Exception {
String input = "prog image x;"; 
thrown.expect(SyntaxException.class);
typeCheck(input);
}

@Test
public void test100() throws Exception {
String input = "prog image [10,11] abcd <- \"\";"; 
//thrown.expect(SyntaxException.class);
typeCheck(input);
}

@Test
public void test011() throws Exception {
String input = "prog image [10,11] abcd <- @(1234+234);"; 
typeCheck(input);
}

 @Test
public void test12() throws Exception {
String input = "prog int x_y=12; image [10,11] abcd <- x_y;"; 
typeCheck(input);
}

// SourceSinkDeclaration
@Test
public void test13() throws Exception {
String input = "prog url imageurl=\"https://www.google.com\";"; 
typeCheck(input);
}

@Test
public void test14() throws Exception {
String input = "prog url imageurl1=\"https://www.google.com\"; url imageurl=imageurl1;"; 
typeCheck(input);
}

@Test
public void test15() throws Exception {
String input = "prog url imageurl=@(1234+3454);";
thrown.expect(SemanticException.class);
typeCheck(input);
}

//Statements for Image Out and Image in
@Test
public void test16() throws Exception {
String input = "prog image imageName;image imageName1 <- \"https://www.google.com\";"+ 
		        "imageName -> SCREEN;";
//thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test17() throws Exception {
String input = "prog file fileName=\"filepng\"; image image1<- fileName;";
typeCheck(input);
}

//LHS Assignment Statement
@Test
public void test18() throws Exception {
String input = "prog image imageName; int array; array[[x,y]]=imageName[5,6];";
typeCheck(input);
}

@Test
public void test19() throws Exception {
String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test20() throws Exception {
String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test21() throws Exception {
String input = "prog image imageName;int array;array[[r,a]]=imageName[5,6];";
typeCheck(input);
}

@Test
public void test22() throws Exception {
String input = "prog int value1=10; int value2 =20; int sinValue=abs(sin(value1)); int cosValue=abs(cos(value2));";
typeCheck(input);
}

@Test
public void test23() throws Exception {
String input = "prog int value1=10; int value2 =20; int sinValue; int cosValue; sinValue=abs(sin(atan(cos(value1))));"
		        + " cosValue=cart_x[value1*10,value2*20];";
typeCheck(input);
}

@Test
public void test24() throws Exception {
String input = "prog int x_value=10; int y_value =20; int sinValue; int cosValue; "
			    + " sinValue=polar_a[x_value*1/2+12+13,y_value*1/3*1/2*3/4%2];"
		        + " cosValue=cart_y[x_value/2*2+2,(x_value>y_value)?sinValue/234:sinValue*10-23];"
		        + " int cotValue=polar_r[sinValue/cosValue,sinValue*100/400+cosValue];";
typeCheck(input);
}

@Test
public void test25() throws Exception {
String input = "";
thrown.expect(SyntaxException.class);
typeCheck(input);
}

@Test
public void test26() throws Exception {
	 String input = "prog boolean k = false;\n k=k;";
	 typeCheck(input);
}

@Test
public void test27() throws Exception {
String input = "prog int k=(5*5+12+3-5+4+false);";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void test28() throws Exception {
String input = "prog int k=((5+6/0+1/2+2%3));";
typeCheck(input);
}

@Test
public void test29() throws Exception {
String input =  "prog int k = k + 1 ;";
thrown.expect(SemanticException.class);
typeCheck(input);
}
@Test
public void testDec2() throws Exception {
String input = "prog boolean k = true;";
typeCheck(input);
}

@Test
public void testDec3() throws Exception {
String input = "prog int v = 6; image[4, 6] nish;";
typeCheck(input);
}

@Test
public void testDec4() throws Exception {
String input = "prog int v = 6; int b = 3; int c = 7; image[c + b, 6] nish;";
typeCheck(input);
}

@Test
public void testDec05() throws Exception {
String input = "prog int v = 6; int b = 3;  image[c + b, 6] nish;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void testDec06() throws Exception {
String input = "prog int v = 6; int b = 3;  image[b, 6] nish <- @3;";	 
typeCheck(input);
}

@Test
public void testDec44() throws Exception {
String input = "prog int v = 6; int b = 3;  image[b, false] nish <- @3;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void testDec007() throws Exception {
String input = "prog int v = 6; int b = 3; int n = 4; image[b, 6] nish <- @5*6-7/2%9+2+n;";
typeCheck(input);
}

@Test
public void testDec008() throws Exception {
String input = "prog int v = 6; int b = 3; int n = 4; image[b, 6] nish <- \"nishant\";";
typeCheck(input);
}

@Test
public void testDec009() throws Exception {
String input = "prog int v = 5 > 3 ? 3 + 5 : true;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void testDec0010() throws Exception {
String input = "prog int v = 5 > 3 ? 3 + 5 : 7;";	 
typeCheck(input);
}

@Test
public void testDec11() throws Exception {
String input = "prog boolean v = 5 > 3 ? 3 + 5 : 7;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void testDec102() throws Exception {
String input = "prog boolean v = 5 > 3 ? true : false;";
typeCheck(input);
}

@Test
public void testDec13() throws Exception {
String input = "prog url abc = \"http://www.google.com\";";
typeCheck(input);
}

@Test
public void testDec14() throws Exception {
String input = "prog file abc = \"book.pdf\";";
typeCheck(input);
}	 

@Test
public void testDec16() throws Exception {
String input = "prog file def = \"book.pdf\"; file abc = def;";
typeCheck(input);
}

@Test
public void testDec17() throws Exception {
String input = "prog int def = 4; file abc = def;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

@Test
public void testDec18() throws Exception {
String input = "prog int k = false;";
thrown.expect(SemanticException.class);
typeCheck(input);
}

//////////////////Statement/////////////////////////////

// StatementAssignment
@Test
public void testDec19() throws Exception {
String input = "prog abc[[x,y]] = 5;";
thrown.expect(SemanticException.class);
typeCheck(input);
}	 	 

@Test
public void testDec20() throws Exception {
String input = "prog boolean abc = true; abc[[x,y]] = 5;";
thrown.expect(SemanticException.class);
typeCheck(input);
}	 	 

@Test
public void testDec21() throws Exception {
String input = "prog int abc = 7; abc[[x,y]] = 5;";	 
typeCheck(input);
}	 	

@Test
public void testDec22() throws Exception {
String input = "prog boolean abc = true; abc[[x,y]] = false;";	
typeCheck(input);
}	 	

/// Statement_IN ///

@Test
public void testDec23() throws Exception {
String input = "prog boolean def = true; def <- @45;";
thrown.expect(SemanticException.class);
typeCheck(input);
}	 	

@Test
public void testDec24() throws Exception {
String input = "prog int def = 6; def <- @45;";	
typeCheck(input);
}	 	

@Test
public void testDec25() throws Exception {
String input = "prog int def = 6; def <- @true;";
thrown.expect(SemanticException.class);
typeCheck(input);
}	 	

//// statement Out ////////////////

@Test
public void testDec26() throws Exception {
String input = "prog int def = 5; def -> SCREEN;";	
typeCheck(input);
}	 	

@Test
public void testDec27() throws Exception {
String input = "prog boolean def = true; def -> SCREEN;";	
typeCheck(input);
}	 	

@Test
public void testDec28() throws Exception {
String input = "prog image[2,3]def; def -> SCREEN;";	
typeCheck(input);
}

@Test
public void testDec29() throws Exception {
String input = "prog int k = k+1;";	
thrown.expect(SemanticException.class);
typeCheck(input);
}
}
