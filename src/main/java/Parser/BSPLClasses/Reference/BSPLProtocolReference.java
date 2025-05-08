package Parser.BSPLClasses.Reference;

import Parser.BSPLClasses.BSPLParameter;
import Parser.BSPLClasses.BSPLRole;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record BSPLProtocolReference(String protocolName, List<BSPLRole> roles,
                                    List<BSPLParameter> parameters) implements BSPLReference {


    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocolName).append(" (");
        if (roles != null && !roles.isEmpty()) {
            for (BSPLRole role : roles) {
                sb.append(role.toString()).append(", ");
            }
        }

        if (roles != null && !roles.isEmpty() && (parameters == null || parameters.isEmpty())) {
            sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        }

        if (parameters != null && !parameters.isEmpty()) {
            for (BSPLParameter parameter : parameters) {
                sb.append(parameter.toString()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        }
        sb.append(")");
        return sb.toString();

    }
}
