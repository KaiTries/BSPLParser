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
import static Tokenizer.BSPLTokenType.PAREN_CLOSE;
import static Tokenizer.BSPLTokenType.PAREN_OPEN;
import static Tokenizer.BSPLTokenType.WORD;

import Parser.BSPLClasses.BSPLParameter;
import Parser.BSPLClasses.BSPLPrivateParameters;
import Parser.BSPLClasses.BSPLProtocol;
import Parser.BSPLClasses.Reference.BSPLMessage;
import Parser.BSPLClasses.Reference.BSPLProtocolReference;
import Parser.BSPLClasses.Reference.BSPLReference;
import Parser.BSPLClasses.BSPLRole;
import Parser.util.ParserException;
import Tokenizer.BSPLToken;
import Tokenizer.BSPLTokenType;
import Tokenizer.BSPLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BSPLParser {
    private final List<BSPLToken> tokens;
    private int currentTokenIndex = 0;

    private static final String ROLES = "roles";
    private static final String PARAMETERS = "parameters";
    private static final String PRIVATE = "private";


    public static List<BSPLProtocol> parseFromFile(String filePath) throws IOException {
        BSPLTokenizer tokenizer = BSPLTokenizer.fromPath(filePath);
        List<BSPLToken> tokens = tokenizer.tokenize();
        BSPLParser parser = new BSPLParser(tokens);
        return parser.parse();
    }

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
        if (notKeyword(peekNextToken(), ROLES)) {
            throw new ParserException("Expected mandatory Roles section");
        }
        assertKeyword(getNextToken(),ROLES);

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
        if (notKeyword(peekNextToken(), PARAMETERS)) {
            throw new ParserException("Expected mandatory parameters section");
        }
        assertKeyword(getNextToken(), PARAMETERS);

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
        if (notKeyword(peekNextToken(), PRIVATE)) {
            return List.of();
        }
        assertKeyword(getNextToken(), PRIVATE);
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
        } else if (checkTokenType(token, PAREN_OPEN)) { // in a reference
            token = getNextToken();
            assertTokenType(token, PAREN_OPEN);

            List<BSPLRole> roles = new ArrayList<>();
            List<BSPLParameter> parameters = new ArrayList<>();

            token = peekNextToken();
            while (!checkTokenType(token, PAREN_CLOSE)) {
                if (checkTokenType(token, WORD)) {
                    roles.add(new BSPLRole(getNextToken().value()));
                } else if (checkTokenType(token, ADORNMENT)) {
                    parameters.add(parseParameter());
                } else {
                    throw new ParserException(token);
                }
                if (checkTokenType(peekNextToken(), COMMA)) {
                    getNextToken();
                }
                token = peekNextToken();
            }
            assertTokenType(getNextToken(), PAREN_CLOSE);

            return new BSPLProtocolReference(referenceNameOrSender.value(), roles, parameters);
        } else {
            throw new ParserException(
                "Expected token type: " + ARROW + " or " + DELIMITER + " but got: " + token);
        }
    }


    private boolean checkTokenType(BSPLToken token, BSPLTokenType expectedType) {
        return token != null && token.type() == expectedType && token.value() != null;
    }

    private void assertTokenType(BSPLToken token, BSPLTokenType expectedType) {
        if(!checkTokenType(token, expectedType)) {
            throw new ParserException(token, expectedType);
        }
    }

    private boolean notKeyword(BSPLToken token, String keyword) {
        return token == null || token.type() != KEYWORD || !Objects.equals(token.value(), keyword);
    }

    private void assertKeyword(BSPLToken token, String expectedKeyword) {
        if (notKeyword(token, expectedKeyword)) {
            throw new ParserException("Expected keyword: " + expectedKeyword + " but got: " + token);
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
