package Tokenizer;

public class BSPLTokenType {
    public static final BSPLTokenType KEYWORD = new BSPLTokenType("KEYWORD");
    public static final BSPLTokenType DELIMITER = new BSPLTokenType("DELIMITER");
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
