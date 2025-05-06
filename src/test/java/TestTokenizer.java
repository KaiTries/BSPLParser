import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}