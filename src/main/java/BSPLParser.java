import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tokens.BSPLAdornment;
import tokens.BSPLDocument;
import tokens.BSPLKey;
import tokens.BSPLMessage;
import tokens.BSPLName;
import tokens.BSPLParameter;
import tokens.BSPLPrivateParameter;
import tokens.BSPLProtocol;
import tokens.BSPLRole;

public class BSPLParser {
    private final String PROTOCOL_ROLES_KEYWORD = "roles";
    private final String PROTOCOL_PARAMETERS_KEYWORD = "parameters";
    private final String PROTOCOL_MESSAGE_ARROW = "→";
    private final String PROTOCOL_MESSAGE_ARROW_ALT = "->";


    private final String srcString;
    private BSPLDocument document;
    private BSPLProtocol protocol;
    private List<BSPLRole> roles;
    private List<BSPLParameter> parameters;
    private List<BSPLPrivateParameter> privateParemeters;
    private List<BSPLMessage> messages;
    private int idx;


    public BSPLParser(String srcString) {
        this.srcString = srcString;
        this.idx = 0;
    }

    public BSPLDocument parse() throws IllegalArgumentException {
        final BSPLProtocol protocol = this.parseProtocol();
        this.protocol = protocol;
        this.document = new BSPLDocument(protocol);
        return this.document;
    }

    private BSPLProtocol parseProtocol() throws IllegalArgumentException {
        // should always be at the start of a valid protocol
        // so spacename = /[ \w@-]+/ gives us protocol name
        // and then we can parse the rest of the protocol

        // Read the protocol name
        final String protocolName = this.getNextToken();
        final BSPLName name = new BSPLName(protocolName);
        System.out.println("Protocol name: " + protocolName);

        // Read the protocol body
        this.roles = this.parseRoles();
        this.parameters = this.parseParameters();
        if (Objects.equals(this.peakNextToken(), "private")) {
            this.privateParemeters = this.parsePrivateParameters();
        }
        this.messages = this.parseMessages();


        return new BSPLProtocol(name, roles, parameters, messages, privateParemeters);
    }

    private List<BSPLRole> parseRoles() {
        // should always be at the start of the roles section
        final String token = this.getNextToken();
        if (!Objects.equals(token, "{")) {
            throw new IllegalArgumentException("Invalid start roles section: " + srcString);
        }

        final String rolesKeyword = this.getNextToken();
        if (!Objects.equals(rolesKeyword, PROTOCOL_ROLES_KEYWORD)) {
            throw new IllegalArgumentException("Invalid roles section: " + srcString);
        }

        List<BSPLRole> roles = new ArrayList<>();
        String rolesToken = this.peakNextToken();
        while (!Objects.equals(rolesToken, PROTOCOL_PARAMETERS_KEYWORD)) {
            rolesToken = this.getNextToken();
            if (!Objects.equals(rolesToken, ",")) {
                roles.add(new BSPLRole(new BSPLName(rolesToken)));
            }
            rolesToken = this.peakNextToken();

            if (rolesToken == null) {
                throw new IllegalArgumentException("Invalid end of roles section: " + srcString);
            }
        }
        System.out.println("Roles: " + roles);
        return roles;
    }

    private List<BSPLParameter> parseParameters() {
        // should always be at the start of the parameters section
        String token = this.getNextToken();
        if (!Objects.equals(token, PROTOCOL_PARAMETERS_KEYWORD)) {
            throw new IllegalArgumentException("Invalid start parameters section: " + srcString);
        }

        final List<BSPLParameter> parameters = new ArrayList<>();
        token = this.peakNextToken();


        while (BSPLAdornment.isValid(token)) {
            token = this.getNextToken(); // consume the adornment
            final BSPLAdornment adornment = BSPLAdornment.fromString(token);


            token = this.getNextToken(); // get the name
            if (token == null) {
                throw new IllegalArgumentException("Invalid parameter section: " + srcString);
            }
            final BSPLName name = new BSPLName(token);

            // check if next token is key, comma or end of section
            token = this.peakNextToken();
            if (Objects.equals(token, ",")) {
                token = this.getNextToken();
                final BSPLKey key = new BSPLKey(false);
                parameters.add(new BSPLParameter(adornment, name, key));
                token = this.peakNextToken(); // check if next token is key or end of section
            } else if (Objects.equals(token, "key")) {
                token = this.getNextToken(); // consume the key
                assert Objects.equals(token, "key");
                final BSPLKey key = new BSPLKey(true);
                parameters.add(new BSPLParameter(adornment, name, key));
                token = this.peakNextToken(); // check if next token is comma or end of section

                if (Objects.equals(token, ",")) {
                    this.getNextToken();
                    token = this.peakNextToken();
                }
            }
        }

        System.out.println("Parameters: " + parameters);
        return parameters;
    }

