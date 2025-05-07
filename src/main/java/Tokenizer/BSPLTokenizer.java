package Tokenizer;


import java.util.ArrayList;
import java.util.List;

/**
 * Reads a Strings and returns tokens. Tokens correspond to BSPL specification.
 */
public class BSPLTokenizer {
    private final String srcString;
    private int idx = 0;

    private static final List<Character> DELIMITERS = List.of(',', '{', '}', '[', ']', ':','(', ')');
    private static final List<String> COMMENTS = List.of("//", "#");

    public BSPLTokenizer(String srcString) {
        this.srcString = srcString;
    }

    public List<BSPLToken> tokenize() {
        List<BSPLToken> tokens = new ArrayList<>();
        String token;
        while ((token = getNextToken()) != null) {
            BSPLTokenType type = getTokenType(token);
            tokens.add(new BSPLToken(token, type));
        }
        return tokens;
    }

    private BSPLTokenType getTokenType(String token) {
        return switch(token) {
            case "{" -> BSPLTokenType.BRACE_OPEN;
            case "}" -> BSPLTokenType.BRACE_CLOSE;
            case "," -> BSPLTokenType.COMMA;
            case "[" -> BSPLTokenType.BRACKET_OPEN;
            case "]" -> BSPLTokenType.BRACKET_CLOSE;
            case ":" -> BSPLTokenType.COLON;
            case "(" -> BSPLTokenType.PAREN_OPEN;
            case ")" -> BSPLTokenType.PAREN_CLOSE;
            case "->", "→", "↦" -> BSPLTokenType.ARROW;
            case "out", "in", "nil", "any", "opt" -> BSPLTokenType.ADORNMENT;
            case "roles", "parameters", "private" -> BSPLTokenType.KEYWORD;
            case "key" -> BSPLTokenType.KEY;
            default -> BSPLTokenType.WORD;
        };
    }


    public String getNextToken() {
        skipWhitespace();
        if (this.idx >= this.srcString.length()) {
            return null;
        }

        char c = this.srcString.charAt(this.idx);

        if (DELIMITERS.contains(c)) {
            this.idx++;
            return String.valueOf(c);
        }

        StringBuilder token = new StringBuilder();
        while (this.idx < this.srcString.length() &&
            !Character.isWhitespace(srcString.charAt(this.idx)) &&
            !DELIMITERS.contains(srcString.charAt(this.idx))) {
            token.append(srcString.charAt(this.idx));
            this.idx++;
        }

        if (token.isEmpty()) {
            return null;
        }

        // Check for comments
        for (String comment : COMMENTS) {
            if (token.toString().startsWith(comment)) {
                // skip to the end of the line
                while (this.idx < this.srcString.length() && srcString.charAt(this.idx) != '\n') {
                    this.idx++;
                }
                // skip the newline character
                if (this.idx < this.srcString.length() && srcString.charAt(this.idx) == '\n') {
                    this.idx++;
                }
                return getNextToken(); // recursively call to get the next token
            }
        }
        return token.toString();
    }


    private void skipWhitespace() {
        while (this.idx < this.srcString.length() && (
            srcString.charAt(this.idx) == ' ' || srcString.charAt(this.idx) == '\n'
            || srcString.charAt(this.idx) == '\t')) {
            this.idx++;
        }
    }
}
