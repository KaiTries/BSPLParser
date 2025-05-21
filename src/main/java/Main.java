import java.io.IOException;
import java.net.URL;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {

    public static void main(String[] args) throws IOException {
        URL resource = Main.class.getClassLoader().getResource("test.bspl");
        assert resource != null;
        var tokenStream = new CommonTokenStream(new BSPLLexer(new ANTLRFileStream(resource.getPath())));
        BSPLParser parser = new BSPLParser(tokenStream);
        parser.setBuildParseTree(true);

        var tree = parser.document();

        BSPLValidatorListener listener = new BSPLValidatorListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);
    }

}
