import Parser.BSPLClasses.BSPLParameter;
import Parser.BSPLClasses.BSPLPrivateParameters;
import Parser.BSPLClasses.BSPLProtocol;
import Parser.BSPLClasses.BSPLRole;
import Parser.BSPLParser;
import Parser.util.ParserException;
import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenizer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

  @Test
  public void testEmptyProtocol() {
    final String fileString = "EmptyProtocol {}";

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());

    assertThrows(ParserException.class, parser::parse);
  }

  @Test
  public void testProtocolWithNoRoles() {
    final String fileString = "ProtocolWithNoRoles { parameters in x key, out y }";

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());

    assertThrows(ParserException.class, parser::parse);
  }

  @Test
  public void testProtocolWithNoParameters() {
    final String fileString = "ProtocolWithNoParameters { roles Alice, Bob }";

    final BSPLTokenizer tokenizer = new BSPLTokenizer(fileString);
    final BSPLParser parser = new BSPLParser(tokenizer.tokenize());

    assertThrows(ParserException.class, parser::parse);
  }

  @Test
  public void testTinyProtocol() {
    final String tinyProtocol = """
                                TinyProtocol {
                                \troles Alice, Bob
                                \tparameters in x key,out y
                                \tprivate z
                                }""";

    final BSPLProtocol expected = new BSPLProtocol("TinyProtocol",
        List.of(new BSPLRole("Alice"), new BSPLRole("Bob")),
        List.of(
            new BSPLParameter("in", "x", true),
            new BSPLParameter("out", "y", false)
        ),
        List.of(new BSPLPrivateParameters("z")),
        List.of()
    );
    final BSPLTokenizer tokenizer = new BSPLTokenizer(tinyProtocol);
    final List<BSPLToken> tokens = tokenizer.tokenize();
    final BSPLParser parser = new BSPLParser(tokens);
    final List<BSPLProtocol> protocols = parser.parse();

    BSPLProtocol tinyProtocolObj = protocols.getFirst();
    assertNotNull(tinyProtocolObj);
    assertEquals(expected, tinyProtocolObj);
  }

  @Test
  public void testParseFromFile() throws IOException, URISyntaxException {
    // Test the parseFromFile method
    final String pathString = "src/test/resources/example_logistics.bspl";
    final List<BSPLProtocol> protocols = BSPLParser.parseFromFile(pathString);
    System.out.println(protocols);
  }
}
