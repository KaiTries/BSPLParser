package Parser.BSPLClasses;

import org.jetbrains.annotations.NotNull;

public record BSPLRole(String role) {

    @Override
    @NotNull
    public String toString() {
        return role;
    }
}
