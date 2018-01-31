
/* PART 3 */

import java.io.IOException;
import java.util.*;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class JavaParserTest extends JavaBaseListener{

	public static void main(String[] args) throws IOException {
		if(args.length<1)
		{
			System.err.println("java JavaParserTest input-filename\n"
					+"Example: java JavaParserTest Test.java");
			return;
		}
		String inputFile = args[0];
    	CharStream input = new ANTLRFileStream(inputFile);
    	JavaLexer lexer = new JavaLexer(input);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	JavaParser parser = new JavaParser(tokens); //create parser

    	ParseTree tree = parser.compilationUnit();
    	ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
    	JavaParserTest listener = new JavaParserTest(); // create a parse tree listener

    	ModifyIfListner extractor = new ModifyIfListner(tokens);

    	walker.walk(extractor, tree); // traverse parse tree with listener
		System.out.print(extractor.rewriter.getText());

    }
}


	
