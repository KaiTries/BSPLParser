import static org.junit.jupiter.api.Assertions.assertNotNull;

import Parser.BSPLClasses.BSPLProtocol;
import Parser.BSPLParser;
import Tokenizer.BSPLTokenizer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import org.junit.jupiter.api.Test;

public class TestParser {

  @Test
  public void testReadFileLogistics() throws IOException, URISyntaxException {
    // Test the parseFile method
    URL resource = getClass().getClassLoader().getResource("example_logistics.bspl");
    assertNotNull(resource);
    Path path = Paths.get(resource.toURI());
    final String fileString =
            Files.readString(path);

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());
    final List<BSPLProtocol> protocols = parser.parse();
    System.out.println(protocols);
  }
  @Test
  public void testReadFileExample() throws IOException, URISyntaxException {
    // Test the parseFile method
    URL resource = getClass().getClassLoader().getResource("example.bspl");
    assertNotNull(resource);
    Path path = Paths.get(resource.toURI());
    final String fileString =
        Files.readString(path);

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());
    final List<BSPLProtocol> protocols = parser.parse();
    System.out.println(protocols);
  }
  @Test
  public void testReadFileTwoProtocols() throws IOException, URISyntaxException {
    // Test the parseFile method
    URL resource = getClass().getClassLoader().getResource("example_two_protocols.bspl");
    assertNotNull(resource);
    Path path = Paths.get(resource.toURI());
    final String fileString =
        Files.readString(path);

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());
    final List<BSPLProtocol> protocols = parser.parse();
    System.out.println(protocols);
  }
}
