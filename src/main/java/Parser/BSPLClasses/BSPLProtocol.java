package Parser.BSPLClasses;

import Parser.BSPLClasses.Reference.BSPLReference;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class BSPLProtocol {
    private final String name;
    private final List<BSPLRole> roles;
    private final List<BSPLParameter> parameters;
    private final List<BSPLPrivateParameters> privateParameters;
    private final List<BSPLReference> references;

    public BSPLProtocol(String name, List<BSPLRole> roles,
                        List<BSPLParameter> parameters,
                        List<BSPLPrivateParameters> privateParameters,
                        List<BSPLReference> references) {
        this.name = name;
        this.roles = roles;
        this.parameters = parameters;
        this.privateParameters = privateParameters;
        this.references = references;
    }


    @Override
    @NotNull
    public String toString() {
        return "\n" + name + " {\n" +
            "\t" + createStringFromBSPLList("roles", roles, ", ") +
            "\t" + createStringFromBSPLList("parameters", parameters, ", ") +
            "\t" + createStringFromBSPLList("private", privateParameters, ", ") +
            "\n" +
            "\t" + createStringFromBSPLList(null, references, "\n\t") +
            "}";
    }

    private <T> String createStringFromBSPLList(String prefix, List<T> list, String separator) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            return "";
        }
        if (prefix != null) {
            sb.append(prefix).append(" ");
        }

        for (T item : list) {
            sb.append(item.toString()).append(separator);
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append('\n');
        return sb.toString();
    }

    public String name() {
        return name;
    }

    public List<BSPLRole> roles() {
        return roles;
    }

    public List<BSPLParameter> parameters() {
        return parameters;
    }

    public List<BSPLPrivateParameters> privateParameters() {
        return privateParameters;
    }

    public List<BSPLReference> references() {
        return references;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (BSPLProtocol) obj;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.roles, that.roles) &&
            Objects.equals(this.parameters, that.parameters) &&
            Objects.equals(this.privateParameters, that.privateParameters) &&
            Objects.equals(this.references, that.references);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roles, parameters, privateParameters, references);
    }

}
