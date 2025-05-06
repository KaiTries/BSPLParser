package tokens;

import java.util.Objects;

public final class BSPLRole {
    private final BSPLName name;

    public BSPLRole(BSPLName name) {
        this.name = name;
    }

    public BSPLName name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (BSPLRole) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BSPLRole[" +
            "name=" + name + ']';
    }
}
