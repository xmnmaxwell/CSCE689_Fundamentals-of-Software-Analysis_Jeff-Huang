import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.BufferedTokenStream;
import java.util.*;

/* PART 3 */

public class ModifyIfListner extends JavaBaseListener {
	TokenStreamRewriter rewriter;
    static List<String> variables;

    public ModifyIfListner(TokenStream tokens) {
        rewriter = new TokenStreamRewriter(tokens);
        variables = new ArrayList<>();

    }

    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
    /*
    Filter statements starting with if
    */
    if(ctx.getStart().getText().equals("if")) {
        /*
        Select only variables without ! and length >3
        */
        String child = ctx.getChild(1).getText().replace("(", "").replace(")", "");

        if(variables.contains(child) && child.length() > 3) {

            String field = "  { \n\t\t\t\tSystem.out.println(\""+child+" "+ctx.getStart().getLine()+"\");\n\t\t\t\t\b";
            int ix = ctx.start.getTokenIndex();
            while(!(rewriter.getTokenStream().get(ix).getText().equals(")"))) {
                ix++;
            }
            rewriter.insertAfter(rewriter.getTokenStream().get(ix), field);
            rewriter.insertAfter(ctx.getStop(), "\n\t\t\t}");
            }

        }
    }   
    


    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        variables.add(ctx.variableDeclaratorId().getText());
    }

}
