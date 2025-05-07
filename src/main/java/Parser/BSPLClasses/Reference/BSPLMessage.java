package Parser.BSPLClasses.Reference;

import Parser.BSPLClasses.BSPLParameter;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record BSPLMessage(String sender, String recipient, String name,
                          List<BSPLParameter> parameters) implements BSPLReference {

    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sender).append(" -> ").append(recipient).append(": ").append(name).append(" [");
        if (parameters != null && !parameters.isEmpty()) {
            for (BSPLParameter parameter : parameters) {
                sb.append(parameter.toString()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
        }
        sb.append("]");
        return sb.toString();
    }
}
