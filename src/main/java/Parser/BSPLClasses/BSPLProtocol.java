package Parser.BSPLClasses;

import org.jetbrains.annotations.NotNull;

public record BSPLProtocol(String name, java.util.List<BSPLRole> roles,
                           java.util.List<BSPLParameter> parameters,
                           java.util.List<BSPLPrivateParameters> privateParameters,
                           java.util.List<Parser.BSPLClasses.Reference.BSPLReference> references) {



    @Override
    @NotNull
    public String toString() {
        return  "\n" + name +" {\n" +
                "\troles " + getRolesString() +
                "\tparameters " + getParametersString() +
                "\tprivate " + getPrivateParametersString() +
                "\n\t" + getReferencesString() +
                "}\n";
    }

    private String getRolesString() {
        StringBuilder sb = new StringBuilder();
        for (BSPLRole role : roles) {
            sb.append(role.role()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        sb.append('\n');
        return sb.toString();
    }

    private String getParametersString() {
        StringBuilder sb = new StringBuilder();
        for (BSPLParameter parameter : parameters) {
            sb.append(parameter.toString());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        sb.append('\n');
        return sb.toString();
    }

    private String getPrivateParametersString() {
        StringBuilder sb = new StringBuilder();
        for (BSPLPrivateParameters privateParameter : privateParameters) {
            sb.append(privateParameter.name()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        sb.append('\n');
        return sb.toString();
    }

    private String getReferencesString() {
        StringBuilder sb = new StringBuilder();
        for (Parser.BSPLClasses.Reference.BSPLReference reference : references) {
            sb.append(reference.toString()).append("\n\t");
        }
        sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        sb.append('\n');
        return sb.toString();
    }
}
