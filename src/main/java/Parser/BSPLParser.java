package Parser;

import static Tokenizer.BSPLTokenType.DELIMITER;
import static Tokenizer.BSPLTokenType.WORD;

import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenType;
import java.util.List;
import java.util.Objects;
import tokens.BSPLProtocol;

public class BSPLParser {
    private final List<BSPLToken> tokens;
    private int currentTokenIndex = 0;


    private final List<BSPLProtocol> protocols;

    public BSPLParser(List<BSPLToken> tokens) {
        this.tokens = tokens;
        this.protocols = List.of();
    }

    public void parse() {
        BSPLToken protocolName = getNextToken();
        assertTokenType(protocolName, WORD);
        assertTokenTypeAndValue(getNextToken(), DELIMITER, "{");
        System.out.println("Protocol Name: " + protocolName.value());

        while (currentTokenIndex < tokens.size()) {
            BSPLToken token = getNextToken();


            if (token == null || (currentTokenIndex == tokens.size() &&
                !Objects.equals(token.value(), "}"))) {
                throw new IllegalArgumentException("Unexpected end of input." + token);
            }
            if (token.type() == DELIMITER && token.value().equals("}")) {
                System.out.println("Token: " + token);
                break;
            }
            System.out.println("Token: " + token);
        }


    }

    private void assertTokenType(BSPLToken token, BSPLTokenType expectedType) {
        if (token == null) {
            throw new IllegalArgumentException("Expected token type: " + expectedType + " but got null");
        }
        if (token.type() != expectedType) {
            throw new IllegalArgumentException("Expected token type: " + expectedType + " but got: " + token);
        }
    }

    private void assertTokenTypeAndValue(BSPLToken token, BSPLTokenType expectedType, String expectedValue) {
        if (token == null) {
            throw new IllegalArgumentException("Expected token type: " + expectedType + " and value: " + expectedValue + " but got null");
        }
        if (token.type() != expectedType || !Objects.equals(token.value(), expectedValue)) {
            throw new IllegalArgumentException("Expected token type: " + expectedType + " and value: " + expectedValue + " but got: " + token);
        }
    }


    private BSPLToken getNextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        }
        return null;
    }

    private BSPLToken peekNextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }
}