    private List<BSPLPrivateParameter> parsePrivateParameters() {
        String token = this.getNextToken();
        if (!Objects.equals(token, "private")) {
            throw new IllegalArgumentException("Invalid start private parameters section: " + srcString);
        }


        final List<BSPLPrivateParameter> privateParameters = new ArrayList<>();
        token = this.getNextToken();
        privateParameters.add(new BSPLPrivateParameter(token));
        while(Objects.equals(this.peakNextToken(), ",")) {
            this.getNextToken(); // consume the comma
            token = this.getNextToken();
            privateParameters.add(new BSPLPrivateParameter(token));
        }


        System.out.println("Private parameters: " + privateParameters);
        return privateParameters;
    }

    private List<BSPLMessage> parseMessages() {
      final List<BSPLMessage> messages = new ArrayList<>();
      while (this.peakNextToken() != null && !this.peakNextToken().equals("}")) {
          messages.add(this.parseMessage());
      }
      System.out.println("Messages: " + messages);
      return messages;
    }

    private BSPLMessage parseMessage() {
        // should always be at the start of the messages section
        final String sender = this.getNextToken();
        // Sender must be a role
        if (!isValidRole(sender)) {
            throw new IllegalArgumentException("Role does not exist: " + sender);
        }
        String check = this.getNextToken();
        if (!Objects.equals(check, PROTOCOL_MESSAGE_ARROW) &&
            !Objects.equals(check, PROTOCOL_MESSAGE_ARROW_ALT)
        ) {
            throw new IllegalArgumentException("Invalid start messages section: " + check);
        }

        // Receiver must be a role
        final String receiver = this.getNextToken();
        if (!isValidRole(receiver)) {
            throw new IllegalArgumentException("Role does not exist: " + receiver);
        }

        this.getNextToken(); // consume ":"

        final String messageName = this.getNextToken();
        if (messageName == null) {
            throw new IllegalArgumentException("Invalid message section: " + srcString);
        }

        // parse parameters
        final List<BSPLParameter> parameters = new ArrayList<>();
        String token = this.peakNextToken();
        if (!Objects.equals(token, "[")) {
          new BSPLMessage(new BSPLName(sender), new BSPLName(receiver),
              new BSPLName(messageName),parameters);
        }
        this.getNextToken(); // consume the "["
        this.peakNextToken();
        while (!Objects.equals(token, "]")) {
            token = this.getNextToken();
            if (BSPLAdornment.isValid(token)) {
                final BSPLAdornment adornment = BSPLAdornment.fromString(token);
                token = this.getNextToken(); // get the name
                if (token == null) {
                    throw new IllegalArgumentException("Invalid parameter section: " + srcString);
                }
                final BSPLName name = new BSPLName(token);
                token = this.peakNextToken(); // check if next token is key or end of section
                System.out.println("Token: " + token);
                if (Objects.equals(token, "key")) {
                    parameters.add(new BSPLParameter(adornment, name, new BSPLKey(true)));
                    this.getNextToken(); // consume the key
                    token = this.peakNextToken(); // check if next token is comma or end of section
                } else {
                    parameters.add(new BSPLParameter(adornment, name, null));
                }
                if (Objects.equals(token, ",")){
                  this.getNextToken();
                }
            } else {
              // if not valid throw exception
                throw new IllegalArgumentException("Invalid parameter section: " + token);
            }
            token = this.peakNextToken();
        }

        this.getNextToken(); // consume the "]"


        return new BSPLMessage(new BSPLName(sender), new BSPLName(receiver),
            new BSPLName(messageName), parameters);
    }

    private boolean isValidRole(String token) {
        return this.roles.stream().anyMatch(role -> Objects.equals(role.name().name(), token));
    }

    public String getSrcString() {
        return srcString;
    }

    private static final List<Character> delimiters = List.of(',', '{', '}', '[', ']', ':');

    String getNextToken() {
        skipWhitespace();
        if (this.idx >= this.srcString.length()) {
            return null;
        }

        char c = this.srcString.charAt(this.idx);

        if (delimiters.contains(c)) {
            this.idx++;
            return String.valueOf(c);
        }

        StringBuilder token = new StringBuilder();
        while (this.idx < this.srcString.length() &&
            !Character.isWhitespace(srcString.charAt(this.idx)) &&
            !delimiters.contains(srcString.charAt(this.idx))) {
            token.append(srcString.charAt(this.idx));
            this.idx++;
        }

        if (token.isEmpty()) {
            return null;
        }
        return token.toString();
    }

    private String peakNextToken() {
        final int oldIdx = this.idx;
        final String token = getNextToken();
        this.idx = oldIdx; // reset the index
        return token;
    }


    private void skipWhitespace() {
        while (this.idx < this.srcString.length() && (
                srcString.charAt(this.idx) == ' ' ||
                srcString.charAt(this.idx) == '\n'))
        {
            this.idx++;
        }
    }

    public void printDocument() {
        System.out.println("Document: " + this.document);
    }
}
