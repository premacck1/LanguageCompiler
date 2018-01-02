package cop5556fa17;
import java.util.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}



	@Test
	public void unaryExpressionNotPlusMinus1() throws SyntaxException, LexicalException {
		List<String> names = Arrays.asList("x", "y", "r", "a", "X", "Y", "Z", "A", "R", "DEF_X", "DEF_Y");
		for(String name : names){
			show(name);
			Scanner scanner = new Scanner(name).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.unaryExpressionNotPlusMinus(); // Call expression directly.
		}		
	}
	
	@Test
	public void unaryExpressionNotPlusMinus2() throws SyntaxException, LexicalException {
		String input = "jksd [2,2]";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpressionNotPlusMinus(); // Call expression directly.
	}
	
	@Test
	public void unaryExpressionNotPlusMinus3() throws SyntaxException, LexicalException {
		List<String> names = Arrays.asList("true", "false", "2", "3", "(skad)", "sin(x)", "!R", "!+A", "!- jksd [2,2]");
		for(String name : names){
			show(name);
			Scanner scanner = new Scanner(name).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.unaryExpressionNotPlusMinus(); // Call expression directly.
		}		
	}
	
	@Test
	public void unaryExpressionNotPlusMinus4() throws SyntaxException, LexicalException {
		String input = "!x";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.unaryExpressionNotPlusMinus(); // Call expression directly.
	}
	
	@Test
	public void primary1() throws SyntaxException, LexicalException {
		List<String> names = Arrays.asList("true", "false", "2", "3", "(skad)", "sin(x)");
		for(String name : names){
			show(name);
			Scanner scanner = new Scanner(name).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.primary(); // Call expression directly.
		}		
	}
	
	@Test
	public void expression234() throws SyntaxException, LexicalException {
		String input =  "x*X + y*Y < x == R & x*X + y*Y < x == R | x*X + y*Y < x == R & x*X + y*Y < x == R";
		//String input = "+Z*+true++Z*+true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression(); // Call expression directly.
	}
	
	@Test
	public void expression3we() throws SyntaxException, LexicalException {
		String input =  "polar_a(s + f)";
		//String input = "+Z*+true++Z*+true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression(); // Call expression directly.
	}
	
	@Test
	public void expression43() throws SyntaxException, LexicalException {
		String input =  "polar_a(s + f) ? (a+b) : (c+ d)";
		//String input = "+Z*+true++Z*+true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression(); // Call expression directly.
	}
	
	@Test
	public void selector() throws SyntaxException, LexicalException {
		String input =  "cart_y(d),(s + d)";
		//String input = "+Z*+true++Z*+true";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.selector(); // Call expression directly.
	}
	
	@Test
	public void testExpressionMultandAnd() throws LexicalException, SyntaxException {
		String input = "length + breadth*width & 4";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	
	@Test
	public void testExpressionIfThenElse() throws LexicalException, SyntaxException {
		String input = "(height == 0) ? 0 : (base * height)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	
	@Test
	public void testParseIfThenElse() throws LexicalException, SyntaxException {
		String input = "program area = (height == 0) ? 0 : (base * height);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	
	@Test
	public void testExpressionErrorinParens() throws LexicalException, SyntaxException {
		String input = "(a+(b+(c+d))";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		thrown.expect(SyntaxException.class);
		Parser parser = new Parser(scanner);
		try {
			parser.expression();
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	
	@Test
	public void testParsewithAssignmentStatement1() throws LexicalException, SyntaxException {
		String input = "prog flag [[x,y]] = (5+4*3);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testParsewithAssignmentStatement2() throws LexicalException, SyntaxException {
		String input = "prog flag [[r,A]] = (5+4*3);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	@Test
	public void expression2() throws SyntaxException, LexicalException {
		String input = ";;\n;;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void expression3() throws SyntaxException, LexicalException {
		String input = "z@file";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void expression4() throws SyntaxException, LexicalException {
		String input = "abc int def;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.parse();  //Parse the program	
	}
	@Test
	public void expression5() throws SyntaxException, LexicalException {
		String input = "abc";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.parse();  //Parse the program	
	}
	@Test
	public void expression6() throws SyntaxException, LexicalException {
		String input = "a==b";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void expression7() throws SyntaxException, LexicalException {
		String input = "ABC\r\nabc";
		show(input);
		Scanner scanner = new Scanner(input).scan();   
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}	
	}
	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "++++x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	@Test
	public void testDec4() throws LexicalException, SyntaxException {
		String input = "+x=x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	@Test
	public void testDec5() throws LexicalException, SyntaxException {
		String input = "+++x = +(+(+x)) = x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	@Test
	public void testDec6() throws LexicalException, SyntaxException {
		String input = "boolean val=false;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   
		Parser parser = new Parser(scanner);  
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void testDec7() throws LexicalException, SyntaxException {
		String input = "myprog boolean val=false";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	@Test
	public void testDec8() throws LexicalException, SyntaxException {
		String input = "a=a-+b";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.expression();
	}
	@Test
	public void sourceSinkTypeFai3() throws SyntaxException, LexicalException {
		//String input = "URL";
		String input = "(a*b,c*d";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkType();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void sourceSinkTypeFai4() throws SyntaxException, LexicalException {
		//String input = "URL";
		String input = "(a*b),c*d";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.sourceSinkType();
		} catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	@Test
	public void sourceSinkTypeFail1() throws SyntaxException, LexicalException {
		String input = "b = b-+c";
		/*String input = "URL";
		String input = "FILE";*/
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	@Test
	public void sourceSinkTypeFail2() throws SyntaxException, LexicalException {
		String input = " a*b,c*d";
		/*String input = "URL";
		String input = "FILE";*/
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.selector();
	}
	@Test
	public void sourceSinkTypeFail4() throws SyntaxException, LexicalException {
		String input = " a + b / c";
		/*String input = "URL";
		String input = "FILE";*/
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}


	    @Test
	    public void testRaSelector() throws SyntaxException, LexicalException {
	        String input = "r , A";
	        show(input);
	        Scanner scanner = new Scanner(input).scan();
	        show(scanner);
	        Parser parser = new Parser(scanner);
	        parser.raSelector();
	    }

	    @Test
	    public void xySelector() throws SyntaxException, LexicalException {
	        String input = "x,y";
	        show(input);
	        Scanner scanner = new Scanner(input).scan();
	        show(scanner);
	        Parser parser = new Parser(scanner);
	        parser.xySelector();
	    }


	    @Test
	    public void lhsSelector() throws SyntaxException, LexicalException {
	        String input = "[x,y]";
	        show(input);
	        Scanner scanner = new Scanner(input).scan();
	        show(scanner);
	        Parser parser = new Parser(scanner);
	        parser.lhsSelector();
	    }
	    @Test
	    public void functionName() throws SyntaxException, LexicalException {
	        String input = "atan";
	        show(input);
	        Scanner scanner = new Scanner(input).scan();
	        show(scanner);
	        Parser parser = new Parser(scanner);
	        parser.functionName();
	    }
	    
		@Test
		public void functionNameFail() throws SyntaxException, LexicalException {
			String input = "polar_b";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			thrown.expect(SyntaxException.class);
			try {
				parser.FunctionApplication(); // Parse the program
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}

		@Test
		public void functionApplication() throws SyntaxException, LexicalException {
			String input = "sin(2)";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.FunctionApplication();
		}
		
		@Test
		public void functionApplication2() throws SyntaxException, LexicalException {
			String input = "sin(sin(true))";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.FunctionApplication();
		}
		
		@Test
		public void functionApplicationFail() throws SyntaxException, LexicalException {
			String input = "sin(sin)";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			thrown.expect(SyntaxException.class);
			try {
				parser.FunctionApplication(); // Parse the program
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}

		@Test
		public void varType() throws SyntaxException, LexicalException {
			String input = "int";
			/*String input = "int";
			String input = "boolean";*/
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.varType();
		}
		

		@Test
		public void varTypeFail() throws SyntaxException, LexicalException {
			//String input = "bool";
			//String input = "true";
			//String input = "false";
			String input= "bool";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			thrown.expect(SyntaxException.class);
			try {
				parser.varType();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}
		
		

		@Test
		public void sourceSinkType() throws SyntaxException, LexicalException {
			String input = "url";
			/*String input = "url";
			String input = "file";*/
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			parser.sourceSinkType();
		}
		

		@Test
		public void sourceSinkTypeFail() throws SyntaxException, LexicalException {
			//String input = "URL";
			String input = "FILE";
			show(input);
			Scanner scanner = new Scanner(input).scan();
			show(scanner);
			Parser parser = new Parser(scanner);
			thrown.expect(SyntaxException.class);
			try {
				parser.sourceSinkType();
			} catch (SyntaxException e) {
				show(e);
				throw e;
			}
		}
		
		@Test
		public void testDec22() throws LexicalException, SyntaxException {
			String input = "abc int ab;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();
		}
		
		@Test
		public void testDec23() throws LexicalException, SyntaxException {
			String input = "xa int gh;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec21() throws LexicalException, SyntaxException {
			String input = "xa image [1,2] fgh <- \"yo hey\";";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec20() throws LexicalException, SyntaxException {
			String input = "xa url bhu = @true;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec19() throws LexicalException, SyntaxException {
			String input = "xa bhu[[x,y]]=(4);";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec18() throws LexicalException, SyntaxException {
			String input = "xa boolean bgh = yo; int yt=R?1:2;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec17() throws LexicalException, SyntaxException {
			String input = "xa boolean bgh = yo; int yt==R?1:2;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			thrown.expect(SyntaxException.class);
			try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}  	
		}
		
		@Test
		public void testDec9() throws LexicalException, SyntaxException {
			String input = "xa boolean bgh = yo; int yt=R?1:2;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec10() throws LexicalException, SyntaxException {
			String input = "poy int yop=true;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec11() throws LexicalException, SyntaxException {
			String input = "poy [[x,y]]=int;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			thrown.expect(SyntaxException.class);
			try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}  	
		}
		@Test
		public void testDec12() throws LexicalException, SyntaxException {
			String input = "poy oi <- @A|abg[true,false] ;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec13() throws LexicalException, SyntaxException {
			String input = "p url bhu=\"bhu\";";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		
		@Test
		public void testDec14() throws LexicalException, SyntaxException {
			String input = "zs yu ->SCREEN->yu;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			thrown.expect(SyntaxException.class);
			try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}  	
		}
		
		@Test
		public void testDec15() throws LexicalException, SyntaxException {
			String input = "ki image [true,r]not<-@re ;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
		

		/**
		 * This example invokes the method for expression directly. 
		 * Effectively, we are viewing Expression as the start
		 * symbol of a sub-language.
		 *  
		 * Although a compiler will always call the parse() method,
		 * invoking others is useful to support incremental development.  
		 * We will only invoke expression directly, but 
		 * following this example with others is recommended.  
		 * 
		 * @throws SyntaxException
		 * @throws LexicalException
		 */
		
		@Test
		public void expression() throws SyntaxException, LexicalException {
			String input = "0 ? true : raktima";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression13() throws SyntaxException, LexicalException {
			String input = "sin (true)";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression14() throws SyntaxException, LexicalException {
			String input = "sin (aj[1,2])";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression15() throws SyntaxException, LexicalException {
			String input = "A<a";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression16() throws SyntaxException, LexicalException {
			String input = "A|abg[true,false]";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		

		@Test
		public void expression17() throws SyntaxException, LexicalException {
			String input = "+!r*+Z";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.  
		}
		
		@Test
		public void expression18() throws SyntaxException, LexicalException {
			String input = "DEF_Y??";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			thrown.expect(SyntaxException.class);
			try {
			parser.parse();  //Parse the program
			}
			catch (SyntaxException e) {
				show(e);
				throw e;
			}  
		}
		
		@Test
		public void expression19() throws SyntaxException, LexicalException {
			String input = "(cos(abs[1,2]))";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.
		}
		
		@Test
		public void expression20() throws SyntaxException, LexicalException {
			String input = "2345678&567&789";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.
		}
		
		@Test
		public void expression21() throws SyntaxException, LexicalException {
			String input = "true?(x):(R)";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.
		}
		
		@Test
		public void expression22() throws SyntaxException, LexicalException {
			String input = "yu[m,n]";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.
		}

		@Test
		public void expression23() throws SyntaxException, LexicalException {
			String input = "sin(a+b)";
			show(input);
			Scanner scanner = new Scanner(input).scan();  
			show(scanner);   
			Parser parser = new Parser(scanner);  
			parser.expression();  //Call expression directly.
		}
		
		@Test
		public void failedOne() throws LexicalException, SyntaxException {
			String input = "p image1 = Z;";
			show(input);
			Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
			show(scanner);   //Display the Scanner
			Parser parser = new Parser(scanner);  //
			parser.parse();  //Parse the program	
		}
	
	}

