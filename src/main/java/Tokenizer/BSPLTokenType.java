package Tokenizer;

public class BSPLTokenType {
    public static final BSPLTokenType KEYWORD = new BSPLTokenType("KEYWORD");

    public static final BSPLTokenType DELIMITER = new BSPLTokenType("DELIMITER");
    public static final BSPLTokenType COMMA = new BSPLTokenType("COMMA");
    public static final BSPLTokenType COLON = new BSPLTokenType("COLON");
    public static final BSPLTokenType BRACE_OPEN = new BSPLTokenType("BRACE_OPEN");
    public static final BSPLTokenType BRACE_CLOSE = new BSPLTokenType("BRACE_CLOSE");
    public static final BSPLTokenType BRACKET_OPEN = new BSPLTokenType("BRACKET_OPEN");
    public static final BSPLTokenType BRACKET_CLOSE = new BSPLTokenType("BRACKET_CLOSE");
    public static final BSPLTokenType PAREN_OPEN = new BSPLTokenType("PAREN_OPEN");
    public static final BSPLTokenType PAREN_CLOSE = new BSPLTokenType("PAREN_CLOSE");

    public static final BSPLTokenType WORD = new BSPLTokenType("WORD");
    public static final BSPLTokenType KEY = new BSPLTokenType("KEY");
    public static final BSPLTokenType ADORNMENT = new BSPLTokenType("ADORNMENT");
    public static final BSPLTokenType ARROW = new BSPLTokenType("ARROW");

    private final String name;

    private BSPLTokenType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BSPLTokenType that = (BSPLTokenType) obj;
        return name.equals(that.name);
    }
}
