import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Tokenizer.BSPLTokenType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenizer;

public class TestTokenizer {

    @Test
    public void testGetNextToken() {
        // Test the getNextToken method
        final String keyString = "key";
        final String keyStringLeadingComma = ",key";
        final String keyStringTrailingComma = "key,";
        final String keyStringLeadingSpace = " key";
        final String keyStringTrailingSpace = "key ";
        final String keyStringLeadingCommaTrailingComma = ",key,";

        final BSPLTokenizer tokenizer = new BSPLTokenizer(keyString);
        assertEquals("key", tokenizer.getNextToken());

        final BSPLTokenizer tokenizerLeadingComma = new BSPLTokenizer(keyStringLeadingComma);
        assertEquals(",", tokenizerLeadingComma.getNextToken());
        assertEquals("key", tokenizerLeadingComma.getNextToken());

        final BSPLTokenizer tokenizerTrailingComma = new BSPLTokenizer(keyStringTrailingComma);
        assertEquals("key", tokenizerTrailingComma.getNextToken());
        assertEquals(",", tokenizerTrailingComma.getNextToken());

        final BSPLTokenizer tokenizerLeadingSpace = new BSPLTokenizer(keyStringLeadingSpace);
        assertEquals("key", tokenizerLeadingSpace.getNextToken());

        final BSPLTokenizer tokenizerTrailingSpace = new BSPLTokenizer(keyStringTrailingSpace);
        assertEquals("key", tokenizerTrailingSpace.getNextToken());

        final BSPLTokenizer tokenizerLeadingCommaTrailingComma =
            new BSPLTokenizer(keyStringLeadingCommaTrailingComma);
        assertEquals(",", tokenizerLeadingCommaTrailingComma.getNextToken());
        assertEquals("key", tokenizerLeadingCommaTrailingComma.getNextToken());
        assertEquals(",", tokenizerLeadingCommaTrailingComma.getNextToken());
    }

    @Test
    public void getNextTokenNewLines() {
        // Test the getNextToken method with new lines
        final String keyString = "key\nkey";

        final BSPLTokenizer tokenizer = new BSPLTokenizer(keyString);
        assertEquals("key", tokenizer.getNextToken());
        assertEquals("key", tokenizer.getNextToken());
    }

    @Test
    public void eolCommentsGetIgnored() {
        // Test the getNextToken method with comments
        final String keyString = "key // this is a comment\nkey";
        final BSPLTokenizer tokenizer = new BSPLTokenizer(keyString);

        assertEquals("key", tokenizer.getNextToken());
        assertEquals("key", tokenizer.getNextToken());
    }

    @Test
    public void testTokenize() throws IOException, URISyntaxException {
        // Test the tokenize method
        URL resource = getClass().getClassLoader().getResource("example_logistics.bspl");
        assertNotNull(resource);
        Path path = Paths.get(resource.toURI());

        final String fileString = Files.readString(path);
        assertNotNull(fileString);
        final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);

        final List<BSPLToken> tokens = tokenizer.tokenize();

        System.out.println("Tokens:" + tokens);
    }

    @Test
    public void testIgnoreTabs() {
        // Test the getNextToken method with tabs
        final String stringWithTabs = "key\tkey";
        final BSPLTokenizer tokenizer = new BSPLTokenizer(stringWithTabs);
        assertEquals("key", tokenizer.getNextToken());
        assertEquals("key", tokenizer.getNextToken());
    }

    @Test
    public void testTokenizeEmptyProtocol() {
        final String emptyProtocol = "EmptyProtocol {}";
        final BSPLTokenizer tokenizer = new BSPLTokenizer(emptyProtocol);
        final List<BSPLToken> tokens = tokenizer.tokenize();
        assertEquals(3, tokens.size());
        assertTokenEquals(tokens.getFirst(), "EmptyProtocol", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(1), "{", BSPLTokenType.BRACE_OPEN);
        assertTokenEquals(tokens.get(2), "}", BSPLTokenType.BRACE_CLOSE);
    }

    @Test
    public void testTokenizeProtocolWithRoles() {
        final String protocolWithRoles = """
                                         ProtocolWithRoles {
                                         \troles Alice, Bob
                                         }""";
        final BSPLTokenizer tokenizer = new BSPLTokenizer(protocolWithRoles);
        final List<BSPLToken> tokens = tokenizer.tokenize();
        assertEquals(7, tokens.size());
        assertTokenEquals(tokens.getFirst(), "ProtocolWithRoles", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(1), "{", BSPLTokenType.BRACE_OPEN);
        assertTokenEquals(tokens.get(2), "roles", BSPLTokenType.KEYWORD);
        assertTokenEquals(tokens.get(3), "Alice", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(4), ",", BSPLTokenType.COMMA);
        assertTokenEquals(tokens.get(5), "Bob", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(6), "}", BSPLTokenType.BRACE_CLOSE);
    }

    @Test
    public void testTokenizeTinyProtocol() {
        final String tinyProtocol = """
                                    TinyProtocol {
                                    \troles Alice, Bob
                                    \tparameters in x key,out y
                                    \tprivate z
                                    }""";
        final BSPLTokenizer tokenizer = new BSPLTokenizer(tinyProtocol);
        final List<BSPLToken> tokens = tokenizer.tokenize();
        assertEquals(16, tokens.size());
        assertTokenEquals(tokens.getFirst(), "TinyProtocol", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(1), "{", BSPLTokenType.BRACE_OPEN);
        assertTokenEquals(tokens.get(2), "roles", BSPLTokenType.KEYWORD);
        assertTokenEquals(tokens.get(3), "Alice", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(4), ",", BSPLTokenType.COMMA);
        assertTokenEquals(tokens.get(5), "Bob", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(6), "parameters", BSPLTokenType.KEYWORD);
        assertTokenEquals(tokens.get(7), "in", BSPLTokenType.ADORNMENT);
        assertTokenEquals(tokens.get(8), "x", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(9), "key", BSPLTokenType.KEY);
        assertTokenEquals(tokens.get(10), ",", BSPLTokenType.COMMA);
        assertTokenEquals(tokens.get(11), "out", BSPLTokenType.ADORNMENT);
        assertTokenEquals(tokens.get(12), "y", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(13), "private", BSPLTokenType.KEYWORD);
        assertTokenEquals(tokens.get(14), "z", BSPLTokenType.WORD);
        assertTokenEquals(tokens.get(15), "}", BSPLTokenType.BRACE_CLOSE);
    }

    private void assertTokenEquals(BSPLToken token, String value, BSPLTokenType type) {
        assertEquals(value, token.value());
        assertEquals(type, token.type());
    }
}