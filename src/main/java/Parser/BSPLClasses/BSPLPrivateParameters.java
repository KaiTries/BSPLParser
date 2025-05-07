package Parser.BSPLClasses;

import org.jetbrains.annotations.NotNull;

public record BSPLPrivateParameters(String name) {


    @Override
    @NotNull
    public String toString() {
        return name;
    }
}
