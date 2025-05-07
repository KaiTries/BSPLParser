package Parser;

import static Tokenizer.BSPLTokenType.ADORNMENT;
import static Tokenizer.BSPLTokenType.ARROW;
import static Tokenizer.BSPLTokenType.BRACE_CLOSE;
import static Tokenizer.BSPLTokenType.BRACE_OPEN;
import static Tokenizer.BSPLTokenType.BRACKET_CLOSE;
import static Tokenizer.BSPLTokenType.BRACKET_OPEN;
import static Tokenizer.BSPLTokenType.COLON;
import static Tokenizer.BSPLTokenType.COMMA;
import static Tokenizer.BSPLTokenType.DELIMITER;
import static Tokenizer.BSPLTokenType.KEY;
import static Tokenizer.BSPLTokenType.KEYWORD;
import static Tokenizer.BSPLTokenType.WORD;

import Parser.BSPLClasses.BSPLParameter;
import Parser.BSPLClasses.BSPLPrivateParameters;
import Parser.BSPLClasses.BSPLProtocol;
import Parser.BSPLClasses.Reference.BSPLMessage;
import Parser.BSPLClasses.Reference.BSPLReference;
import Parser.BSPLClasses.BSPLRole;
import Parser.util.NotImplementedException;
import Parser.util.ParserException;
import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BSPLParser {
    private final List<BSPLToken> tokens;
    private int currentTokenIndex = 0;



    public BSPLParser(List<BSPLToken> tokens) {
        this.tokens = tokens;
    }

    public List<BSPLProtocol> parse() {
        List<BSPLProtocol> protocols = new ArrayList<>();

        while (currentTokenIndex < tokens.size()) {
            BSPLToken token = peekNextToken();
            if (checkTokenType(token, WORD)) {
                protocols.add(parseProtocol());
            } else {
                throw new ParserException(token);
            }
        }
        return protocols;
    }

    private BSPLProtocol parseProtocol() {
        BSPLToken protocolName = getNextToken();
        assertTokenType(protocolName, WORD);
        assertTokenType(getNextToken(), BRACE_OPEN);

        final List<BSPLRole> roles = this.parseRoles();
        final List<BSPLParameter> parameters = this.parseParameters();
        final List<BSPLPrivateParameters> privateParameters = this.parsePrivateParameters();
        final List<BSPLReference> references = this.parseReferences();
        assertTokenType(getNextToken(), BRACE_CLOSE);

        return new BSPLProtocol(protocolName.value(), roles, parameters, privateParameters,
            references);
    }

    private List<BSPLRole> parseRoles() {
        if (!checkTokenTypeAndValue(peekNextToken(), KEYWORD, "roles")) {
            return List.of();
        }
        assertTokenTypeAndValue(getNextToken(), KEYWORD, "roles");

        BSPLToken token = peekNextToken();

        final List<BSPLRole> roles = new ArrayList<>();

        while (!checkTokenType(token, KEYWORD)) {
            token = getNextToken();
            assertTokenType(token, WORD);
            roles.add(new BSPLRole(token.value()));
            token = peekNextToken();
            if (checkTokenType(token, COMMA)) {
                getNextToken();
                token = peekNextToken();
            } else {
                break; // we know roles are over if the last word is not followed by a delimiter
            }
        }
        return roles;
    }

    private BSPLParameter parseParameter() {
        BSPLToken adornment = getNextToken();
        assertTokenType(adornment, ADORNMENT);
        BSPLToken parameter = getNextToken();
        assertTokenType(parameter, WORD);
        boolean key = false;
        if (checkTokenType(peekNextToken(), KEY)) {
            key = true;
            getNextToken();
        }
        return new BSPLParameter(adornment.value(), parameter.value(), key);
    }

    private List<BSPLParameter> parseParameters() {
        if (!checkTokenTypeAndValue(peekNextToken(), KEYWORD, "parameters")) {
            return List.of();
        }
        assertTokenTypeAndValue(getNextToken(), KEYWORD, "parameters");

        BSPLToken token = peekNextToken();

        final List<BSPLParameter> parameters = new ArrayList<>();

        while (!checkTokenType(token, KEYWORD)) {
            parameters.add(parseParameter());
            token = peekNextToken();
            if (checkTokenType(token, COMMA)) {
                getNextToken();
                token = peekNextToken();
            } else {
                break; // we know parameters are over if the last word is not followed by a
                // delimiter
            }
        }
        return parameters;
    }

    private List<BSPLPrivateParameters> parsePrivateParameters() {
        if (!checkTokenTypeAndValue(peekNextToken(), KEYWORD, "private")) {
            return List.of();
        }
        assertTokenTypeAndValue(getNextToken(), KEYWORD, "private");
        final List<BSPLPrivateParameters> privateParameters = new ArrayList<>();

        BSPLToken token = peekNextToken();
        while (checkTokenType(token, WORD)) {
            token = getNextToken();
            assertTokenType(token, WORD);

            privateParameters.add(new BSPLPrivateParameters(token.value()));
            token = peekNextToken();
            if (checkTokenType(token, COMMA)) {
                getNextToken();
                token = peekNextToken();
            } else {
                break; // we know private params are over if the last word is not followed by a
                // delimiter
            }
        }
        return privateParameters;
    }

    private List<BSPLReference> parseReferences() {
        final List<BSPLReference> references = new ArrayList<>();
        BSPLReference nextReference = parseReference();
        while (nextReference != null) {
            references.add(nextReference);
            nextReference = parseReference();
        }
        return references;
    }

    private BSPLReference parseReference() {
        if (!checkTokenType(peekNextToken(), WORD)) {
            return null;
        }
        BSPLToken referenceNameOrSender = getNextToken();
        assertTokenType(referenceNameOrSender, WORD);


        // its either a reference or a message
        BSPLToken token = peekNextToken();
        if (checkTokenType(token, ARROW)) { // in a message
            getNextToken(); // consume the arrow
            BSPLToken recipient = getNextToken();
            assertTokenType(recipient, WORD);
            assertTokenType(getNextToken(), COLON);

            BSPLToken messageName = getNextToken();
            assertTokenType(messageName, WORD);

            // parse message body for parameters [ param1, param2, ...]
            assertTokenType(getNextToken(), BRACKET_OPEN);
            token = peekNextToken();
            List<BSPLParameter> parameters = new ArrayList<>();
            while (!checkTokenType(token, BRACKET_CLOSE)) {
                parameters.add(parseParameter());
                token = getNextToken();
            }
            return new BSPLMessage(referenceNameOrSender.value(), recipient.value(),
                messageName.value(),parameters);
        } else if (checkTokenTypeAndValue(token, DELIMITER, "(")) { // in a reference
            throw new NotImplementedException("BSPL references not yet implemented");
        } else {
            throw new ParserException(
                "Expected token type: " + ARROW + " or " + DELIMITER + " but got: " + token);
        }
    }


    private boolean checkTokenType(BSPLToken token, BSPLTokenType expectedType) {
        return token != null && token.type() == expectedType;
    }

    private boolean checkTokenTypeAndValue(BSPLToken token, BSPLTokenType expectedType,
                                           String expectedValue) {
        return token != null && token.type() == expectedType &&
            Objects.equals(token.value(), expectedValue);
    }

    private void assertTokenType(BSPLToken token, BSPLTokenType expectedType) {
        if (token == null || token.type() != expectedType) {
            throw new ParserException(token, expectedType);
        }
        if (token.value() == null) {
            throw new ParserException("Expected token to have value but got null");
        }
    }

    private void assertTokenTypeAndValue(BSPLToken token, BSPLTokenType expectedType,
                                         String expectedValue) {
        if (token == null || token.type() != expectedType || !Objects.equals(token.value(),
            expectedValue)) {
            throw new ParserException(token, expectedType, expectedValue);
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
