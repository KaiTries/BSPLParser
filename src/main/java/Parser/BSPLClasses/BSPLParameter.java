package Parser.BSPLClasses;

import org.jetbrains.annotations.NotNull;

public record BSPLParameter(String adornment, String name, boolean key) {


    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(adornment).append(" ").append(name);
        if (key) {
            sb.append(" key");
        }
        return sb.toString();
    }
}
