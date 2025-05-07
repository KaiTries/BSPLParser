package Tokenizer;


import java.util.ArrayList;
import java.util.List;

/**
 * Reads a Strings and returns tokens. Tokens correspond to BSPL specification.
 */
public class BSPLTokenizer {
    private final String srcString;
    private int idx = 0;

    private static final List<String> KEYWORDS = List.of(
        "roles", "parameters", "private"
    );
    private static final List<Character> DELIMITERS = List.of(',', '{', '}', '[', ']', ':');
    private static final List<String> ADORNMENTS = List.of(
        "out", "in", "nil", "any", "opt"
    );
    private static final List<String> ARROW = List.of("->", "→", "↦");

    private static final List<String> COMMENTS = List.of("//", "#");

    private static final String KEY = "key";

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
        if (DELIMITERS.contains(token.charAt(0))) {
            return switch (token) {
                case "{" -> BSPLTokenType.BRACE_OPEN;
                case "}" -> BSPLTokenType.BRACE_CLOSE;
                case "," -> BSPLTokenType.COMMA;
                case "[" -> BSPLTokenType.BRACKET_OPEN;
                case "]" -> BSPLTokenType.BRACKET_CLOSE;
                case ":" -> BSPLTokenType.COLON;
                default -> BSPLTokenType.DELIMITER;
            };
        }

        if (KEYWORDS.contains(token)) {
            return BSPLTokenType.KEYWORD;
        }

        if (token.equals(KEY)) {
            return BSPLTokenType.KEY;
        }

        if (ADORNMENTS.contains(token)) {
            return BSPLTokenType.ADORNMENT;
        }

        if (ARROW.contains(token)) {
            return BSPLTokenType.ARROW;
        }

        return BSPLTokenType.WORD;
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

    private String peakNextToken() {
        final int oldIdx = this.idx;
        final String token = getNextToken();
        this.idx = oldIdx; // reset the index
        return token;
    }


    private void skipWhitespace() {
        while (this.idx < this.srcString.length() && (
            srcString.charAt(this.idx) == ' ' || srcString.charAt(this.idx) == '\n'
            || srcString.charAt(this.idx) == '\t')) {
            this.idx++;
        }
    }
}
