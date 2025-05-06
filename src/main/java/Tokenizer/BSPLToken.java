package Tokenizer;

public record BSPLToken(String value, BSPLTokenType type) {

    @Override
    public String toString() {
        return "BSPLToken { " +
            "type='" + type + '\'' +
            ", value='" + value +
            "'}";
    }
}
