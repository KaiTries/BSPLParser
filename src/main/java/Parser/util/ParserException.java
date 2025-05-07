package Parser.util;

import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenType;

public class ParserException extends RuntimeException {
    public ParserException(BSPLToken token) {
        super("Unexpected token: " + token);
    }
    public ParserException(String message) {
        super(message);
    }
    public ParserException(BSPLToken token, BSPLTokenType expected) {
        super("Unexpected token: " + token + ", expected: " + expected);
    }
    public ParserException(BSPLToken token, BSPLTokenType expected, String expectedValue) {
        super("Unexpected token: " + token + ", expected: " + expected + ", value: " + expectedValue);
    }
}
