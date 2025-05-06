import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Parser.BSPLParser;
import Tokenizer.BSPLTokenizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class TestParser {


  @Test
  public void testReadFile() throws IOException {
    // Test the parseFile method
    final String absPath = "/Users/kaischultz/github/BSPLParser/src/test/resources" +
        "/example_logistics" +
        ".bspl";
    final String fileString = Files.readString(Path.of(absPath));
    assertNotNull(fileString);

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());
    parser.parse();



  }
}
